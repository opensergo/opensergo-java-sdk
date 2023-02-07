<img src="https://user-images.githubusercontent.com/9434884/197435179-bbda0a82-6bae-485e-ac1a-490fee91a002.png" alt="OpenSergo Logo" width="50%">

# OpenSergo Java SDK

[![OpenSergo Java SDK CI](https://github.com/opensergo/opensergo-java-sdk/actions/workflows/ci.yml/badge.svg)](https://github.com/opensergo/opensergo-java-sdk/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.opensergo/opensergo-java-sdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.opensergo%22%20AND%20a:%22opensergo-java-sdk%22)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Introduction

## Documentation

See the [OpenSergo Website](https://opensergo.io/) for the official website of OpenSergo.

See the [中文文档](https://opensergo.io/zh-cn/) for document in Chinese.

## Quick Start

1. Add dependency in Maven `pom.xml`:

```xml
<!-- replace here with the latest version -->
<dependency>
    <groupId>io.opensergo</groupId>
    <artifactId>opensergo-java-sdk</artifactId>
    <version>0.1.0-beta2</version>
</dependency>
```

2. Subscribe data from OpenSergo control plane:

```java
// 1. Create client with remote host and port of OpenSergo control plane
OpenSergoClient client = OpenSergoClientManager.get().getOrCreateClient(host, port);
// 2. Start the OpenSergo client.
client.start();

// 3. Subscribe the config of the target (namespace, appName, kind)
client.subscribeConfig(new SubscribeKey("default", "my-service", configKind),
    new OpenSergoConfigSubscriber() {
        @Override
        public boolean onConfigUpdate(SubscribeKey subscribeKey, Object dataList) {
            // Handle received config here
            System.out.println("key: " + subscribeKey + ", data: " + dataList);
            return true;
        }
    });
```