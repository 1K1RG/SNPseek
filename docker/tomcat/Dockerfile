#FROM maven:3.6.3 as maven
#LABEL COMPANY="ShuttleOps"
#LABEL MAINTAINER="support@shuttleops.com"
#LABEL APPLICATION="Sample Application"

#WORKDIR /usr/src/app
#COPY . /usr/src/app
#RUN mvn package 

#FROM tomcat
#ARG TOMCAT_FILE_PATH=/docker 
	
#Data & Config - Persistent Mount Point
#ENV APP_DATA_FOLDER=/var/lib/SampleApp
#ENV SAMPLE_APP_CONFIG=${APP_DATA_FOLDER}/config/
	
#ENV CATALINA_OPTS="-Xms1024m -Xmx4096m -XX:MetaspaceSize=512m -	XX:MaxMetaspaceSize=512m -Xss512k"

#Move over the War file from previous build step
#WORKDIR /usr/local/tomcat/webapps/
#COPY target/SNP-seekV3_clean-8.13.3.war /usr/local/tomcat/webapps/v3.war

#COPY ${TOMCAT_FILE_PATH}/* ${CATALINA_HOME}/conf/

#WORKDIR /usr/local/tomcat

#XPOSE 8080

#CMD ["catalina.sh", "run"]


FROM tomcat:8.5-jre8-alpine

WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

RUN mkdir webapps/temp

COPY SNP-seekV3_clean-8.13.3.war webapps/v3.war

COPY logging.properties conf/logging.properties

#RUN rm /usr/local/tomcat/conf/logging.properties

EXPOSE 8080

CMD [ "catalina.sh", "run"]
