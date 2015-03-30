## Spring Cloud Lattice

Preview of Spring Cloud Lattice implementation

### Running the sample

1. [Install lattice](http://lattice.cf/docs/getting-started.html)
2. Follow the getting started guide, including scaling `lattice-app` to 3
4. run `mvn package`
5. run `java -jar spring-cloud-lattice-sample/target/spring-cloud-lattice-sample-1.0.0.BUILD-SNAPSHOT.jar`
6. visit [http://localhost:8080](http://localhost:8080) verify that the 3 services rotate through as you refresh
