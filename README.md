# Auf REST

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-rest)

## Introduction
## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-rest).
## Dependency
Auf REST is developed and tested on top of these:
* JDK 11
* Log4j2
* Jackson
* Spring 5
* Spring Boot 2

It requires the following modules to run:
* JDK 11
* Log4j2: api
* Jackson: core and databind
* Spring: beans, context and web

It does not require Spring Boot. It should work on Spring 4 but it's not been tested.

## Examples
## Logging
## Authentication
## Configuration
## Modularity
While Auf REST is not a Java module yet, the intention is to make it so as soon as I can figure out how to run the unit tests without an issue. Currently I'm having trouble to run unit tests by Maven because of in-accessible classes. If you can provide help, please let me know. It'd be much appreciated.

Once modularized, all exported classes will be under package:
* me.ehp246.aufrest.api

All code outside of this package is private.

