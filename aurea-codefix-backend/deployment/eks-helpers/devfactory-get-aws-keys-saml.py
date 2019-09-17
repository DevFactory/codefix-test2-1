#!/usr/bin/env python

"""
Gets temporary access/secret keys from aws using adfs authentication

Based on the script from https://goo.gl/Tz3I7C

The script roughly works like this:

- gets the auth code from adfs
- uses aws sts service to get a temporary key pair
- saves the temporary key pair in temporary script file
"""

import argparse
import base64
import getpass
import sys
import xml.etree.ElementTree as ET

import boto.sts
import requests
from bs4 import BeautifulSoup
from requests_ntlm import HttpNtlmAuth

AWS_ACCESS_KEY_ID='AWS_ACCESS_KEY_ID'
AWS_SECRET_ACCESS_KEY='AWS_SECRET_ACCESS_KEY'
AWS_SESSION_TOKEN='AWS_SESSION_TOKEN'

# defines our cli arguments
parser = argparse.ArgumentParser(description='Gets temporary keys from AWS STS utilizing devfactory ADFS credentials')

parser.add_argument('--login', type=str, help='specify login e.g domain\\username, requires --password', required=True)
parser.add_argument('--password', type=str, help='specify password. requires --login', required=True)
parser.add_argument('--output-file', type=str, help='output file name', required=True)
parser.add_argument('--role', type=str, help='role', required=True)


parse_args = parser.parse_args()

banner = """
     __         ___         __
 ___/ /__ _  __/ _/__ _____/ /____  ______ __
/ _  / -_) |/ / _/ _ `/ __/ __/ _ \/ __/ // /
\_,_/\__/|___/_/ \_,_/\__/\__/\___/_/  \_, /
                                      /___/
ADFS/SAML Temporary Key Pair Generator
add --help for more options
"""

print(banner)

if not parse_args.password and not parse_args.login:
    print("Enter your ADFS credentials")
    # Get the federated credentials from the user
    username = input("Username (domain\\username): ")
    password = getpass.getpass(prompt="Password: ")
else:
    username = parse_args.login
    password = parse_args.password


##########################################################################
# Variables

# region: The default AWS region that this script will connect
# to for all API calls
REGION = 'us-east-1'

# output format: The AWS CLI output format that will be configured in the
# saml profile (affects subsequent CLI calls)
outputformat = 'json'

# SSL certificate verification: Whether or not strict certificate
# verification is done, False should only be used for dev/test
sslverification = True

# fully qualified domain name of your adfs
fqdn = 'adfs.devfactory.com'

# idpentryurl: The initial URL that starts the authentication process.
idpentryurl = 'https://'+fqdn+'/adfs/ls/IdpInitiatedSignOn.aspx?loginToRp=urn:amazon:webservices'


# key list, for saving up the keys to write to the file at the end
# items in the list are in the format {"account":"", "ak":"", "sak":""}
aws_key_list = []
##########################################################################


# authenticates to adfs using ntlm
def auth_ntlm():
    # reauth
    # Initiate session handler
    session = requests.Session()

    # Programmatically get the SAML assertion
    # Set up the NTLM authentication handler by using the provided credential
    session.auth = HttpNtlmAuth(username, password, session)

    # Opens the initial AD FS URL and follows all of the HTTP302 redirects
    # The adfs server I am using this script against returns me a form, not ntlm auth, so we cheat here giving it a
    # browser header so it gives us the NTLM auth we wanted.
    headers = {'User-Agent': 'Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko'}
    response = session.get(idpentryurl, verify=sslverification, headers=headers)

    # Exits if the authentication failed
    if response.status_code != 200:
        print('Authentication failed!')
        sys.exit(1)

    # Decode the response and extract the SAML assertion
    soup = BeautifulSoup(response.text, "html.parser")
    assertion_f = ''

    # Look for the SAMLResponse attribute of the input tag (determined by analyzing the debug print lines above)
    for inputtag in soup.find_all('input'):
        if inputtag.get('name') == 'SAMLResponse':
            assertion_f = inputtag.get('value')
    return assertion_f


def get_keys_from_sts(assertion, f_role_arn, f_principal_arn):

    conn = boto.sts.connect_to_region(REGION, anon=True)
    token = conn.assume_role_with_saml(f_role_arn, f_principal_arn, assertion)

    return {
        AWS_ACCESS_KEY_ID: token.credentials.access_key,
        AWS_SECRET_ACCESS_KEY: token.credentials.secret_key,
        AWS_SESSION_TOKEN: token.credentials.session_token
    }


def parse_role(role):

    role_arn = role.split(',')[0]
    principal_arn = role.split(',')[1]
    role_name = role_arn.split(':')[5].replace('role/', '')
    return role_arn, principal_arn, role_name


def get_aws_roles(assertion):
    # Parse the returned assertion and extract the authorized roles
    awsroles = []
    root = ET.fromstring(base64.b64decode(assertion))

    for saml2attribute in root.iter('{urn:oasis:names:tc:SAML:2.0:assertion}Attribute'):
        if saml2attribute.get('Name') == 'https://aws.amazon.com/SAML/Attributes/Role':
            for saml2attributevalue in saml2attribute.iter('{urn:oasis:names:tc:SAML:2.0:assertion}AttributeValue'):
                awsroles.append(saml2attributevalue.text)

    # Note the format of the attribute value should be role_arn,principal_arn
    # but lots of blogs list it as principal_arn,role_arn so let's reverse
    # them if needed
    for awsrole in awsroles:
        chunks = awsrole.split(',')
        if 'saml-provider' in chunks[0]:
            newawsrole = chunks[1] + ',' + chunks[0]
            index = awsroles.index(awsrole)
            awsroles.insert(index, newawsrole)
            awsroles.remove(awsrole)

    return awsroles


def main():
    assertion = auth_ntlm()

    aws_roles = get_aws_roles(assertion)

    found_role = None
    for role in aws_roles:

        (role_arn, principal_arn, role_name) = parse_role(role)

        if role_name == parse_args.role:
            found_role = role
            break

    if not found_role:
        print("User '{}' does not have role '{}'".format(username, parse_args.role))
        print("Available User Roles:\n\t{}".format("\n\t".join(aws_roles)))
        sys.exit(1)

    (role_arn, principal_arn, role_name) = parse_role(role)

    credentials = get_keys_from_sts(assertion, role_arn, principal_arn)

    with open(parse_args.output_file, 'w') as script:
        script.write("# Temporary AWS credentials for user '{}'\n".format(parse_args.login))
        script.write("echo Activating temp AWS credentials for role '{}'...\n".format(parse_args.role))
        for key in (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN):
            script.write("export {}={}\n".format(key, credentials[key]))

    print("Credentials file saved; run `source {}` to activate".format(parse_args.output_file))


if __name__ == "__main__":
    main()
