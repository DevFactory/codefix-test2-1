#!/bin/bash

REGISTRY=registry.devfactory.com/devfactory
APP=codefix-ui
BUILD_SERVER=tcp://dlb1.aureacentral.com
ROOT=`pwd`/aurea-codefix-ui
export DOCKER_HOST=${BUILD_SERVER}

# make sure build fails
set -e

check(){
  command -v $1 >/dev/null 2>&1 || { echo >&2 "$1 is required but it's not installed.  Aborting."; exit 1; }
}

get_env() {
    command=$1
    env=`echo ${command} | cut -d'-' -f2`
    echo ${env}
}

init_k8s() {
    command=$1
    env=$(get_env $1)
    username=$2
    password=$3
    df_cert=$4
    df_key=$5

    mkdir -p /tmp/codefix-ui

    export KUBECONFIG=$6
    aurea-codefix-backend/deployment/eks-helpers/devfactory-get-aws-keys-saml.py --login="${username}" --password="${password}" --role="RAM-AWS-Aurea-CentralKube-Codefix" --output-file=aws.creds

    export AWS_PROFILE=saml
    . aws.creds && rm -f aws.creds
    echo "KUBECONFIG value: $KUBECONFIG"

    if [ "${command}" == "deploy-${env}" ]; then
      kubectl -n codefix-${env} delete secret codefix-ui-cert
      kubectl -n codefix-${env} create secret tls codefix-ui-cert --key ${df_key} --cert ${df_cert}
    fi
}

build_image(){
  cd ${ROOT}
  cp deployment/Dockerfile .
  environment=$1
  image_name=`define_image_name ${environment}`
  commit_hash=$(git show | head -1 | cut -d' ' -f2)
  short_hash=$(git rev-parse --short $commit_hash)
  echo "Building image ${image_name} with ${short_hash}"
  docker build --no-cache --build-arg GIT_COMMIT=${short_hash} --build-arg CR_ENV=${environment} -t ${APP}:${image_name} .
  buildResult="$?"
   if [ $buildResult -ne 0 ] ; then
            echo "We have an error in the ui docker build. Exiting with exit code: $buildResult"
            exit $buildResult
  fi
  echo "after the build i have this status $buildResult"

  docker tag ${APP}:${image_name} ${REGISTRY}/${APP}:${image_name}
  docker push ${REGISTRY}/${APP}:${image_name}
}

define_image_name() {
    if [[ "${GIT_BRANCH}" == origin/env/* ]]; then
      echo "$1-latest"
    else
      image_name=$1-${GIT_BRANCH}
      image_name=`echo "${image_name}" | sed -e 's/origin\///' | sed -e 's/\//_/' | cut -c 1-30`
      echo ${image_name}
    fi
}

get_ingress_class() {
if [ "$1" == "prod" ]; then
  echo "private"
else
  echo "private"
fi
}

get_ui_host() {
if [ "$1" == "prod" ]; then
  echo "codefix.devfactory.com"
else
  echo "codefix-$1.devfactory.com"
fi
}

get_coderamp_host() {
if [ "$1" == "prod" ]; then
  echo "https://coderamp.devfactory.com"
else
  echo "https://coderamp-$1.devfactory.com"
fi
}

get_codefix_backend_host() {
if [ "$1" == "prod" ]; then
  echo "https://codefix-backend.devfactory.com"
else
  echo "https://codefix-backend-$1.devfactory.com"
fi
}

get_codeserver_ticketing_host() {
  echo "https://codeserver-framework-$1.devfactory.com/#/ticketing-system"
}

refresh_x1(){
  command=$1
  env=$(get_env $1)
  ingress_class=$(get_ingress_class ${env})
  host=$(get_ui_host ${env})
  git_version_timestamp=$(echo `git log -1 --format=%cd --date=format:%Y.%m.%d`"-${env}-"`git log -1 --format=%h`)

  auth0Domain=$2
  auth0ClientId=$3
  auth0Audience=$4
  codeRampUrl=$(get_coderamp_host ${env})
  codefixBackend=$(get_codefix_backend_host ${env})
  ticketingSystemUrl=$(get_codeserver_ticketing_host ${env})

  echo "generating /tmp/deployment.yaml"
  image_name=`define_image_name ${env}`

  sed -e "s/<environment>/${env}/g" \
      -e "s/<image_name>/${image_name}/g" \
      -e "s/<rolling-version>/${git_version_timestamp}/g" \
      -e "s~<auth-domain>~${auth0Domain}~g" \
      -e "s~<auth-clientid>~${auth0ClientId}~g" \
      -e "s~<auth-audience>~${auth0Audience}~g" \
      -e "s~<codeRampUrl>~${codeRampUrl}~g" \
      -e "s~<codeFixBackendUrl>~${codefixBackend}~g" \
      -e "s~<codeserver-ticketing-system>~${ticketingSystemUrl}~g" \
      -e "s/<ingress-class>/${ingress_class}/g" \
      -e "s/<host>/${host}/g" ${ROOT}/deployment/deploy-template.yaml > /tmp/deployment.yaml

  echo "/tmp/deployment.yaml content"
  cat /tmp/deployment.yaml

  echo "refreshing X1"
    if [ "${command}" == "deploy-${env}" ]; then
        kubectl -n codefix-${env} apply -f /tmp/deployment.yaml
    fi
    if [ "${command}" == "undeploy-${env}" ]; then
        kubectl -n codefix-${env} delete -f /tmp/deployment.yaml
    fi

   verifyDeployment ${env}
}

verifyDeployment() {
   requiredVersion=$(git rev-parse --short HEAD)
   targetEnv=$1
   retry=5s
   host=$(get_ui_host ${targetEnv})


   targetUrl="https://$host/env-config.js"
   for i in {1..60}
   do
      echo "Querying GET $targetUrl"
      info=$(curl $targetUrl -f || true)
      if echo $info | grep $requiredVersion; then
         echo "Version $requiredVersion successfully deployed to $targetEnv"
         exit 0
      else
         echo "FrontEnd has version: $info. Required revision: $requiredVersion. Iteration $i Retry in $retry"
      fi
      sleep $retry
   done
   echo Required version was not deployed. Failing build with non-zero exit
   exit 25
}

promote_image(){
  # Attempt to re-fetch image (|| true to avoid failing script if genuinely missing)
  docker pull "${REGISTRY}/${APP}:${1}-latest" || true
  docker tag ${REGISTRY}/${APP}:${1}-latest ${REGISTRY}/${APP}:${2}-latest
  docker push ${REGISTRY}/${APP}:${2}-latest
}

execute(){
  command=$1
  env=$(get_env $1)
  fromEnv=$(get_from_env $env)
  forceBuild=$5
  if [ "${forceBuild}" == "true" ]; then
    build_image ${env}
  else
    promote_image $fromEnv $env
  fi
  refresh_x1 $1 $2 $3 $4
}

get_from_env() {
  case $1 in
    qa)
      echo "dev"
    ;;
    staging)
      echo "qa"
    ;;
    prod)
      echo "staging"
    ;;
   esac
}

check docker
check git
check sed
check curl
check kubectl

init_k8s $1 $2 $3 $4 $5 $6
execute $1 $7 $8 $9 ${10}
