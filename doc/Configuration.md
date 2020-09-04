The framework supports the following application configuration properties.

# Connect Timeout

```
me.ehp246.aufrest.connectTimeout
```
It defines, in milliseconds, how long to wait for a connection to a domain host. The default is 15000 milliseconds.


The value is passed to the HttpClient builder. See the details [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.Builder.html#connectTimeout(java.time.Duration)).


# Response Timeout
```
me.ehp246.aufrest.responseTimeout
```
It defines, in milliseconds, how long to wait for a response on a request. The default is 30000 milliseconds.

See the details [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.Builder.html#timeout(java.time.Duration)).

