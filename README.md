# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
Auf REST is aimed at <a href='https://spring.io/'>Spring</a>-based applications that need to implement REST clients to dependent services/applications. It offers an annotation-driven and declarative programming model similar to  <a href='https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories'>Spring Data Repositories</a>. It abstracts away underline HTTP/REST concerns by offering a set of annotations and conventions with which application developers declare the intentions via Java interfaces with provided annotations. The developers don't need to dictate in an imperative way the details on how a HTTP client should be created, requests created/sent, responses processed, and exceptions propogated. The library takes care of these low-level details so they can focus on application logic. The library can reduce the code base of an application significantly by removing commonly-seen <a href='https://openjdk.org/groups/net/httpclient/intro.html'>HttpClient</a>/<a href='https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html'>RestTemplate</a>-based helper/utility/<a href='https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Service.html'>Service</a> classes that are largely repetitive in implementation, difficult to test and error prone. Because the programming model is centered on Java interfaces, the library also makes implementing unit tests much easier as it removes the need for heavy and brittle mocking.

## Quick Start

Assuming you have a Spring Boot application ready, add dependency:

* [Auf REST](https://mvnrepository.com/artifact/me.ehp246/auf-rest)

**Enable by `@EnableByRest`.**

```
@EnableByRest
@SpringBootApplication
class ClientApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
```

**Declare by `@ByRest`.**

```
@ByRest("${api.base}")
public interface TimeServer {
    Instant get();
}
```
At this point, you have a REST client that when invoked
* performs GET
* has the URL defined by a Spring property
* takes no parameter
* returns the response body as a Java ``Instant`` object

The client won't do anything by itself, so the next step is to...

**Inject and enjoy.**

```
@Service
public class AppService {
    // Do something with it
    @Autowired
    private TimeServer timeServer;
    ...
}
```

<br>
The following are a few more examples.

**POST a JSON body from an object**

```
@ByRest("${api.base}")
public interface TimeServer {
    void post(Instant newTime);
}
```

**PATCH with query parameters**

```
@ByRest("${api.base}")
public interface TimeServer {
    void patch(@OfQuery("timeUnit") String timeUnit, @OfQuery("timeDelta") int timeAmount);
}
```

**DELETE**

```
@ByRest("${api.base}")
public interface TimeServer {
   void delete(@OfQuery("timerName") String timerName);
}
```

**PUT**

```
@ByRest("${api.base}")
public interface TimeServer {
    void put(@OfQuery("timerName") String timerName);
}
```

Details can be found at the project's [Wiki](https://github.com/ehp246/auf-rest/wiki).

## Runtime
The latest version 5 requires the following to run:
* <a href='https://openjdk.org/projects/jdk/21/'>JDK 21</a>
* <a href='https://mvnrepository.com/artifact/org.springframework'>Spring 6.1</a>: Bean and Context
* <a href='https://mvnrepository.com/artifact/com.fasterxml.jackson'>Jackson 2</a>: Core and Databind

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
