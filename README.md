<!-- omit in toc -->
# Welcome to CAP Samples for Java

Welcome to the *bookshop-java* project. It demonstrates how to build business applications using the [CAP Java SDK](https://cap.cloud.sap) by SAP. This application maintains a database of books and authors and provides a UI to browse and manage available books and orders:

![Application Overview in Fiori Launchpad](assets/readmeImages/FioriHome.jpg)

<!-- omit in toc -->
## Outline
- [Overview](#overview)
- [Demonstrated Features](#demonstrated-features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Clone, Build & Run](#clone-build--run)
  - [Using the Application](#using-the-application)
  - [Using Eclipse](#using-eclipse)
  - [Database Setup and Spring Profiles](#database-setup-and-spring-profiles)
  - [Using a File-Based SQLite Database](#using-a-file-based-sqlite-database)
- [Get Support](#get-support)
- [License](#license)

# Overview

This sample demonstrates how to write [*CAP Java applications*](http://cap.cloud.sap/java) in combination with the [*Spring Boot*](https://spring.io/projects/spring-boot) framework. Although CAP Java applications aren't required to build on Spring Boot, it’s the first choice of framework, as it’s seamlessly integrated.

By default, data is stored in an in-memory or optionally a file-based *SQLite* database. Once productively deployed to the SAP Cloud Platform, you can easily switch to *SAP HANA*.  

*Domain modelling* is achieved using [*CDS entity definitions*](https://cap.cloud.sap/docs/cds/cdl#entity-and-type-definitions). The public API of CAP services is defined by [*CDS service models*](https://cap.cloud.sap/docs/cds/cdl#services). Eventually, that data is exposed using the *OData V4* protocol.

Custom business logic is implemented by so called [*custom event handlers*](https://cap.cloud.sap/docs/get-started/in-a-nutshell#adding-custom-logic), while most *CRUD requests* are served out-of-the-box.

A [*SAP UI5*](https://sapui5.hana.ondemand.com) based frontend uses predefined SAP Fiori Elements templates. [*Fiori annotations*](https://cap.cloud.sap/docs/guides/fiori/#fiori-annotations) add information to the service definitions, on how to render the data.

# Demonstrated Features

Framework and infrastructure-related features:

- [Application configuration](https://cap.cloud.sap/docs/java/development#application-configuration) for Spring and CDS using [application.yaml](srv/src/main/resources/application.yaml)
- [Mocking users](/srv/src/main/resources/application.yaml) for local development
- [Authentication & Authorization](https://cap.cloud.sap/docs/java/advanced#security) (including user-specific restrictions with `@restrict` in the [Admin Service](/srv/admin-service.cds))

Domain model related features:

- [CDS Query Language with a Static CDS Model](https://cap.cloud.sap/docs/java/advanced#staticmodel) in the [Admin Service](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- Use of [Aspects](https://cap.cloud.sap/docs/cds/cdl#aspects) in the Model Definition such as the [`managed` or `cuid` Aspect](https://cap.cloud.sap/docs/cds/common#common-reuse-aspects) in [Books](db/schema.cds)
- [Data Localization](https://cap.cloud.sap/docs/guides/localized-data) for [Books](db/schema.cds)

Service model related features:

- [Custom event handlers](https://cap.cloud.sap/docs/java/provisioning-api) such as the [Custom business logic for the Admin Service](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- [Custom actions](https://cap.cloud.sap/docs/cds/cdl#actions) such as `addToOrder` in the [Admin Service](/srv/admin-service.cds). The Action implementation is in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- Add annotations for [searchable elements](https://github.wdf.sap.corp/pages/cap/java/query-api#select) in the [Admin Service](srv/admin-service.cds)
- [Localized Messages](https://cap.cloud.sap/docs/java/provisioning-api#indicating-errors) in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)

User interface related features:

- Support for SAP [Fiori Elements](https://cap.cloud.sap/docs/guides/fiori/#fiori-draft-support)
- [Fiori Draft based Editing](https://cap.cloud.sap/docs/guides/fiori/#fiori-draft-support) for [Books and Authors](srv/admin-service.cds)
- [Fiori annotations](https://cap.cloud.sap/docs/guides/fiori/#fiori-annotations) specific for [Browse Books](app/browse/fiori-service.cds), [Manage Books](app/admin/fiori-service.cds), [Manage Orders](app/orders/fiori-service.cds) and [common annotations](app/common.cds), which apply to all UI's
- UI Annotations for custom actions in the [Manage Books](app/admin/fiori-service.cds) UI, including annotations for a button and a popup
- [Value Help](https://cap.cloud.sap/docs/cds/annotations#odata) for [Books](app/orders/fiori-service.cds) and [Authors](app/common.cds)
- [Model Localization](https://cap.cloud.sap/docs/guides/i18n) for [English](app/_i18n/i18n.properties) and [German](app/_i18n/i18n_de.properties) language for static texts


# Getting Started

The following sections describe how to set up, build, and run the project.

## Prerequisites

Make sure you have set up a development environment (that means, you’ve installed the CDS Compiler, Java, and Apache Maven) [as described here](https://cap.cloud.sap/docs/java/getting-started).

## Clone, Build & Run

1.  Clone the project:

  ```bash 
  git clone https://github.com/SAP-samples/cloud-cap-samples-java.git
  ```

2. Build and run the application:

  ```
  mvn spring-boot:run
  ```

## Using the Application    

Try it out using the following URLs:
   
-  <http://localhost:8080/>: shows the automatically generated index page of served paths.
- <http://localhost:8080/fiori.html>: shows the graphical user interface

You'll start with an empty stock of books as this procedure starts the bookshop application with an empty in-memory SQLite database.

Log in with one of the two mock users that are pre-defined for local development:
  - To browse books, login in with user: `user`, password: `user`
  - To manage bookd and orders, log in with User: `admin`, password: `admin`

## Using Eclipse

Optionally, use the following steps to import the project in Eclipse:

1.  **Import** the project using **File > Import > Existing Maven Projects**.
    
    Now, you should see the projects *bookshop* and *bookshop-parent* in the Project Explorer view.

    > In Project Explorer, change the property "Package Presentation" from "Flat" to "Hierarchical" for better understanding.

2.  To **build** the project, right-click on the **bookshop-parent** project root folder and select **Run as** > **Maven build**.

    In the following dialog, enter the string `clean install` into the field labeled with **Goals** and click **Run**.

    > This step also compiles the CDS artifacts, thus repeat this once you made changes to the CDS model. This step also generates source files, therefore refresh the "bookshop" project in your IDE.

3.  To **run** the application, right-click on the *bookshop* project root in the Package Explorer and select **Run as** > **Spring Boot App** (make sure you have [Spring Tools 4 installed](https://marketplace.eclipse.org/content/spring-tools-4-aka-spring-tool-suite-4)).

    > This step creates a default Run Configuration named *Bookshop - Application* and starts the application afterwards. To go on with the next step, stop the application again.

4.  Then, set the default working directory by editing your Run Configuration via **Run** > **Run Configurations** > **Bookshop - Application**. On the tab **Arguments** change the default **Working Directory** to:

	```${workspace_loc:bookshop-parent}```

	Afterwards, click **Run**. This step starts the applications `main` method located in *src/main/java/my/bookshop/Application.java*.

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

Check out the CAP documentation [here](https://cap.cloud.sap).
In case you find a bug or need support, please [open an issue in here](https://github.com/SAP-samples/cloud-cap-samples-java/issues/new).

# License

Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.
