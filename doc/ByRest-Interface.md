`@ByRest`-annotated interfaces are implemented by the framework automatically and made available for injection/autowiring as Spring beans. Method invocations on the interfaces are transformed into HTTP requests and the responses received are returned either as normal return or thrown as an exception. 

The following is an interface that implements a simple GET request.
```java
@ByRest("${echo.base}/get")
interface EchoGetTestCase001 {
	EchoResponseBody get();
}
```