## Spring Cloud Lattice

Preview of Spring Cloud Lattice implementation

### Running the sample

1. [Install lattice](http://lattice.cf/docs/getting-started.html)
2. Follow the getting started guide, including scaling `lattice-app` to 3
4. run `mvn package`
5. run `java -jar spring-cloud-lattice-sample/target/spring-cloud-lattice-sample-1.0.0.BUILD-SNAPSHOT.jar`
6. visit [http://localhost:8080?service=lattice-app](http://localhost:8080?service=lattice-app) verify that the 3 services rotate through as you refresh

### Running the sample ON LATTICE

1. `cd spring-cloud-lattice-sample`
1. `mvn clean package docker:build`
1. `docker tag spring-cloud-lattice-sample:latest <yourdockerhubid>/spring-cloud-lattice-sample`
1. `docker push <yourdockerhubid>/spring-cloud-lattice-sample`
1. `LATTICE_CLI_TIMEOUT=180 ltc create spring-cloud-lattice-sample spencergibb/spring-cloud-lattice-sample`
1. `ltc scale spring-cloud-lattice-sample 3`
6. visit [http://spring-cloud-lattice-sample.192.168.11.11.xip.io?service=spring-cloud-lattice-sample](http://spring-cloud-lattice-sample.192.168.11.11.xip.io?service=spring-cloud-lattice-sample) verify that the 3 services rotate through as you refresh
6. visit [http://spring-cloud-lattice-sample.192.168.11.11.xip.io/me](http://spring-cloud-lattice-sample.192.168.11.11.xip.io/me) verify that the 3 services rotate through as you refresh
