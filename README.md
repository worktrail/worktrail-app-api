# worktrail-app-api

Basic API for applications making use of WorkTrail Devleoper API
available at https://worktrail.net/en/api/

It implements the basic authentication mechanism and helper methods to
wrap API calls.


# Status

Implementation is ready to use - See worktrail-app-hub-sync for example usage:
https://github.com/worktrail/worktrail-app-hub-sync

Still open are some kind of documentation and publically runnable test suite.

# Usage

Entry point is net.worktrail.appapi.WorkTrailAppApi - see javadoc for more information.

http://www.javadoc.io/doc/net.worktrail/worktrail-app-api

You can install it from maven central:

    <dependency>
        <groupId>net.worktrail</groupId>
        <artifactId>worktrail-app-api</artifactId>
        <version>0.2.0</version>
    </dependency>

or for gradle:

    compile 'net.worktrail:worktrail-app-api:0.2.0'

# Development

    ./gradlew eclipse
    # import existing project into eclipse.

# License

worktrail-app-api is available under The BSD 2-Clause License. Please
contribute back by sending us pull requests on github:
https://github.com/worktrail/worktrail-app-api
