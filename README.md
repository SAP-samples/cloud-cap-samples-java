<!-- omit in toc -->
# Welcome to CAP Samples for Java

Welcome to the bookshop-java project. It demonstrates how to build business applications using the [CAP Java SDK](https://cap.cloud.sap) providing a book shop web application as an example. The application in this project enables browsing books, managing books, and managing orders.

![Application Overview in Fiori Launchpad](assets/readmeImages/FioriHome.jpg)

<!-- omit in toc -->
## Outline

- [Overview](#overview)
  - [Demonstrated Features](#demonstrated-features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Using Eclipse](#using-eclipse)
    - [Building and Running](#building-and-running)
  - [Building and Running](#building-and-running)
  - [Database Setup and Spring Profiles](#database-setup-and-spring-profiles)
  - [Using a File-Based SQLite Database](#using-a-file-based-sqlite-database)
- [Get Support](#get-support)
- [License](#license)

# Overview

This sample application shows how to conveniently create business applications based on **CDS domain models**, persisting data with **SQLite**, or **SAP HANA**, and exposing an **OData V4** frontend with an **SAP Fiori** frontend on top.

This sample uses Spring Boot as an **application framework**. Although a CAP Java application isn’t required to build on Spring Boot, it’s the first choice of framework, as it’s seamlessly integrated.

The **domain models** are defined using [CDS entity definitions](https://cap.cloud.sap/docs/cds/cdl#entity-and-type-definitions).

By default, an in-memory or optionally a file-based SQLite database is used for **data persistency**. Once productively deployed to SAP Cloud Platform, SAP HANA can be used.

**Services** are defined using [CDS Service Models](https://cap.cloud.sap/docs/cds/cdl#services). The **OData V4 Protocol Adapter** translates the CDS service models into corresponding OData schemas and maps the incoming OData requests to the corresponding CDS services.

Although CAP provides generic **event handlers** to serve most CRUD requests out-of-the-box, it’s possible to add business logic through [Custom Event Handlers](https://cap.cloud.sap/docs/get-started/in-a-nutshell#adding-custom-logic).

A Fiori UI is added using predefined SAP Fiori elements templates. **[Fiori annotations](https://cap.cloud.sap/docs/guides/fiori/#fiori-annotations)** add information to the service definitions, on how to render the data.

## Demonstrated Features

Framework and Infrastructure-related Features:

- [Application configuration](https://cap.cloud.sap/docs/java/development#application-configuration) for Spring and CDS using [application.yaml](srv/src/main/resources/application.yaml)
- [Mocking users](/srv/src/main/resources/application.yaml) for local development
- [Authentication & Authorization](https://cap.cloud.sap/docs/java/advanced#security) (including user-specific restrictions with `@restrict` in the [Admin Service](/srv/admin-service.cds))

Domain Model related Features:

- [CDS Query Language with a Static CDS Model](https://cap.cloud.sap/docs/java/advanced#staticmodel) in the [Admin Service](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- Use of [Aspects](https://cap.cloud.sap/docs/cds/cdl#aspects) in the Model Definition such as the [`managed` or `cuid` Aspect](https://cap.cloud.sap/docs/cds/common#common-reuse-aspects) in [Books](db/schema.cds)
- [Data Localization](https://cap.cloud.sap/docs/guides/localized-data) for [Books](db/schema.cds)

Service Model related Features:

- [Custom event handlers](https://cap.cloud.sap/docs/java/provisioning-api) such as the [Custom business logic for the Admin Service](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- [Custom actions](https://cap.cloud.sap/docs/cds/cdl#actions) such as `addToOrder` in the [Admin Service](/srv/admin-service.cds). The Action implementation is in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- Add annotations for [searchable elements](https://github.wdf.sap.corp/pages/cap/java/query-api#select) in the [Admin Service](srv/admin-service.cds)
- [Localized Messages](https://cap.cloud.sap/docs/java/provisioning-api#indicating-errors) in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)

User Interface related Features:

- Support for SAP [Fiori Elements](https://cap.cloud.sap/docs/guides/fiori/#fiori-draft-support)
- [Fiori Draft based Editing](https://cap.cloud.sap/docs/guides/fiori/#fiori-draft-support) for [Books and Authors](srv/admin-service.cds)
- [Fiori annotations](https://cap.cloud.sap/docs/guides/fiori/#fiori-annotations) specific for [Browse Books](app/browse/fiori-service.cds), [Manage Books](app/admin/fiori-service.cds), [Manage Orders](app/orders/fiori-service.cds) and [common annotations](app/common.cds), which apply to all UI's
- UI Annotations for custom actions in the [Manage Books](app/admin/fiori-service.cds) UI, including annotations for a button and a popup
- [Value Help](https://cap.cloud.sap/docs/cds/annotations#odata) for [Books](app/orders/fiori-service.cds) and [Authors](app/common.cds)
- [Model Localization](https://cap.cloud.sap/docs/guides/i18n) for [English](app/_i18n/i18n.properties) and [German](app/_i18n/i18n_de.properties) language for static texts

CDS Maven Plugin Features:

- Install [Node.js](srv/pom.xml#L87) in the specified version
- Install the latest version of [@sap/cds-dk](srv/pom.xml#L97)
- Execute arbitrary [CDS](srv/pom.xml#L107) commands
- [Generate](srv/pom.xml#L122) Java POJOs for type-safe access to the CDS model
- [Clean](srv/pom.xml#L80) project from artifacts of the previous build

# Getting Started

The following sections describe how to set up, build, and run the project.

## Prerequisites

Make sure you have set up a development environment (that means, you’ve installed the CDS Compiler, Java, and Apache Maven) [as described here](https://cap.cloud.sap/docs/java/getting-started).

## Clone Build & Run

1.  Clone the project:

```bash 
  git clone https://github.com/SAP-samples/cloud-cap-samples-java.git
```

2. Build and run the application:

```
  mvn spring-boot:run
```

## Using Eclipse

Optionally, use the following steps to import the project to Eclipse:

1.  Import the project using **File > Import > Existing Maven Projects**.
    
    Now, you should see the projects **bookshop** and **bookshop-parent** in the project/package explorer view.

2.  In Project Explorer, change the property "Package Presentation" from "Flat" to "Hierarchical" for better understanding.


### Building and Running

1.  To **compile** the project, right-click the file `pom.xml` in the `bookshop-parent` project root folder and select
**Run as** > **Maven build**.

    In the following dialog, enter the string `clean install` into the field labeled with "Goals" and click "Run".

    Note: This step also compiles the CDS artifacts, thus repeat this once you made changes to the CDS model. This step also generates source files, therefore refresh the "bookshop" project in your IDE.

2.  To **run** the application, right-click the `bookshop` project root in the Package Explorer and select **Run as** > **Spring Boot App** (make sure you have [Spring Tools 4 installed](https://marketplace.eclipse.org/content/spring-tools-4-aka-spring-tool-suite-4)).

    This step creates a default Run Configuration named `Bookshop - Application` and starts the application afterwards. To go on with the next step, stop the application again.

3.  Then, set the default working directory by editing your Run Configuration via **Run** > **Run Configurations** > **Bookshop - Application**. On the tab **Arguments** change the default **Working Directory** to:

	```${workspace_loc:bookshop-parent}```

	Afterwards, click **Run**. This step starts the applications `main` method located in `src/main/java/my/bookshop/Application.java`.

4.  Use the following links in the browser to check if everything works fine:

    <http://localhost:8080/>: This should show the automatically generated index page of served paths.
    <http://localhost:8080/fiori.html>: This is the actual bookshop application UI

    You'll start with an empty stock of books as this procedure starts the bookshop application with an empty in-memory sqlite database.

    Two mock users are defined for local development:
    - User: `user`, password: `user` to browse books
    - User: `admin`, password: `admin` to manage books and orders

## Database Setup and Spring Profiles

The application comes with three predefined profiles: `default`, `sqlite`, and `cloud` (see `src/main/resources/application.yaml`).

- The `default` profile specifies to use an in-memory SQLite database.
  The in-memory database is set up automatically during startup of the application.
  However, example data from CSV files aren’t yet automatically imported, therefore some content needs to be created via OData V4 API requests.

- The `sqlite` profile specifies to use a persistent SQLite database from root directory of the project.
  This database needs to be created first, to ensure it’s initialized with the correct schema and with the CSV-based example data.
  To initialize the database, simply run `cds deploy` from the project's root directory. Repeat this step, once you make changes to the CDS model.

- When deploying the application to the CloudFoundry, the CF Java Buildpack automatically configures the `cloud` Spring profile.
  This profile doesn’t specify any datasource location. In that case CAP Java can automatically detect HANA service bindings available in the environment.

## Using a File-Based SQLite Database

To switch from the default in-memory SQLite database to a file-based SQLite database in this sample application perform the following steps:

1.  Deploy the example data stored in .csv files in the folder ``db/data`` to a file-based SQLite database by executing the command-line utility

    ```cds deploy```

    from your project root folder.

2.  Edit your Run Configuration via **Run** > **Run Configurations...** and select enter the **Profile** `sqlite` on tab **Spring** and click **Run**.

# Get Support

Check out the documentation at [https://cap.cloud.sap](https://cap.cloud.sap).
In case you have a question, find a bug, or otherwise need support, please use our [community](https://answers.sap.com/tags/9f13aee1-834c-4105-8e43-ee442775e5ce).

# License

Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.
