# Hex
An open-source live streaming site.

[![](https://jitci.com/gh/MemoryLeakDeath/hex/svg)](https://jitci.com/gh/MemoryLeakDeath/hex)
[![Release](https://jitpack.io/v/MemoryLeakDeath/hex.svg)](https://jitpack.io/#MemoryLeakDeath/hex)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/MemoryLeakDeath/hex/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/MemoryLeakDeath/hex/tree/main)

## Development Setup
To get a development environment setup for this project, you can either follow the "Local Deployment" steps or use the "Docker Deployment" steps below.

### Prerequisites (Docker Local Deployment)
- [Docker 4.21](https://www.docker.com/)
- Java OpenJDK 19
- [Apache Maven 3.9](https://maven.apache.org/index.html)
- (Optional) My [greenmail-gui](https://github.com/MemoryLeakDeath/greenmail-gui) project for locally testing/viewing emails generated by the application

### Initial setup (Docker Local Deployment)
- Create a "secretkeys.properties" file under the root of the project, then copy and fill in the appropriate properties below:
```
postgresPassword=<password for 'postgres' user>
liquibase.dbserver=localhost
dbname=hex
dbuser=hex-user
dbpassword=<some password>
dbport=5432
dbserver=hex-db  # server name is required to be "hex-db" for docker deployment
dburl=jdbc:postgresql://${dbserver}:${dbport}/${dbname}
totpKey=<some generated key max 85 characters>
rememberMeKey=<some generated key max 90 characters>
emailHost=greenmail-app
emailPort=10025
emailTLS=true
emailFrom=hexautomatedemail@test.com
dbtestname=hex-test
dbtestuser=hex-tester
dbtestpassword=<some password>
dbtestport=5432
dbtestserver=hex-db # server name is required to be "hex-db" for docker deployment
dbtesturl=jdbc:postgresql://${dbtestserver}:${dbtestport}/${dbtestname}
```
- To perform the initial build of the docker containers, the creation of the database, and the creation of the dev SSL certs, run the following:
```
mvn clean install docker:build docker:start liquibase:update
```
Some notes:
- You should be able to start/stop/restart the docker containers after this initial build using the normal docker tools/command line.
- The app server exposes several volumes into the project base folder, if your IDE of choice compiles classes to "target/classes" then they will automatically be redeployed into the running tomcat docker container.  Similarly, changes made to the project's HTML/CSS/JS files are automatically picked up by the running docker instance.  The app server logs are available under the "logs" folder in the project base folder.  If you wish to make changes to the tomcat server.xml file, a copy of it is under the "conf" folder in the project base and will be copied to the container on the next "docker:build" maven command.
- If you wish to test email, my [greenmail-gui](https://github.com/MemoryLeakDeath/greenmail-gui) project can also be deployed to Docker, see that project page for more details.
- (Optional) Configure your IDE to use the Checkstyle file in the root of the project (checkstyle.xml).  The checkstyle checks can also be run via maven:
```
mvn checkstyle:check
```
- That's it! You should be able to see the application running at "https://localhost:8443/" and if you optionally deploy greenmail-gui for email testing, that will be at "https://localhost:18443/greenmail-gui/".


### Prerequisites (Non-Docker Local Deployment)
- Java OpenJDK 19
- [Apache Tomcat 10.1](https://tomcat.apache.org/)
- [Apache Maven 3.9](https://maven.apache.org/index.html)
- [Postgresql 15.3](https://www.postgresql.org/)
- (Optional) My [greenmail-gui](https://github.com/MemoryLeakDeath/greenmail-gui) project for locally testing/viewing emails generated by the application
  
### Initial setup (Non-Docker Local Deployment)
- Create a "secretkeys.properties" file under the root of the project, then copy and fill in the appropriate properties below:
```
postgresPassword=<leave blank>
liquibase.dbserver=localhost
dbname=hex
dbuser=hex-user
dbpassword=<some password>
dbport=5432
dbserver=localhost
dburl=jdbc:postgresql://${dbserver}:${dbport}/${dbname}
totpKey=<some generated key max 85 characters>
rememberMeKey=<some generated key max 90 characters>
emailHost=localhost
emailPort=10025
emailTLS=true
emailFrom=hexautomatedemail@test.com
dbtestname=hex-test
dbtestuser=hex-tester
dbtestpassword=<some password>
dbtestport=5432
dbtestserver=localhost
dbtesturl=jdbc:postgresql://${dbtestserver}:${dbtestport}/${dbtestname}
```
- Run the following command:
```
mvn resources:resources
```
- Run the "target/classes/hex-db-init.sh" file to create the databases and users, on Windows you can copy the sql statements out and run them in pgAdmin.
- To initialize the project, generate dev SSL keystore, and run the database migrations, run the following:
```
mvn clean install liquibase:update
```
- You can use the conf/server.xml to configure Tomcat for the application, just make sure to point the <Certificate> to the project directory's "conf/local_keystore.jks" file:
```
        <SSLHostConfig>
            <Certificate certificateKeystoreFile="<project.directory>/conf/local_keystore.jks" certificateKeystorePassword="changeit" type="RSA"/>
        </SSLHostConfig>
```
- NOTE on Eclipse IDE: I recommend changing the tomcat settings (double-click the server in the "servers" view) to have longer timeouts on start/stop and also click the "modules" tab and make sure the application root is set to just "/".
- (Optional) Configure your IDE to use the Checkstyle file in the root of the project (checkstyle.xml).  The checkstyle checks can also be run via maven:
```
mvn checkstyle:check
```
- That's it! You should be able to see the application running at "https://localhost:8443/" and if you optionally deploy greenmail-gui for email testing, that will be at "https://localhost:8443/greenmail-gui/".
