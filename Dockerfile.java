USER root

VOLUME /tmp
RUN mkdir -p /var/log

#New relic data 
ARG NRA_LICENCE
ARG NRA_APP_NAME
ARG ENV_DEPLOY


#Installing curl and zip
RUN apk --update  --no-cache add curl unzip libtcnative-1

#Configure new relic logs
RUN mkdir /opt
RUN mkdir -p /opt/newrelic/logs
RUN chmod 777 /opt/newrelic && chmod 777 /opt/newrelic/logs

#Install APM
RUN curl "http://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -o /tmp/newrelic.zip \
    && unzip /tmp/newrelic.zip -d /opt/ \
    && rm /tmp/newrelic.zip

RUN cat /opt/newrelic/newrelic.yml | sed -e "s/<%= license_key %>/$NRA_LICENCE/" \
    -e "s/app_name:.*/app_name: $NRA_APP_NAME/" > /opt/newrelic/newrelic.yml.new


#Overwrite newrelic.yml file with our data
RUN mv /opt/newrelic/newrelic.yml /opt/newrelic/newrelic.yml.default
RUN mv /opt/newrelic/newrelic.yml.new /opt/newrelic/newrelic.yml


#Copying jar generated
COPY target/ems-provider-service-1.0-SNAPSHOT.jar /home/ems-provider-service-1.0-SNAPSHOT.jar
COPY application.yml /home/application.yml

#Creating folders needed
RUN mkdir -p /qantas_files /qantas_logs

RUN ls /dev

#Running Java application
CMD ["sh", "-c","java -Djava.security.egd=file:/dev/./urandom -jar /saloodo-backend.jar  -Djava.library.path=/dev/tomcat/bin -Dspring.config.location=application.yml -Dspring.profiles.active=$ENV_DEPLOY  -javaagent:\"/opt/newrelic/newrelic.jar\" -jar /home/ems-provider-service-1.0-SNAPSHOT.jar"]

