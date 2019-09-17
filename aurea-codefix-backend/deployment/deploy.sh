#!/bin/bash

REGISTRY=registry2.swarm.devfactory.com/v2/devfactory
APP=codefix
BUILD_SERVER=dlb1.aureacentral.com
ROOT=`pwd`
export DOCKER_HOST=${BUILD_SERVER}

# make sure build fails
set -e

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

    mkdir -p /tmp/codefix
    export KUBECONFIG=$6
    aurea-codefix-backend/deployment/eks-helpers/devfactory-get-aws-keys-saml.py --login="${username}" --password="${password}" --role="RAM-AWS-Aurea-CentralKube-Codefix" --output-file=aws.creds

    export AWS_PROFILE=saml
    . aws.creds && rm -f aws.creds
    echo "KUBECONFIG value: $KUBECONFIG"

    if [ "${command}" == "deploy-${env}" ]; then
        kubectl -n codefix-${env} delete secret codefix-cert
        kubectl -n codefix-${env} create secret tls codefix-cert --key ${df_key} --cert ${df_cert}
    fi
}

check(){
  command -v $1 >/dev/null 2>&1 || { echo >&2 "$1 is required but it's not installed.  Aborting."; exit 1; }
}

verifyDeployment() {
   requiredVersion=$(git rev-parse --short HEAD)
   targetEnv=$1
   retry=5s
   host=$(get_host ${targetEnv})

   targetUrl="https://$host/actuator/info"
   for i in {1..60}
   do
      echo "Querying GET $targetUrl"
      info=$(curl $targetUrl -f || true)
      if echo $info | grep $requiredVersion; then
         echo "Version $requiredVersion successfully deployed to $targetEnv"
         exit 0
      else
         echo "Backend has version: $info. Required revision: $requiredVersion. Iteration $i Retry in $retry"
      fi
      sleep $retry
   done
   echo Required version was not deployed. Failing build with non-zero exit
   exit 25
}


build_image() {
    cd ${ROOT}
    cp aurea-codefix-backend/deployment/Dockerfile .
    environment=$1
    echo "Building image for: ${GIT_BRANCH}"
    image_name=`define_image_name ${environment}`
    commit_hash=$(git show | head -1 | cut -d' ' -f2)
    short_hash=$(git rev-parse --short ${commit_hash})
    echo "Building ${image_name} image with ${short_hash} on ${DOCKER_HOST}"
    docker build --build-arg GIT_COMMIT=${short_hash} -t ${APP}:${image_name} .
    buildResult="$?"
    if [ $buildResult -ne 0 ] ; then
        echo "We have an error in the backend docker build. Exiting with exit code: $buildResult"
        exit $buildResult
    fi
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

promote_image_to_qa() {
    echo "Promoting dev-latest image on ${DOCKER_HOST} to staging"
    # Attempt to re-fetch image (|| true to avoid failing script if genuinely missing)
    docker pull ${REGISTRY}/${APP}:dev-latest || true
    docker tag ${REGISTRY}/${APP}:dev-latest ${REGISTRY}/${APP}:qa-latest
    docker push ${REGISTRY}/${APP}:qa-latest
}

promote_image_to_stg() {
    echo "Promoting dev-latest image on ${DOCKER_HOST} to staging"
    # Attempt to re-fetch image (|| true to avoid failing script if genuinely missing)
    docker pull ${REGISTRY}/${APP}:qa-latest || true
    docker tag ${REGISTRY}/${APP}:qa-latest ${REGISTRY}/${APP}:staging-latest
    docker push ${REGISTRY}/${APP}:staging-latest
}

promote_image_to_prod() {
    echo "Promoting staging-latest image on ${DOCKER_HOST} to prod"
    # Attempt to re-fetch image (|| true to avoid failing script if genuinely missing)
    docker pull ${REGISTRY}/${APP}:staging-latest || true
    docker tag ${REGISTRY}/${APP}:staging-latest ${REGISTRY}/${APP}:prod-latest
    docker push ${REGISTRY}/${APP}:prod-latest
}

get_ingress_class() {
if [ "$1" == "prod" ]; then
  echo "private"
else
  echo "private"
fi
}

get_host() {
if [ "$1" == "prod" ]; then
  echo "codefix-backend.devfactory.com"
else
  echo "codefix-backend-$1.devfactory.com"
fi
}

get_webhook_host() {
  if [ "$1" == "prod" ]; then
    echo "codefix-webhook.devfactory.com"
  else
    echo "codefix-webhook-$1.devfactory.com"
  fi
}

get_github_syncCron() {
  if [ "$1" == "prod" ]; then
    echo "\"0 0 6 * * ?\""
  else
    echo "\"* * * * * ?\""
  fi
}

get_env_profile() {
if [ "$1" == "staging" ]; then
    echo "stg"
else
    echo $1
fi
}

get_codeserver_base_url() {
  case $1 in
   dev)
     echo "http://codeserver-framework-dev.devfactory.com"
   ;;
   qa)
     echo "http://codeserver-framework-qa.devfactory.com"
   ;;
   staging)
     echo "http://codeserver-framework-staging.devfactory.com"
   ;;
   *)
     echo "http://codeserver-framework-prod.devfactory.com"
   ;;
  esac
}

get_brp_notification_queue() {
  case $1 in
   dev)
     echo "devfactory_brp_to_codefix_notifications_dev"
   ;;
   qa)
     echo "devfactory_brp_to_codefix_notifications_qa"
   ;;
   staging)
     echo "devfactory_brp_to_codefix_notifications_staging"
   ;;
   *)
     echo "devfactory_brp_to_codefix_notifications_prod"
   ;;
  esac
}

