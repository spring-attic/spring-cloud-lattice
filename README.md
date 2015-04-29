## Spring Cloud Lattice

Preview of Spring Cloud Lattice implementation

### Temporary pre-setup

1. `git clone git@github.com:markfisher/receptor-client.git`
1. `./gradlew clean build install -x test`

ltc create redis redis -r
ltc create rabbit rabbitmq -r
ltc create mysql mysql -r -e MYSQL_ROOT_PASSWORD=password
mysql -h 192.168.11.11 -u root -P 61002 -p
CREATE DATABASE test
ltc create mongo mongo -r -e LC_ALL=C -- /entrypoint.sh mongod --smallfiles

### Running the sample

1. [Install lattice](http://lattice.cf/docs/getting-started.html)
2. Follow the getting started guide, including scaling `lattice-app` to 3
4. run `mvn  --settings .settings.xml install`
5. run `PROCESS_GUID=spring-cloud-lattice-sample java -jar spring-cloud-lattice-sample/target/spring-cloud-lattice-sample-1.0.0.BUILD-SNAPSHOT.jar`
6. visit [http://localhost:8080?service=lattice-app](http://localhost:8080?service=lattice-app) verify that the 3 services rotate through as you refresh

### Running the sample ON LATTICE

Replace `<yourdockerhubid>` below with your docker hub id.

1. `cd spring-cloud-lattice-sample`
1. `mvn --settings ../.settings.xml clean package docker:build`
1. `docker tag spring-cloud-lattice-sample:latest <yourdockerhubid>/spring-cloud-lattice-sample`
1. `docker push <yourdockerhubid>/spring-cloud-lattice-sample`
1. `LATTICE_CLI_TIMEOUT=180 ltc create spring-cloud-lattice-sample spencergibb/spring-cloud-lattice-sample`

6. visit [http://spring-cloud-lattice-sample.192.168.11.11.xip.io?service=spring-cloud-lattice-sample](http://spring-cloud-lattice-sample.192.168.11.11.xip.io?service=spring-cloud-lattice-sample) verify that the 3 services rotate through as you refresh
6. visit [http://spring-cloud-lattice-sample.192.168.11.11.xip.io/me](http://spring-cloud-lattice-sample.192.168.11.11.xip.io/me) verify that the 3 services rotate through as you refresh

### IDE Discovery

if you run SampleLatticeApplication in the IDE on port 8081 you can run it on lattice
with the following command

`LATTICE_CLI_TIMEOUT=180 ltc create spring-cloud-lattice-sample spencergibb/spring-cloud-lattice-sample -- java -jar /spring-cloud-lattice-sample.jar --spring.cloud.lattice.discovery.routes.myservice.port=8081`

Then call `http://spring-cloud-lattice-sample.192.168.11.11.xip.io/call` and it will hit
the service running in the ide.

### Config Server

LATTICE_CLI_TIMEOUT=180 ltc create configserver springcloud/configserver
LATTICE_CLI_TIMEOUT=180 ltc create configserver mstine/configserver --env SERVER_PORT=8080

SPRING_CLOUD_CONFIG_SERVER_GIT_URI=
