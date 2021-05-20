package my.bookshop.handlers;

import com.sap.cds.ql.Upsert;
import com.sap.cds.services.application.ApplicationLifecycleService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.my.bookshop.Bookshop_;
import cds.gen.my.bookshop.Csv;

@Component
@ServiceName(ApplicationLifecycleService.DEFAULT_NAME)
public class SingletonInitialization implements EventHandler {

	@Autowired
	private PersistenceService db;
	
	@After(event = ApplicationLifecycleService.EVENT_APPLICATION_PREPARED)
	public void initializeCsvEntity() {
		Csv csv = Csv.create();
		csv.setDataType("text/csv");
		db.run(Upsert.into(Bookshop_.CSV).entry(csv));
	}
}
