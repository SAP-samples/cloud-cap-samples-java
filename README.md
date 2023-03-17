<!-- omit in toc -->
# Welcome to CAP Samples for Java

![CI status](https://github.com/SAP-samples/cloud-cap-samples-java/workflows/Java%20CI%20with%20Maven/badge.svg)
[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/cloud-cap-samples-java)](https://api.reuse.software/info/github.com/SAP-samples/cloud-cap-samples-java)

Welcome to the bookshop-java project. It demonstrates how to build business applications using the [CAP Java SDK](https://cap.cloud.sap) providing a book shop web application as an example. The application in this project enables browsing books, managing books, and managing orders.

![Book Object Page](assets/readmeImages/BookPage.png)

<!-- omit in toc -->
## Outline

- [Overview](#overview)
  - [Demonstrated Features](#demonstrated-features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Clone Build & Run](#clone-build--run)
  - [Using Eclipse](#using-eclipse)
    - [Building and Running](#building-and-running)
  - [Using IntelliJ Idea (Community and Ultimate)](#using-intellij-idea-community-and-ultimate)
  - [Database Setup and Spring Profiles](#database-setup-and-spring-profiles)
  - [API_BUSINESS_PARTNER Remote Service and Spring Profiles](#api_business_partner-remote-service-and-spring-profiles)
  - [Deploy to SAP Business Technology Platform, Cloud Foundry](#deploy-to-sap-business-technology-platform-cloud-foundry)
  - [Deploy to SAP Business Technology Platform, Kyma Runtime](#deploy-to-sap-business-technology-platform-kyma-runtime)
  - [Setup Authorizations in SAP Business Technology Platform](#setup-authorizations-in-sap-business-technology-platform)
- [Code Tour](#code-tour)
- [Get Support](#get-support)
- [License](#license)


# Overview

This sample application shows how to conveniently create business applications based on **CDS domain models**, persisting data with **H2**, or **SAP HANA**, and exposing an **OData V4** frontend with an **SAP Fiori** frontend on top.

This sample uses Spring Boot as an **application framework**. Although a CAP Java application isn’t required to build on Spring Boot, it’s the first choice of framework, as it’s seamlessly integrated.

The **domain models** are defined using [CDS entity definitions](https://cap.cloud.sap/docs/cds/cdl#entity-and-type-definitions).

By default, an in-memory H2 database is used for **data persistency**. Once productively deployed to SAP Business Technology Platform, SAP HANA can be used.

**Services** are defined using [CDS Service Models](https://cap.cloud.sap/docs/cds/cdl#services). The **OData V4 Protocol Adapter** translates the CDS service models into corresponding OData schemas and maps the incoming OData requests to the corresponding CDS services.

Although CAP provides generic **event handlers** to serve most CRUD requests out-of-the-box, it’s possible to add business logic through [Custom Event Handlers](https://cap.cloud.sap/docs/get-started/in-a-nutshell#adding-custom-logic).

A SAP Fiori UI is added using predefined SAP Fiori elements templates. **[SAP Fiori annotations](https://cap.cloud.sap/docs/advanced/fiori#fiori-annotations)** add information to the service definitions, on how to render the data.

CAP provides built-in multitenancy support with out-of-the box tenant isolation. The sample application demonstrates usage of MTX sidecar based on [streamlined MTX](https://cap.cloud.sap/docs/guides/deployment/as-saas?impl-variant=java) and can be deployed as multitenant application. The [deprecated classic MTX](https://cap.cloud.sap/docs/java/multitenancy) setup is shown in the [mtx-classic branch](https://github.com/SAP-samples/cloud-cap-samples-java/tree/mtx-classic) for reference.

## Demonstrated Features

Framework and Infrastructure related Features:

- [Application configuration](https://cap.cloud.sap/docs/java/development#application-configuration) for Spring and CDS using [application.yaml](srv/src/main/resources/application.yaml)
- [Mocking users](/srv/src/main/resources/application.yaml) for local development
- [Authentication & Authorization](https://cap.cloud.sap/docs/java/security) (including user-specific restrictions with `@restrict` in the [Admin Service](/srv/admin-service.cds))
- [Cloud Foundry Deployment using MTA](https://cap.cloud.sap/docs/advanced/deploy-to-cloud#deploy-using-mta) with XSUAA [Service Bindings](mta-single-tenant.yaml)
- Application Router configuration including authentication via the XSUAA Service. See [package.json](app/package.json), [xs-app.json](app/xs-app.json) and [xs-security.json](xs-security.json)
- [Multitenancy configuration](https://cap.cloud.sap/docs/java/multitenancy) via [mta-multi-tenant.yaml](mta-multi-tenant.yaml), [.cdsrc.json](.cdsrc.json), [sidecar module](mtx-sidecar)
- [Feature toggles](https://cap.cloud.sap/docs/guides/extensibility/feature-toggles?impl-variant=java#limitations). In CF, features can be toggled by assigning the roles `expert` or `premium-customer` to the user.

Domain Model related Features:

- [CDS Query Language with a Static CDS Model](https://cap.cloud.sap/docs/java/advanced#staticmodel) in the [Admin Service](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- Use of [Aspects](https://cap.cloud.sap/docs/cds/cdl#aspects) in the Model Definition such as the [`managed` or `cuid` Aspect](https://cap.cloud.sap/docs/cds/common#common-reuse-aspects) in [Books](db/books.cds)
- [Input validation](https://cap.cloud.sap/docs/cds/annotations#input-validation) using model annotation `@assert.format`
- [Data Localization](https://cap.cloud.sap/docs/guides/localized-data) for [Books](db/books.cds)
- Use of [Media Data](https://cap.cloud.sap/docs/guides/providing-services#media-data) in [Books](db/books.cds) and [AdminService](srv/admin-service.cds)

Service Model related Features:

- [Custom event handlers](https://cap.cloud.sap/docs/java/provisioning-api) such as the [Custom business logic for the Admin Service](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- [Custom actions](https://cap.cloud.sap/docs/cds/cdl#actions) such as `addToOrder` in the [Admin Service](srv/admin-service.cds). The Action implementation is in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- Add annotations for [searchable elements](https://github.wdf.sap.corp/pages/cap/java/query-api#select) in the [Admin Service](srv/admin-service.cds)
- [Localized Messages](https://cap.cloud.sap/docs/java/indicating-errors) in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)
- role-based restrictions in [AdminService](srv/admin-service.cds) and [ReviewService](srv/review-service.cds)
- Use of [`@cds.persistence.skip`](https://cap.cloud.sap/docs/advanced/hana#cdspersistenceskip) in [AdminService](srv/admin-service.cds)
- [Media Data](https://cap.cloud.sap/docs/guides/providing-services#media-data) processing in the [Admin Service Event Handler](srv/src/main/java/my/bookshop/handlers/AdminServiceHandler.java)

User Interface related Features:

- Support for [SAP Fiori Elements](https://cap.cloud.sap/docs/advanced/fiori)
- [SAP Fiori Draft based Editing](https://cap.cloud.sap/docs/advanced/fiori#draft-support) for [Books, Orders](srv/admin-service.cds) and [Reviews](srv/review-service.cds)
- [SAP Fiori annotations](https://cap.cloud.sap/docs/advanced/fiori#fiori-annotations) specific for [Browse Books](app/browse/fiori-service.cds), [Manage Books](app/admin/fiori-service.cds), [Manage Orders](app/orders/fiori-service.cds), [Manage Reviews](app/reviews/fiori-service.cds) and [common annotations](app/common.cds), which apply to all UI's
- UI Annotations for custom actions in the [Browse Books](app/browse/fiori-service.cds) and [Manage Books](app/admin/fiori-service.cds) UI, including annotations for a button and a popup
- [Value Help](https://cap.cloud.sap/docs/cds/annotations#odata) for [Books](app/orders/fiori-service.cds) and [Authors](app/common.cds)
- [Model Localization](https://cap.cloud.sap/docs/guides/i18n) for [English](app/_i18n/i18n.properties) and [German](app/_i18n/i18n_de.properties) language for static texts
- [Custom File Upload extension](app/admin/webapp/extension/Upload.js) which provides a button for uploading `CSV` files
- A simple Swagger UI for the CatalogService API at <http://localhost:8080/swagger/index.html>

CDS Maven Plugin Features:

- Install [Node.js](srv/pom.xml#L157) in the default version.
- Install a [configured version](pom.xml#L24) of [@sap/cds-dk](srv/pom.xml#L167).
- Execute arbitrary [CDS](srv/pom.xml#L177) commands.
- [Generate](srv/pom.xml#L195) Java POJOs for type-safe access to the CDS model.
- [Clean](srv/pom.xml#L150) project from artifacts of the previous build.

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
    <http://localhost:8080/fiori.html>: This is the actual bookshop application UI.
    <http://localhost:8080/swagger/index.html>: This is providing a Swagger UI for the CatalogService API.

    You'll start with a predefined stock of books as this procedure starts the bookshop application with a CSV-initialized in-memory H2 database.

    Two mock users are defined for local development:
    - User: `user`, password: `user` to browse books
    - User: `admin`, password: `admin` to manage books and orders

## Using IntelliJ Idea (Community and Ultimate)

IntelliJ can handle the project more or less out-of-the-box. Since some of the event handlers in the project rely on
the code generated from the CDS model the build path of the project (module) needs to be extended
with the folder containing the generated code. In order to add the generated code you need to add the 'gen' folder
to the build path:

* Open the project settings.
* Navigate to the 'modules' section.
* Select the srv/src/gen folder and mark it as 'sources'.
* Save and leave the project settings.
* Trigger a rebuild.

After the generated code is considered by IntelliJ's build the application can be handled just as any other Spring Boot
application in IntelliJ.

## Database Setup and Spring Profiles

The application comes with two predefined profiles that determine how to run the application: `default`, and `cloud` (see `srv/src/main/resources/application.yaml`).


- The `default` profile specifies to use an in-memory H2 database.
  The in-memory database is set up automatically during startup of the application and initialized with some example data from CSV files.

- When deploying the application to Cloud Foundry, the CF Java Buildpack automatically configures the `cloud` Spring profile.
  This profile doesn’t specify any datasource location. In that case CAP Java can automatically detect SAP HANA service bindings available in the environment.

## API_BUSINESS_PARTNER Remote Service and Spring Profiles

The behavior of the API_BUSINESS_PARTNER remote service is controlled using profiles (see `srv/src/main/resources/application.yaml`):

- **Using mock data via internal service:** When using only the `default` profile (default when omitting any profile setting), the API_BUSINESS_PARTNER API is mocked as a local service using the mock data.

- **Using mock data via internal service through OData:** With the `mocked` profile, all requests to the API_BUSINESS_PARTNER service will be routed through HTTP and OData to itself (`http://localhost:<port>/api/API_BUSINESS_PARTNER/...`). This mode is similar to using a real remote destination, and such helps to prevent issues from differences in local service and remote service behavior.

- **Using the sandbox environment:** You can access data from the [SAP API Business Hub sandbox](https://api.sap.com/api/API_BUSINESS_PARTNER/overview) with the `sandbox` profile. The API key needs to be provided with the environment variable `CDS_REMOTE_SERVICES_API_BUSINESS_PARTNER_DESTINATION_HEADERS_APIKEY`. You can retrieve it by clicking on *Show API Key* on [this page](https://api.sap.com/api/API_BUSINESS_PARTNER/overview) after logging in.

- **Using S/4HANA cloud or on-premise system:** With the `destination` profile, you can access data from a real S/4HANA system. You need to create a destination with name `s4-destination` and make sure that an instance of XSUAA and destination service are bound to your application. For an on-premise destination, you additionally need to bind the connectivity service and add an additional property `URL.headers.sap-client` with the S/4HANA client number to your destination.

The profiles `sandbox` and `destination` can be combined with the `default` profile for [hybrid testing](https://cap.cloud.sap/docs/advanced/hybrid-testing) and with the `cloud` profile when deployed to the cloud.

## Deploy to SAP Business Technology Platform, Cloud Foundry

CAP Java applications can be deployed to the SAP Business Technology Platform either in single tenant or in multitenancy mode. See [Multitenancy in CAP Java](https://cap.cloud.sap/docs/java/multitenancy) for more information.

Prerequisites:
- Install the [Cloud MTA Build Tool](https://sap.github.io/cloud-mta-build-tool/): `npm install -g mbt`.
- Install the [Cloud Foundry Command Line Interface](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html).
- Get an SAP Business Technology Platform account to deploy the services and applications.
- [Create a SAP HANA Cloud Instance](https://developers.sap.com/tutorials/hana-cloud-deploying.html) in your SAP Business Technology Platform space.
- Ensure you have an entitlement for `SAP HANA Schemas & HDI Containers` with plan `hdi-shared` in the same space.
- Ensure that your CF instances are connected to Internet to download SAPMachine JRE 17 as it is available in `sap_java_buildpack` in online mode only.

Deploy as Single Tenant Application:
- Rename `mta-single-tenant.yaml` to `mta.yaml`
- Run `mbt build`
- Run `cf login`
- Run `cf deploy mta_archives/bookshop_1.0.0.mtar`

Deploy as Multitenant Application:
- Rename `mta-multi-tenant.yaml` to `mta.yaml`
- Run `mbt build`
- Run `cf login`
- Run `cf deploy mta_archives/bookshop-mt_1.0.0.mtar`
- Go to another subaccount in your global account, under subscriptions and subscribe to the application you deployed.
- Run `cf map-route bookshop-mt-app <YOUR DOMAIN> --hostname <SUBSCRIBER TENANT>-<ORG>-<SPACE>-bookshop-mt-app` or create and bind the route manually.

Before you can access the UI using the (tenant-specific) URL to the bookshop(-mt)-app application, make sure to [Setup Authorizations in SAP Business Technology Platform](#setup-authorizations-in-sap-business-technology-platform).

## Deploy to SAP Business Technology Platform, Kyma Runtime

**TIP:** You can find more information in the [Deploy Your CAP Application on SAP BTP Kyma Runtime](https://developers.sap.com/mission.btp-deploy-cap-kyma.html) tutorial and in the [Deploy to Kyma/K8s](https://cap.cloud.sap/docs/guides/deployment/deploy-to-kyma) guide of the CAP documentation.

### Preconditions

- BTP Subaccount with Kyma Runtime
- BTP Subaccount with Cloud Foundry Space
- [HANA Cloud instance available](https://developers.sap.com/tutorials/hana-cloud-deploying.html) for your Cloud Foundry space
- BTP Entitlements for: *HANA HDI Services & Container* plan *hdi-shared*, *Launchpad Service* plan *standard*
- Container Registry (e.g. [Docker Hub](https://hub.docker.com/))
- Command Line Tools: [`kubectl`](https://kubernetes.io/de/docs/tasks/tools/install-kubectl/), [`kubectl-oidc_login`](https://github.com/int128/kubelogin#setup), [`pack`](https://buildpacks.io/docs/tools/pack/), [`docker`](https://docs.docker.com/get-docker/), [`helm`](https://helm.sh/docs/intro/install/), [`cf`](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)
- Logged into Kyma Runtime (with `kubectl` CLI), Cloud Foundry space (with `cf` CLI) and Container Registry (with `docker login`)
- `@sap/cds-dk` >= 6.6.0

### Add Deployment Files

CAP tooling provides you a Helm chart for deployment to Kyma.

For single tenant deployment, replace the contents of _`.cdsrc.json`_ with _`kyma-st.json`_.
For multi tenant deployment, replace the contents of _`.cdsrc.json`_ with _`kyma-mt.json`_.

Add the CAP Helm chart with the required features to this project:

```bash
cds add helm
```

#### Use API_BUSSINESS_PARTNER Remote Service (optional, single tenant only)

You can try the `API_BUSINESS_PARTNER` service with a real S/4HANA system with the following configuration:

1. Create either an on-premise or cloud destination in your subaccount.

2. Add configuration required for the destination service by executing the following command.

    ```bash
    cds add destination
    ```

3. Set the profiles `cloud` and `destination` active in your `values.yaml` file:

    ```yaml
    srv:
      ...
      env:
        SPRING_PROFILES_ACTIVE: cloud,destination
    ```

4. For on-premise only: Add the connectivity service to your Helm chart:

    ```bash
    cds add connectivity
    ```

   Note: `cds add helm` will not add configuration required to create a Connectivity Service Instance. This Service Instance should be created by the Kyma Cluster Administrator. For more information regarding configuration of Connectivity Instance, please check the [documentation](https://cap.cloud.sap/docs/guides/deployment/deploy-to-kyma#connectivity-service).

*See also: [API_BUSINESS_PARTNER Remote Service and Spring Profiles](#api_business_partner-remote-service-and-spring-profiles)*

### Prepare Kubernetes Namespace

#### Create container registry secret

Create a secret `container-registry` with credentials to access the container registry:

```
bash ./scripts/create-container-registry-secret.sh
```

The *Docker Server* is the full qualified hostname of your container registry.

#### Create a HDI container / Service Manager Instance and a Secret

This step is only required if you're using a BTP Trial account. If you're using a production or a free tier account then you can create HDI Container from Kyma directly by adding a [mapping to your Kyma namespace in your HANA Cloud Instance](https://blogs.sap.com/2022/12/15/consuming-sap-hana-cloud-from-the-kyma-environment/) and skip this step.

##### Single Tenant

```
bash ./scripts/create-db-secret.sh bookshop-db
```

It will create a HDI container `bookshop-db` instance on your currently targeted Cloud Foundry space and a secret `bookshop-db` with the credentials in your current Kubernetes namespace.

Make the following changes to your _`chart/values.yaml`_.

```diff
srv:
  bindings:
    db:
-     serviceInstanceName: hana
+     fromSecret: bookshop-db
...

hana-deployer:
  bindings:
    hana:
-     serviceInstanceName: hana
+     fromSecret: bookshop-db

...
- hana:
-   serviceOfferingName: hana
-   servicePlanName: hdi-shared
```

##### Multi Tenant

```
bash ./scripts/create-sm-secret.sh bookshop-sm
```

It will create a Service Manager `bookshop-sm` instance on your currently targeted Cloud Foundry space and a secret `bookshop-sm` with the credentials in your current Kubernetes namespace.

Make the following changes to your _`chart/values.yaml`_.

```diff
srv:
  bindings:
    service-manager:
-     serviceInstanceName: service-manager
+     fromSecret: bookshop-sm
...

sidecar:
  bindings:
    service-manager:
-     serviceInstanceName: service-manager
+     fromSecret: bookshop-sm

...
- service-manager:
-   serviceOfferingName: service-manager
-   servicePlanName: container
```

### Build

```bash
cds build --production
```

**Build image for CAP service:**

```bash
mvn clean package -DskipTests=true
```

```bash
pack build $YOUR_CONTAINER_REGISTRY/bookshop-srv \
        --path srv/target/*-exec.jar \
        --buildpack gcr.io/paketo-buildpacks/sap-machine \
        --buildpack gcr.io/paketo-buildpacks/java \
        --builder paketobuildpacks/builder:base \
        --env SPRING_PROFILES_ACTIVE=cloud \
        --env BP_JVM_VERSION=17
```

(Replace `$YOUR_CONTAINER_REGISTRY` with the full-qualified hostname of your container registry)

**Build Approuter Image:**

```bash
pack build $YOUR_CONTAINER_REGISTRY/bookshop-approuter \
     --path app \
     --buildpack gcr.io/paketo-buildpacks/nodejs \
     --builder paketobuildpacks/builder:base \
     --env BP_NODE_RUN_SCRIPTS=""
```

**Build data base deployer image (single tenant only):**

```bash
pack build $YOUR_CONTAINER_REGISTRY/bookshop-hana-deployer \
     --path db \
     --buildpack gcr.io/paketo-buildpacks/nodejs \
     --builder paketobuildpacks/builder:base \
     --env BP_NODE_RUN_SCRIPTS=""
```

**Build sidecar image (multi tenant only):**

```bash
pack build $YOUR_CONTAINER_REGISTRY/bookshop-sidecar \
     --path mtx/sidecar/gen \
     --buildpack gcr.io/paketo-buildpacks/nodejs \
     --builder paketobuildpacks/builder:base \
     --env BP_NODE_RUN_SCRIPTS=""
```

### Push container images

You can push all the container images to your container registry, using:

```bash
docker push $YOUR_CONTAINER_REGISTRY/bookshop-srv

docker push $YOUR_CONTAINER_REGISTRY/bookshop-approuter
```

#### Single Tenant

```bash
docker push $YOUR_CONTAINER_REGISTRY/bookshop-hana-deployer 
```

#### Multi Tenant

```bash
docker push $YOUR_CONTAINER_REGISTRY/bookshop-sidecar
```

### Configuration

Make the following changes in the _`chart/values.yaml`_ file.

1. Change value of `global.domain` key to your cluster domain.

2. Replace `<your-cluster-domain>` in `xsuaa.parameters.oauth2-configuration.redirect-uris` with your cluster domain.

3. Replace `<your-container-registry>` with your container registry.

4. Make the following change to add backend destinations required by Approuter.
   
```diff
-  backendDestinations: {}
+  backendDestinations:
+     backend:
+       service: srv
```

5. Add your image registry secret created in [Create container registry secret](#create-container-registry-secret) step.

```diff
global:
  domain: null
-  imagePullSecret: {}
+  imagePullSecret:
+    name: container-registry
```

### Deployment

Deploy the helm chart using the following command:

#### Single Tenant

```bash
helm install bookshop ./chart --set-file xsuaa.jsonParameters=xs-security.json
```

Before you can access the UI you should make sure to [Setup Authorizations in SAP Business Technology Platform](#setup-authorizations-in-sap-business-technology-platform).

Click on the approuter url logged by the `helm install` to access the UI.

#### Multi Tenant

```bash
helm install bookshop ./chart --set-file xsuaa.jsonParameters=xs-security-mt.json
```

In case of multi tenant, you'll have to subscribe to the application from a different subaccount. You can follow the steps mentioned [here](https://cap.cloud.sap/docs/guides/deployment/as-saas#subscribe) to access the application.

## Setup Authorizations in SAP Business Technology Platform

To access services and UIs that require specific authorizations (e.g. `admin`) you need to assign a corresponding role and role collections to your user in SAP BTP Cockpit.

1. For single-tenant applications open the subaccount where you deployed the `bookshop` application to. For multitenant applications open the subaccount where you subscribed to the `bookshop` application.
2. Navigate to *Security* -> *Roles*
3. Create a role with name `bookshop-admin` based on the `admin` role template of the `bookshop` application:
    1. Enter a Business Partner ID of your S/4 system as value for the `businessPartner` attribute. When using the sandbox environment use `10401010`.
4. Navigate to *Security* -> *Role Collections*
5. Create a new role collection `bookshop-admin`:
    1. Assign the `bookshop-admin` role to this role collection
    2. Assign the role collection to your user

# Code Tour

Take the [guided tour](.tours) in VS Code through our CAP Samples for Java and learn which CAP features are showcased by the different parts of the repository. Just install the [CodeTour extension](https://marketplace.visualstudio.com/items?itemName=vsls-contrib.codetour) for VS Code.

# Get Support

In case you have a question, find a bug, or otherwise need support, please use our [community](https://answers.sap.com/tags/9f13aee1-834c-4105-8e43-ee442775e5ce). See the documentation at [https://cap.cloud.sap](https://cap.cloud.sap) for more details about CAP.

# License

Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSES/Apache-2.0.txt) file.
