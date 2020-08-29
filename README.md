# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
The framework is aimed at Spring-based applications that need to implement a REST client to some other services/applications. It offers an annotation-driven, declarative implementation approach similar to what Spring Data Repository offers. It abstracts away most underline HTTP/REST concerns by offering a set of annotations and conventions with which the developers declare their intentions at a higher, application level via plain Java interfaces. They don't need to dictate in an imperative way every detail on how a HTTP client should be created, requests sent, responses processed, and exceptions handled. The framework takes care of these chores for the developers so they can focus on application logic. The framework can reduce the code base of an application significantly by replacing commonly-seen HttpClient/RestTemplate-based helper/util classes. These Template and Util classes are largely repetitive, difficult to test, error prone. Because of the interface-centered implementation approach, the framework makes implementing unit tests much easier. There is little need for heavy yet brittle mocking.

## Quick Start

**Add [Maven dependency](https://mvnrepository.com/artifact/me.ehp246/auf-rest).**

**Enable the framework using `@EnableByRest`.**

```
@EnableByRest
@SpringBootApplication
class PostmanApplication {
	public static void main(final String[] args) {
		SpringApplication.run(PostmanApplication.class, args);
	}
}
```

**Declare an interface using `@ByRest`.**

```
@ByRest("${postman.echo.base}/get")
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
* has the URL configured by a Spring property
* takes no parameter
* returns the response body de-serialized as a Java object

<br>
The following are a few more examples.

**POST with an object for JSON body**
```
@ByRest("${postman.echo.base}/post")
public interface PostProxy {
	EchoResponseBody post(NewBorn newBorn);
}
```

**PATCH with query parameters**
```
@ByRest("${postman.echo.base}/patch")
public interface PatchProxy {
	EchoResponseBody patch(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName);
}
```
**DELETE**
```
@ByRest("${postman.echo.base}/delete")
public interface DeleteProxy {
	EchoResponseBody delete(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName);
}
```

**PUT**
```
@ByRest("${postman.echo.base}/put")
public interface PutProxy {
	EchoResponseBody put(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			NewBorn newBorn);
}
```

For detailed documents, please see project's [wiki](https://github.com/ehp246/auf-rest/wiki).

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

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
