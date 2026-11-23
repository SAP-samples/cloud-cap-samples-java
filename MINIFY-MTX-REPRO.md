# Java-only query dependency removed from the MTX model (BLI 2971)

`srv/minify-probe.cds` adds `CatalogService.probeMinifiedCountries`. Its CDS
signature returns only an integer; the dependency on `sap.common.Countries`
exists exclusively in `MinifyProbeHandler.java`:

```java
db.run(Select.from(Countries_.class).columns(Countries_::code));
```

The local, non-minified CSN includes Countries, and the Maven plugin generates
`Countries_`. The app therefore compiles. Countries is an unused reuse entity
with `@cds.persistence.skip: 'if-unused'`, so the sidecar removes it from the
tenant model. This happens even with `CDS_BUILD_MINIFY=false`: the sidecar
minifies inferred models independently of the Java build option.

## Automated reproduction

From the repository root:

```sh
CDS_BUILD_MINIFY=false mvn -B verify \
  -Dtest=RatingCalculatorTest -Dsurefire.failIfNoSpecifiedTests=false \
  -Dit.test=MinifyRuntimeModelIT -Dfailsafe.failIfNoSpecifiedTests=false
```

The existing integration-test lifecycle starts the local sidecar on port 4005.
The test subscribes its own `minify-runtime-probe` tenant and unsubscribes it
afterward. Java HTTP requests use MockMvc, not port 8080.

All three reproduction tests pass:

1. The generated query resolves against the local model.
2. The same query analyzed against the tenant model throws the cds4j
   `CdsDefinitionNotFoundException`, without executing SQL.
3. An authenticated tenant request to the action fails with HTTP **404**:

   ```json
   {"error":{"code":"404002","message":"Entity 'sap.common.Countries' not found"}}
   ```

The ordinary persistence call is rejected during runtime-model resolution,
before SQL execution. The handler logs that the entity is absent from the tenant
model. A standalone non-MTX HTTP control returned **200** with `value: 1` when
an explicit one-row Countries SQL fixture was supplied. That fixture is needed
because ordinary CDS deployment also omits the unused code-list table:

```sql
CREATE TABLE SAP_COMMON_COUNTRIES (CODE NVARCHAR(3) PRIMARY KEY);
INSERT INTO SAP_COMMON_COUNTRIES (CODE) VALUES ('DE');
```

Validated with CAP Java 5.2.0, cds-dk 10.2.1, cds 10.2.2, cds-mtxs 4.2.2,
and Spring Boot 4.1.2. This demonstrates the runtime compatibility risk rather
than a generated-interface compilation failure. Enabling build minification
would instead remove Countries from the local generation input as well.
