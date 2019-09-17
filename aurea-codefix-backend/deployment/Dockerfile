FROM openjdk:8-jdk-alpine

ARG GIT_COMMIT
ENV GIT_COMMIT ${GIT_COMMIT}

# labels
LABEL com.trilogy.company=DevFactory
LABEL com.trilogy.team=codefix
LABEL com.trilogy.product=codefix
LABEL com.trilogy.maintainer.email=anatoly.danilov@aurea.com

#aws cli
RUN apk -Uuv add groff less python py-pip git openssh &&  pip install awscli&&  apk --purge -v del py-pip && rm /var/cache/apk/*

RUN mkdir -p /tmp/app && mkdir -p /app && mkdir -p /etc/service/codefix
COPY . /tmp/app
WORKDIR /tmp/app
RUN ./gradlew clean bootJar
RUN mv aurea-codefix-backend/build/libs/codefix-0.0.1-SNAPSHOT.jar /app/codefix.jar
RUN mv aurea-codefix-backend/deployment/entrypoint.sh /etc/service/codefix/run
RUN chmod +x /etc/service/codefix/run
WORKDIR /app
RUN rm -rf /tmp/app

EXPOSE 8080

ENTRYPOINT ["sh", "/etc/service/codefix/run"]
