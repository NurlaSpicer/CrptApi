# CrptApi Java Library

This project is a Java library for working with the Honest Sign API. The `CrptApi` class provides the ability to create documents for introducing goods produced in the Russian Federation into circulation, using HTTP requests to the API.

## Features

- **Thread-Safe**: The `CrptApi` class is implemented as thread-safe, ensuring safe operation with a limit on the number of requests to the API.
- **Request Limitation**: The ability to specify the maximum number of requests within a given time interval.
- **JSON Serialization**: The Jackson library is used for serializing objects into JSON when forming requests to the API.

## Installation

Add the following dependency to your `pom.xml`:

```xml
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Jackson Databind dependency -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.13.3</version> <!-- Make sure to use the latest version available -->
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.13.3</version> <!-- Make sure to use the latest version available -->
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.13.3</version> <!-- Make sure to use the latest version available -->
        </dependency>
    </dependencies>