get_brp_url() {
  case $1 in
   dev)
     echo "http://javabrp-dev.devfactory.com"
   ;;
   qa)
     echo "http://javabrp-qa.devfactory.com"
   ;;
   staging)
     echo "http://javabrp-stage.devfactory.com"
   ;;
   *)
     echo "http://javabrp-prod.devfactory.com"
   ;;
  esac
}

get_frontend_url() {
  case $1 in
   dev)
     echo "https://codefix-dev.devfactory.com"
   ;;
   qa)
     echo "https://codefix-qa.devfactory.com"
   ;;
   staging)
     echo "https://codefix-staging.devfactory.com"
   ;;
   *)
     echo "https://codefix.devfactory.com"
   ;;
  esac
}

get_assembly_line_confirmations_queue() {
  case $1 in
   dev)
     echo "devfactory_assembly_line_to_codefix_confirmations_dev"
   ;;
   qa)
     echo "devfactory_assembly_line_to_codefix_confirmations_qa"
   ;;
   staging)
     echo "devfactory_assembly_line_to_codefix_confirmations_stg"
   ;;
   *)
     echo "devfactory_assembly_line_to_codefix_confirmations_prod"
   ;;
  esac
}

get_assemblyline_order_queue() {
  case $1 in
   dev)
     echo "devfactory_codefix_to_assembly_line_orders_dev"
   ;;
   qa)
     echo "devfactory_codefix_to_assembly_line_orders_qa"
   ;;
   staging)
     echo "devfactory_codefix_to_assembly_line_orders_stg"
   ;;
   *)
     echo "devfactory_codefix_to_assembly_line_orders_prod"
   ;;
  esac
}

get_assemblyline_fixes_queue() {
  case $1 in
   dev)
     echo "devfactory_assembly_line_to_codefix_fixes_dev"
   ;;
   qa)
     echo "devfactory_assembly_line_to_codefix_fixes_qa"
   ;;
   staging)
     echo "devfactory_assembly_line_to_codefix_fixes_stg"
   ;;
   *)
     echo "devfactory_assembly_line_to_codefix_fixes_prod"
   ;;
  esac
}

refresh_image() {
    command=$1
    env=$(get_env $1)
    env_profile=$(get_env_profile ${env})
    ingress_class=$(get_ingress_class ${env})
    codeserver_app_base_url=$(get_codeserver_base_url ${env})
    brp_notification_queue=$(get_brp_notification_queue ${env})
    assemblyline_order_queue=$(get_assemblyline_order_queue ${env})
    brp_url=$(get_brp_url ${env})
    frontend_url=$(get_frontend_url ${env})
    assembly_line_confirmations_queue=$(get_assembly_line_confirmations_queue ${env})
    assembly_line_fixes_queue=$(get_assemblyline_fixes_queue ${env})
    host=$(get_host ${env})
    webhook_host=$(get_webhook_host ${env})
    github_syncCron=$(get_github_syncCron ${env})
    git_version_timestamp=$(echo `git log -1 --format=%cd --date=format:%Y.%m.%d`"-${env}-"`git log -1 --format=%h`-`date +%s`)

    aws_key=$2
    aws_secret=$3
    image_name=`define_image_name ${env}`

    sed -e "s/<environment>/${env}/g" \
        -e "s/<image_name>/${image_name}/g" \
        -e "s/<ingress-class>/${ingress_class}/g" \
        -e "s/<env-profile>/${env_profile}/g" \
        -e "s~<codeserver-appBaseUrl>~${codeserver_app_base_url}~g" \
        -e "s~<brp-notificationQueue>~${brp_notification_queue}~g" \
        -e "s~<frontend-url>~${frontend_url}~g" \
        -e "s~<assemblyline-orderQueue>~${assemblyline_order_queue}~g" \
        -e "s~<brp-url>~${brp_url}~g" \
        -e "s~<assemblyline-confirmationsQueue>~${assembly_line_confirmations_queue}~g" \
        -e "s~<assemblyline-fixesQueue>~${assembly_line_fixes_queue}~g" \
        -e "s/<host>/${host}/g"  \
        -e "s/<webhook-host>/${webhook_host}/g"  \
        -e "s/<github-syncCron>/${github_syncCron}/g"  \
        -e "s/<rolling-version>/${git_version_timestamp}/g" \
        -e "s~<aws-access-key-id>~${aws_key}~g" \
        -e "s~<aws-secret-access-key>~${aws_secret}~g" ${ROOT}/aurea-codefix-backend/deployment/deploy-codefix-template.yaml > /tmp/deployment.yaml

     cat /tmp/deployment.yaml

    if [ "${command}" == "deploy-${env}" ]; then
        kubectl -n codefix-${env} apply -f /tmp/deployment.yaml
    fi
    if [ "${command}" == "undeploy-${env}" ]; then
        kubectl -n codefix-${env} delete -f /tmp/deployment.yaml
    fi

    verifyDeployment ${env}
}

execute() {
    command=$1
    forceBuild=$2
    env=$(get_env $1)
    if [ "${command}" == "deploy-${env}" ]; then
        if [ "${env}" == "dev" ]; then
            build_image dev
        else
            if [ "${forceBuild}" == "true" ]; then
                build_image ${env}
            else
                if [ "${env}" == "qa" ]; then
                    promote_image_to_qa
                elif [ "${env}" == "staging" ]; then
                    promote_image_to_stg
                else
                    promote_image_to_prod
                fi
            fi
        fi
    fi

    refresh_image ${command} $3 $4
}

check docker
check sed
check awk
check git
check grep
check kubectl

init_k8s $1 $3 $4 $5 $6 $7
execute $1 $2 $8 $9
