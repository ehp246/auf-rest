The annotation turns on the capability of the framework. It enables class-path scanning of `@ByRest`-annotated interfaces and registers them as injectable Spring beans. As the Enable annotations from Spring Boot, it's usually applied to the application class as below:

```java
@EnableByRest
@SpringBootApplication
class PostmanApplication {
	public static void main(final String[] args) {
		SpringApplication.run(PostmanApplication.class, args);
	}
}
```

The framework, by default, scans for `@ByRest` interfaces only in the same package and sub packages of the annotated class. Use `scan` element to specify packages to be scanned explicitly.