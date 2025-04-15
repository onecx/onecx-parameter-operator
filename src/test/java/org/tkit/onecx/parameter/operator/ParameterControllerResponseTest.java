package org.tkit.onecx.parameter.operator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.HashMap;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.parameter.operator.client.ParameterService;
import org.tkit.onecx.parameter.test.AbstractTest;

import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ParameterControllerResponseTest extends AbstractTest {

    @InjectMock
    ParameterService parameterService;

    @Inject
    ParameterController reconciler;

    @BeforeEach
    void beforeAll() {
        Mockito.when(parameterService.updateParameter(any())).thenReturn(404);
    }

    @Test
    void testWrongResponse() throws Exception {

        var s = new ParameterSpec();
        s.setKey(KEY);
        s.setProductName("test1");
        s.setApplicationId("test-3");
        s.setParameters(new HashMap<>());
        var n = new ParameterSpec.ParameterItem();
        n.setValue("p1");
        s.getParameters().put("p1", n);

        Parameter m = new Parameter();
        m.setSpec(s);

        UpdateControl<Parameter> result = reconciler.reconcile(m, null);
        assertThat(result).isNotNull();
        assertThat(result.getResource()).isNotNull();
        assertThat(result.getResource().getStatus()).isNotNull();
        assertThat(result.getResource().getStatus().getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.UNDEFINED);

    }
}
