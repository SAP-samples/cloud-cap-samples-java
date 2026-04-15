package my.bookshop.handlers;

import com.sap.cds.services.CoreFactory;
import com.sap.cds.services.ServiceExceptionUtils;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.MessageTarget;
import com.sap.cds.services.mt.DependenciesEventContext;
import com.sap.cds.services.mt.DeploymentService;
import com.sap.cds.services.mt.SaasRegistryDependency;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@ServiceName(DeploymentService.DEFAULT_NAME)
public class CustomSaaSDependenciesHandler implements EventHandler {

  @On
  public void assign(DependenciesEventContext ctx) {
    SaasRegistryDependency dep = SaasRegistryDependency.create();
    dep.setXsappname("xsapp-test-name");

    ServiceExceptionUtils utils = CoreFactory.INSTANCE.createServiceExceptionUtils();
    utils.getMessageTarget("orderAmount");

    ctx.setResult(List.of(dep));
  }
}
