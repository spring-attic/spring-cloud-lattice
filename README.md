## This project is obselete and no longer maintained.

## Spring Cloud Lattice

Preview of Spring Cloud Lattice implementation

### Setup

Tested with lattice v0.5.0

Create redis, rabbit and mysql using the [lattice docs](http://lattice.cf/docs/docker-image-examples/). 

```
mysql -h"192.168.11.11" -p"somesecret" -u"root"
CREATE DATABASE test;
```

### Running the sample

1. [Install lattice](http://lattice.cf/docs/getting-started.html)
2. Follow the getting started guide, including scaling `lattice-app` to 3
4. run `mvn  --settings .settings.xml install`
5. run `PROCESS_GUID=spring-cloud-lattice-sample java -jar spring-cloud-lattice-sample/target/spring-cloud-lattice-sample-1.0.0.BUILD-SNAPSHOT.jar`
6. visit [http://localhost:8080?service=lattice-app](http://localhost:8080?service=lattice-app) verify that the 3 services rotate through as you refresh

### Running the sample ON LATTICE

1. `./mvnw clean package`
1. `ltc build-droplet sc-lattice-sample java --path=spring-cloud-lattice-sample/target/spring-cloud-lattice-sample-1.0.0.BUILD-SNAPSHOT.jar`
1. `ltc launch-droplet sc-lattice-sample sc-lattice-sample`
1. visit [http://spring-cloud-lattice-sample.192.168.11.11.xip.io?service=sc-lattice-sample](http://spring-cloud-lattice-sample.192.168.11.11.xip.io?service=sc-lattice-sample) verify that the 3 services rotate through as you refresh
1. visit [http://sc-lattice-sample.192.168.11.11.xip.io/me](http://sc-lattice-sample.192.168.11.11.xip.io/me) verify that the 3 services rotate through as you refresh

### IDE Discovery

if you run SampleLatticeApplication in the IDE on port 8081 you can run it on lattice
with the following command

`LATTICE_CLI_TIMEOUT=180 ltc create spring-cloud-lattice-sample spencergibb/spring-cloud-lattice-sample -- java -jar /spring-cloud-lattice-sample.jar --spring.cloud.lattice.discovery.routes.myservice.port=8081`

Then call `http://spring-cloud-lattice-sample.192.168.11.11.xip.io/call` and it will hit
the service running in the ide.

### Config Server

TODO: document using catalyst `ltc create -m 512 catalyst mstine/catalyst`

