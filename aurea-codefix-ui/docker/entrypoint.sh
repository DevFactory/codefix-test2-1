#!/bin/bash

SRC_CONF="/etc/app/package/nginx.conf"
DEST_CONF="/etc/app/nginx.conf"

cp ${SRC_CONF} ${DEST_CONF}

# environment to angular properties mapping
echo "(function (window) {
  window.__env = window.__env || {};

  // API url
  window.__env.auth0Domain = '${AUTH0_DOMAIN}';
  window.__env.auth0ClientId = '${AUTH0_CLIENTID}';
  window.__env.auth0Audience = '${AUTH0_AUDIENCE}';
  window.__env.codeRampUrl = '${CODERAMP_URL}';
  window.__env.codefixBackend = '${CODEFIX_BACKEND}';
  window.__env.ticketingSystemUrl = '${TICKETING_SYSTEM_URL}';
  window.__env.appVersion = '${VERSION}';

}(this));" > /usr/share/nginx/html/env-config.js


exec nginx -c ${DEST_CONF}
