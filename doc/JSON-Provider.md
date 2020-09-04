While it's designed to support multiple JSON providers, as of now Jackson is the one used during development and testing. Jackson's core and databind modules are required at the runtime. 

During boot-up the framework looks for an `ObjectMapper` bean in Spring bean factory. Spring Boot will register one if Jackson is on the class path. So most of the time, this bean registered by Spring Boot will be used by the framework.

If one is found, the framework uses the found as the provider. If there is no such a bean, the framework will configure a private `ObjectMapper` for its use. For this private instance, the framework tries to include [jsr310](https://github.com/FasterXML/jackson-modules-java8) and [mrbean](https://github.com/FasterXML/jackson-modules-base/tree/master/mrbean) modules if they are present on the class-path. But the two modules are not required.

If you would like to see additional providers supported, please file an issue. I'd be happy to add them.
