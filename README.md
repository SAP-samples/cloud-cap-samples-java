<!-- omit in toc -->
# Welcome to CAP Samples for Java

![CI status](https://github.com/SAP-samples/cloud-cap-samples-java/workflows/Java%20CI%20with%20Maven/badge.svg)
[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/cloud-cap-samples-java)](https://api.reuse.software/info/github.com/SAP-samples/cloud-cap-samples-java)

Welcome to the PostgreSQL bookshop-java sample. It demonstrates how to build business applications using the [CAP Java SDK](https://cap.cloud.sap) using PostgreSQL. The application in this project enables browsing books and managing books.

<!-- omit in toc -->
## Outline

- [Overview](#overview)
  - [Demonstrated Features](#demonstrated-features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Clone Build & Run](#clone-build--run)
  - [Database Setup](#database-setup)
  - [Deploy to SAP Business Technology Platform, Cloud Foundry](#deploy-to-sap-business-technology-platform-cloud-foundry)
- [Get Support](#get-support)
- [License](#license)


# Overview

This sample application shows how to conveniently create business applications based on **CDS domain models**, persisting data with **PostgreSQL**, and exposing an **OData V4** frontend with an **SAP Fiori** frontend on top.

This sample uses Spring Boot as an **application framework**. Although a CAP Java application isn’t required to build on Spring Boot, it’s the first choice of framework, as it’s seamlessly integrated.

The **domain models** are defined using [CDS entity definitions](https://cap.cloud.sap/docs/cds/cdl#entity-and-type-definitions).

**Services** are defined using [CDS Service Models](https://cap.cloud.sap/docs/cds/cdl#services). The **OData V4 Protocol Adapter** translates the CDS service models into corresponding OData schemas and maps the incoming OData requests to the corresponding CDS services.

A SAP Fiori UI is added using predefined SAP Fiori elements templates. **[SAP Fiori annotations](https://cap.cloud.sap/docs/advanced/fiori#fiori-annotations)** add information to the service definitions, on how to render the data.

## Demonstrated Features

Framework and Infrastructure related Features:

- [PostgreSQL database support](#database-setup) with Liquibase for schema evolution.
- [Application configuration](https://cap.cloud.sap/docs/java/development#application-configuration) for Spring and CDS using [application.yaml](srv/src/main/resources/application.yaml)
- [Mocking users](/srv/src/main/resources/application.yaml) for local development
- [Authentication & Authorization](https://cap.cloud.sap/docs/java/security) (including user-specific restrictions with `@restrict` in the [Admin Service](/srv/admin-service.cds))
- [Cloud Foundry Deployment using MTA](https://cap.cloud.sap/docs/advanced/deploy-to-cloud#deploy-using-mta) with XSUAA [Service Bindings](mta.yaml)
- Application Router configuration including authentication via the XSUAA Service. See [package.json](app/package.json), [xs-app.json](app/xs-app.json) and [xs-security.json](xs-security.json)

Domain Model related Features:

- Use of [Aspects](https://cap.cloud.sap/docs/cds/cdl#aspects) in the Model Definition such as the [`managed` or `cuid` Aspect](https://cap.cloud.sap/docs/cds/common#common-reuse-aspects) in [Books](db/schema.cds)
- [Input validation](https://cap.cloud.sap/docs/cds/annotations#input-validation) using model annotation `@assert.format`
- [Data Localization](https://cap.cloud.sap/docs/guides/localized-data) for [Books](db/schema.cds)

User Interface related Features:

- Support for [SAP Fiori Elements](https://cap.cloud.sap/docs/advanced/fiori)
- [SAP Fiori Draft based Editing](https://cap.cloud.sap/docs/advanced/fiori#draft-support) for [Books](srv/admin-service.cds)
- [SAP Fiori annotations](https://cap.cloud.sap/docs/advanced/fiori#fiori-annotations) specific for [Browse Books](app/browse/fiori-service.cds) and [Manage Books](app/admin/fiori-service.cds) UIs
- [Value Help](https://cap.cloud.sap/docs/cds/annotations#odata) for [Authors](app/common.cds)
- [Model Localization](https://cap.cloud.sap/docs/guides/i18n) for [English](app/_i18n/i18n.properties) and [German](app/_i18n/i18n_de.properties) language for static texts

CDS Maven Plugin Features:

- Install [Node.js](srv/pom.xml#L161) in the default version.
- Execute arbitrary [npm](srv/pom.xml#L161) commands.
- [Generate](srv/pom.xml#L193) Java POJOs for type-safe access to the CDS model.
- [Clean](srv/pom.xml#L154) project from artifacts of the previous build.

# Getting Started

The following sections describe how to set up, build, and run the project.

## Prerequisites

Make sure you have set up a development environment (that means, you’ve installed the CDS Compiler, Java and Apache Maven) [as described here](https://cap.cloud.sap/docs/java/getting-started). This sample also requires Docker Desktop or compatible alternative as described in [testcontainers library documentation](https://java.testcontainers.org/supported_docker_environment/).

## Clone Build & Run

1.  Clone the project:

```bash
  git clone https://github.com/SAP-samples/cloud-cap-samples-java.git
```

2. Build and run the application:

```bash
  mvn spring-boot:test-run
```

In the IDE use the [TestApplication](srv/src/test/java/my/bookshop/config/TestApplication.java) to run the application with the PostgreSQL container created by testcontainer.

## Database Setup

This application is built to use only PostgreSQL as the database. It is used in the tests, can be started locally and, when deployed to SAP BTP, can use PostgreSQL service available there.

This sample uses [Liquibase](https://www.liquibase.com) for deployment and evolution of database schema. You can find more on that in [CAP Database Guide](https://cap.cloud.sap/docs/guides/databases-postgres).

To start the application with built-in PostgresSQL container run `mvn spring-boot:test-run`. Make sure that you have Docker Desktop or compatible alternative up and running. You will see that application created PostgreSQL container, deployed the database there and your application is up and running.

Regular run with `mvn spring-boot:run` will require datasource to be specified explicitly. You may adapt the default configuration if you want to use this sample with persistent database or with PostgreSQL deployed in the cloud.

Unit tests require Docker environment and will fail if executed without one.

## Deploy to SAP Business Technology Platform, Cloud Foundry

CAP Java applications can be deployed to the SAP Business Technology Platform as single tenant application.

Prerequisites:
- Install the [Cloud MTA Build Tool](https://sap.github.io/cloud-mta-build-tool/): `npm install -g mbt`.
- Install the [Cloud Foundry Command Line Interface](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html).
- Get an SAP Business Technology Platform account to deploy the services and applications.
- Ensure you have an entitlement for _PostgreSQL, hyperscaler option_ with appropriate plan in the same space.
- Ensure that your CF instances are connected to Internet to download SAPMachine JRE 17 as it is available in `sap_java_buildpack` in online mode only and you run in the landscape where the _PostgreSQL, hyperscaler option_ is available.

Deploy Application:
- Run `mbt build`
- Run `cf login`
- Run `cf deploy mta_archives/bookshop-pg_1.0.0.mtar`

# Get Support

In case you have a question, find a bug, or otherwise need support, please use our [community](https://answers.sap.com/tags/9f13aee1-834c-4105-8e43-ee442775e5ce). See the documentation at [https://cap.cloud.sap](https://cap.cloud.sap) for more details about CAP.

# License

Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSES/Apache-2.0.txt) file.
