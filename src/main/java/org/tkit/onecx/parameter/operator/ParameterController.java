package org.tkit.onecx.parameter.operator;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.parameter.operator.client.ParameterService;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.processing.event.source.filter.OnAddFilter;
import io.javaoperatorsdk.operator.processing.event.source.filter.OnUpdateFilter;

@ControllerConfiguration(name = "parameter", namespaces = Constants.WATCH_CURRENT_NAMESPACE, onAddFilter = ParameterController.AddFilter.class, onUpdateFilter = ParameterController.UpdateFilter.class)
public class ParameterController implements Reconciler<Parameter>, ErrorStatusHandler<Parameter> {

    private static final Logger log = LoggerFactory.getLogger(ParameterController.class);

    @Inject
    ParameterService service;

    @Override
    public ErrorStatusUpdateControl<Parameter> updateErrorStatus(Parameter parameter, Context<Parameter> context,
            Exception e) {
        int responseCode = -1;
        if (e.getCause() instanceof WebApplicationException re) {
            responseCode = re.getResponse().getStatus();
        }

        log.error("Error reconcile resource", e);
        var status = new ParameterStatus();
        status.setApplicationId(null);
        status.setProductName(null);
        status.setName(null);
        status.setResponseCode(responseCode);
        status.setStatus(ParameterStatus.Status.ERROR);
        status.setMessage(e.getMessage());
        parameter.setStatus(status);
        return ErrorStatusUpdateControl.updateStatus(parameter);
    }

    @Override
    public UpdateControl<Parameter> reconcile(Parameter parameter, Context<Parameter> context) throws Exception {
        log.info("Reconcile resource: {} name: {}", parameter.getMetadata().getName(), parameter.getSpec().getName());

        int responseCode = service.updateParameter(parameter);

        updateStatusPojo(parameter, responseCode);
        log.info("Product '{}' reconciled - updating status", parameter.getMetadata().getName());
        return UpdateControl.updateStatus(parameter);
    }

    private void updateStatusPojo(Parameter parameter, int responseCode) {
        ParameterStatus result = new ParameterStatus();
        ParameterSpec spec = parameter.getSpec();
        result.setApplicationId(spec.getApplicationId());
        result.setProductName(spec.getProductName());
        result.setName(spec.getName());

        result.setResponseCode(responseCode);
        var status = responseCode == 204 ? ParameterStatus.Status.UPDATED
                : ParameterStatus.Status.UNDEFINED;
        result.setStatus(status);
        parameter.setStatus(result);
    }

    public static class AddFilter implements OnAddFilter<Parameter> {

        @Override
        public boolean accept(Parameter resource) {
            return resource.getSpec() != null;
        }
    }

    public static class UpdateFilter implements OnUpdateFilter<Parameter> {

        @Override
        public boolean accept(Parameter newResource, Parameter oldResource) {
            return newResource.getSpec() != null;
        }
    }
}
