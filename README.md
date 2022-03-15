# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
Auf REST is aimed at Spring-based applications that need to implement REST clients of dependent services/applications. It offers an annotation-driven and declarative implementation approach similar to what Spring Data Repository offers. It abstracts away most underline HTTP/REST concerns by offering a set of annotations and conventions with which the developers declare the intentions at an application level via plain Java interfaces. They don't need to dictate in an imperative way the details on how a HTTP client should be created, requests sent, responses processed, and exceptions handled. The framework takes care of these chores for the developers so they can focus on application logic. The library can reduce the code base of an application significantly by removing commonly-seen HttpClient/RestTemplate-based helper/utility classes that are largely repetitive to implement, difficult to test and error prone. Because of the declarative approach based on plain Java interfaces, the library also makes implementing unit tests much easier as it removes the need for heavy and brittle mocking.

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

**Declare an interface using `@ByRest`.**

```
@ByRest("${api.base}")
public interface GetProxy {
    EchoResponseBody get();
}
```

**Inject and enjoy.**

```
@Service
public class ProxyService {
    // Do something with it
    @Autowired
    private GetProxy get;
    ...
}
```
By this point, you have implemented a GET request that
* has the URL defined by a Spring property
* takes no parameter
* returns the response body as a de-serialized Java object

<br>
The following are a few more examples.

**POST a JSON body from an object**
```
@ByRest("${api.base}")
public interface PostProxy {
    EchoResponseBody post(NewBorn newBorn);
}
```

**PATCH with query parameters**
```
@ByRest("${api.base}")
public interface PatchProxy {
    EchoResponseBody patch(@RequestParam("firstName") String firstName, 
        @RequestParam("lastName") String lastName);
}
```
**DELETE**
```
@ByRest("${api.base}")
public interface DeleteProxy {
    EchoResponseBody delete(@RequestParam("firstName") String firstName, 
        @RequestParam("lastName") String lastName);
}
```

**PUT**
```
@ByRest("${api.base}")
public interface PutProxy {
    EchoResponseBody put(@RequestParam("firstName") String firstName, 
        @RequestParam("lastName") String lastName,
	NewBorn newBorn);
}
```
For detailed documents, please see the project's Wiki.

## Dependency
Auf REST is developed and tested on top of these:
* Log4j 2
* Jackson
* Spring 5
* Spring Boot 2
* JDK 17 for v3
* JDK 11 for v2 and below

It requires the following to run:
* Log4j 2
* Jackson: core and databind
* Spring: beans, context and web
* JDK 17 for v3
* JDK 11 for v2 and below

It should work on Spring 4 but it's not been tested.

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
