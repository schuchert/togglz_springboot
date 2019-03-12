# Togglz in Springboot

This is a trivial project to demonstrate how to quickly get a basline version of togglz running in springboot.

## Jumping in

Here's a quick summary of points of interest:
* Copy the dependencies from [build.gradle](https://github.com/schuchert/togglz_springboot/blob/master/build.gradle) (example below but as this is a copy, refer to original version)
```
    compile("org.togglz:togglz-spring-boot-starter:2.6.1.Final")
    compile("org.togglz:togglz-console:2.6.1.Final")
    compile("org.togglz:togglz-servlet:2.6.1.Final")
    compile("org.togglz:togglz-spring-web:2.6.1.Final")
    compile("org.togglz:togglz-spring-security:2.6.1.Final")
```

* Create an enumeration with your toggles (see [FeatureTogglz.java](https://github.com/schuchert/togglz_springboot/blob/master/src/main/java/org/shoe/togglz/FeatureToggles.java)

* Make sure the FeatureToggle class is registered in spring (see [Spring Configuration]()https://github.com/schuchert/togglz_springboot/blob/master/src/main/java/org/shoe/togglz/SystemConfiguration.java)
```
    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(FeatureToggles.class);
    }
```

* Enable the togglz console (See [application-local.yml](https://github.com/schuchert/togglz_springboot/blob/master/src/main/resources/application-local.yml)
```
togglz:
  console:
    enabled: true
    secured: false
    useManagementPort: false
```

## Context
* The structure of the project mirros how many projects were using spring security at the customer
* We had a number of teams working on feature toggles by hand-rolling their own
* When looking into this, I found several examples (5 I think) that gave the basics, but none of them worked with the versions of springboot in use (1.5.x and 2.1.x). The failure was at runtime dealing with incmpatible class versions. Based on that, the single most important part of this examle is a complete list of dependnecies to avoid this runtime issu.

## Intent
* Show a concrete, simple example
* The example should be close-enough for how people are currently working so they can easily use this as a baseline
