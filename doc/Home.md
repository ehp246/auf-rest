Welcome to Auf REST wiki!

`@ByRest`-annotated interfaces are implemented by the framework automatically and made available for injection/autowiring as Spring beans. Method invocations on the interface are transformed into HTTP requests and the responses received are returned as method returns. On application-level, these interfaces can be considered proxies of remote REST endpoints. The framework hides low-level details of HTTP request/response implementation so that the application can focus on high-level dependencies between micro-services.

If you are curious on the low-level implementation technique used by the framework, look into `java.lang.reflect.Proxy` class.