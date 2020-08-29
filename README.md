# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
The framework is aimed at Spring-based applications that need to implement a REST client to some other services/applications. It offers an annotation-driven, declarative implementation approach similar to what Spring Data Repository offers. It abstracts away most underline HTTP/REST concerns by offering a set of annotations and conventions with which the developers declare their intentions at a higher, application level via plain Java interfaces. They don't need to dictate in an imperative way every detail on how a HTTP client should be created, requests sent, responses processed, and exceptions handled. The framework takes care of these chores for the developers so they can focus on application logic. The framework can reduce the code base of an application significantly by replacing commonly-seen HttpClient/RestTemplate-based helper/util classes. These Template and Util classes are largely repetitive, difficult to test, error prone. Because of the interface-centered implementation approach, the framework makes implementing unit tests much easier. There is little need for heavy yet brittle mocking.

## Quick Start

Add Maven dependency. See [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).

Enable the functionality by annotating your Spring application class with `@EnableByRest`

```
@SpringBootApplication
@EnableByRest
class PostmanApplication {
	public static void main(final String[] args) {
		SpringApplication.run(PostmanApplication.class, args);
	}
}
```

Declare an interface that is annotated by `@ByRest` in the same or a sub package.

```
@ByRest("${postman.echo.base}/get")
public interface GetProxy {
	EchoResponseBody get();
}
```
By this point, you have just implemented a HTTP GET request that
* has the URL configured by a Spring property
* takes no parameter
* returns the response body as a Java object


To use the implementation, inject the interface as any other Spring bean.

```
@RestController
public class ProxyController {
	@Autowired
	GetProxy get;
	
	@GetMapping(path = "get", produces = MediaType.APPLICATION_JSON_VALUE)
	public EchoResponseBody get() {
		// A simple forward
		return get.getAsEchoBody();
	}
}
```


The following are a few examples of different use cases.

```
@ByRest("${postman.echo.base}/post")
public interface PostProxy {
	EchoResponseBody post(NewBorn newBorn);
}

@ByRest("${postman.echo.base}/patch")
public interface PatchProxy {
	EchoResponseBody patch(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName);
}

@ByRest("${postman.echo.base}/delete")
public interface DeleteProxy {
	EchoResponseBody delete(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName);
}

@ByRest("${postman.echo.base}/put")
public interface PutProxy {
	EchoResponseBody put(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			NewBorn newBorn);
}
```

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

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
