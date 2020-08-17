# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
## Dependency
Auf REST is developed and tested on top of these:
* JDK 11
* Log4j 2
* Jackson
* Spring 5
* Spring Boot 2

It requires the following to run:
* JDK 11
* Log4j 2: api
* Jackson: core and databind
* Spring: beans, context and web

It does not require Spring Boot. It should work on Spring 4 but it's not been tested.

##JSON Provider
While it's designed to support multiple JSON providers, as of now Jackson is the one used during development and testing. Jackson's core and databind modules are required at the runtime. 

During boot-up the framework looks for an optional ObjectMapper-typed bean in Spring bean factory. If one is found, the framework uses the found as the provider. If there is no such a bean, the framework will configure a private ObjectMapper for its use. For this private instance, the framework tries to include [jsr310](https://github.com/FasterXML/jackson-modules-java8) and [mrbean](https://github.com/FasterXML/jackson-modules-base/tree/master/mrbean) modules if they are present on the class-path. But the two modules are not required.

If you would like to see additional providers supported, please file an issue. I'd be happy to add them.

## Logging
Logging inside of Auf REST is implemented on Log4j 2 API. Log4j-api module is required by the framework at runtime. But it does not require the log4j2-core module. The application can choose its own preferred implementation.

For Log4j 2 details, please visit [here](https://logging.apache.org/log4j/2.x/manual/index.html).

## Examples
## Authentication
## Configuration
The framework supports the following application configuration properties.


```
me.ehp246.aufrest.connectTimeout
```
It defines, in milli-seconds, how long to wait for a connection to a domain host. The default is 15000 milli-seconds.


The value is passed to the HttpClient builder. See the details [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.Builder.html#connectTimeout(java.time.Duration)).

```
me.ehp246.aufrest.responseTimeout
```
It defines, in milli-seconds, how long to wait for a response on a request. The default is 30000 milli-seconds.

See the details [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.Builder.html#timeout(java.time.Duration)).

## Modularity
While Auf REST is not a Java module yet, the intention is to make it so as soon as I can figure out how to run the unit tests without an issue. Currently I'm having trouble to run unit tests by Maven because of in-accessible classes. If you can provide help, please let me know. It'd be much appreciated.

Once modularized, all exported classes will be under package:
* me.ehp246.aufrest.api

All code outside of this package is private.

