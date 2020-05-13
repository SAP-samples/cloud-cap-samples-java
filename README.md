# Welcome to CAP Samples for Java

Welcome to the bookshop-java project. It demonstrates a simple application built on the Java SDK for the [SAP Cloud Application Programming Model](https://cap.cloud.sap).

# Getting Started

The following sections describe how to set up, build and run the project.

## Prerequisites

Make sure you have setup a development environment (that means, you have installed the CDS Compiler, Java and Apache Maven) [as described here](https://cap.cloud.sap/docs/java/getting-started).

## Importing the Project in Eclipse

1.  Clone the project:
```
  git clone https://github.com/SAP-samples/cloud-cap-samples-java.git
```
2.  Import the project using **File > Import > Existing Maven Projects**. 
    
    Now, you should see the projects **bookshop** and **bookshop-parent** in the project/package explorer view.
3.  In Project Explorer, change the property "Package Presentation" from "Flat" to "Hierarchical" for better understanding.

## Building and Running

1.  To **compile** the project, right-click the file `pom.xml` in the `bookshop-parent` project root folder and select 
**Run as** > **Maven build**. 

    In the following dialog, enter the string `clean install` into the field labeled with "Goals" and click "Run".

    Note: This step also compiles the CDS artifacts, thus repeat this once you made changes to the CDS model. This step also generates source files, therefore refresh the "bookshop" project in your IDE.

2.  To **run** the application, right-click the `bookshop` project root in the Package Explorer and select **Run as** > **Spring Boot App** (make sure you have [Spring Tools 4 installed](https://marketplace.eclipse.org/content/spring-tools-4-aka-spring-tool-suite-4)).

    This step creates a default Run Configuration named `Bookshop - Application` and starts the application afterwards. To go on with the next step, stop the application again.

3.  Then, set the default working directory by editing your Run Configuration via **Run** > **Run Configurations** > **Bookshop - Application**. On the tab **Arguments** change the default **Working Directory** to:

	```${workspace_loc:bookshop-parent}```

	Afterwards, click **Run**. This step starts the applications `main` method located in `src/main/java/my/bookshop/Application.java`.
    
3.  Use the following links in the browser to check if everything works fine:

    <http://localhost:8080/>: This should show the automatically generated index page of served paths.  
    <http://localhost:8080/fiori.html>: This is the actual bookshop application UI

    You'll start with an empty stock of books as this procedure starts the bookshop application with an empty in-memory sqlite database. Two mock users are defined for local development:

    - User: `user`, password: `user` to browse books
    - User: `admin`, password: `admin` to manage books and orders

## Database Setup and Spring Profiles

The application comes with three predefined profiles: `default`, `sqlite` and `cloud` (see `src/main/resources/application.yaml`).

- The `default` profile specifies to use an in-memory SQLite database. 
  The in-memory database is set up automatically during startup of the application. 
  However, example data from CSV files are not yet automatically imported, therefore some content needs to be created via OData V4 API requests.

- The `sqlite` profile specifies to use a persistent SQLite database from root directory of the project.
  This database needs to be created first, to ensure it is initialized with the correct schema and with the CSV-based example data.
  To initialize the database, simply run `cds deploy` from the project's root directory. Repeat this step, once you make changes to the CDS model.

- When deploying the application to the CloudFoundry, the CF Java Buildpack automatically configures the `cloud` Spring profile.
  This profile does not specify any datasource location. In that case CAP Java can automatically detect HANA service bindings available in the environment.

## Using a File-Based SQLite Database

To switch from the default in-memory SQLite database to a file-based SQLite database in this sample application perform the following steps:

1.  Deploy the example data stored in .csv files in the folder ``db/data`` to a file-based SQLite database by executing the command-line utility

    ```cds deploy```

    from your project root folder.

2.  Edit your Run Configuration via **Run** > **Run Configurations...** and select enter the **Profile** `sqlite` on tab **Spring** and click **Run**.

## Demonstrated Features

- Application configuration
- How to add custom event handlers
- Authentication & Authorization
- Mocking users for local development
- Localization
- Support for SAP Fiori Elements and Drafts

## Get Support

Check out the documentation at https://cap.cloud.sap.
In case you find a bug or need support, please [open an issue in here](https://github.com/SAP-samples/cloud-cap-samples-java/issues/new).

## License

Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.
