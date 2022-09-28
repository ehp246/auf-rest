# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
Auf REST is aimed at Spring-based applications that need to implement REST clients to dependent services/applications. It offers an annotation-driven and declarative programming approach similar to  Spring Data Repository. It abstracts away underline HTTP/REST concerns by offering a set of annotations and conventions with which the developers declare the intentions via plain Java interfaces. The developers don't need to dictate in an imperative way the details on how a HTTP client should be created, requests sent, responses processed, and exceptions handled. The library takes care of these low-level details for the developers so they can focus on application logic. The library can reduce the code base of an application significantly by removing commonly-seen HttpClient/RestTemplate-based helper/utility classes that are largely repetitive in implementation, difficult to test and error prone. Because the programming is based on straight Java interfaces, the library also makes implementing unit tests much easier as it removes the need for heavy and brittle mocking.

## Quick Start

**Add [Maven dependency](https://mvnrepository.com/artifact/me.ehp246/auf-rest).**

**Enable the library using `@EnableByRest`.**

```
@EnableByRest
@SpringBootApplication
class ClientApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
```

**Define a `ObjectMapper` Bean**

The library requires a ``com.fasterxml.jackson.databind.ObjectMapper`` bean in the application context. Such a bean is often available from Spring Boot dependencies, there is no need to define one explicitly. An optional built-in definition can be imported as such:

```
@Import(me.ehp246.aufjms.api.spi.JacksonConfig.class)
```


**Declare an interface using `@ByRest`.**

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
    void patch(@RequestParam("timeUnit") String timeUnit, @RequestParam("timeDelta") int timeAmount);
}
```

**DELETE**

```
@ByRest("${api.base}")
public interface TimeServer {
   void delete(@RequestParam("timerName") String timerName);
}
```

**PUT**

```
@ByRest("${api.base}")
public interface TimeServer {
    void put(@RequestParam("timerName") String timerName);
}
```

Details can be found at the project's [Wiki](https://github.com/ehp246/auf-rest/wiki).

## Runtime
The latest version requires the following to run:
* Log4j 2
* Jackson 2: core and databind
* Spring 5: beans, context, and web
* JDK 17

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
