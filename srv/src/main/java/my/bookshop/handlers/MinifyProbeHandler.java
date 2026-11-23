package my.bookshop.handlers;

import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.ProbeMinifiedCountriesContext;
import cds.gen.sap.common.Countries_;
import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Reproduces a Java-only model dependency missing from the MTX runtime model. */
@Component
@ServiceName(CatalogService_.CDS_NAME)
class MinifyProbeHandler implements EventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(MinifyProbeHandler.class);

  private final PersistenceService db;

  MinifyProbeHandler(PersistenceService db) {
    this.db = db;
  }

  @On
  public Integer probeMinifiedCountries(ProbeMinifiedCountriesContext context) {
    LOG.info(
        "Minify probe: tenant={}, runtime entity {} present={}",
        context.getUserInfo().getTenant(),
        Countries_.CDS_NAME,
        context.getModel().findEntity(Countries_.CDS_NAME).isPresent());

    // Countries_ is generated locally and compiles successfully. Resolving this
    // query against the tenant model fails if Countries was removed by minify.
    return Math.toIntExact(
        db.run(Select.from(Countries_.class).columns(Countries_::code)).rowCount());
  }
}
