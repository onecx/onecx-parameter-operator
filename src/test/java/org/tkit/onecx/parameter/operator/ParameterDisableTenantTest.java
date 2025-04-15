package org.tkit.onecx.parameter.operator;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.HashMap;

import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.awaitility.Awaitility;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.tkit.onecx.parameter.test.AbstractTest;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.config.SmallRyeConfig;

@QuarkusTest
class ParameterDisableTenantTest extends AbstractTest {

    @Inject
    Operator operator;

    @Inject
    KubernetesClient client;

    @InjectMock
    ParameterConfig dataConfig;

    @Inject
    Config config;

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @BeforeAll
    static void init() {
        Awaitility.setDefaultPollDelay(2, SECONDS);
        Awaitility.setDefaultPollInterval(2, SECONDS);
        Awaitility.setDefaultTimeout(5, SECONDS);
    }

    @BeforeEach
    void beforeEach() {
        var tmp = config.unwrap(SmallRyeConfig.class).getConfigMapping(ParameterConfig.class);

        var tt = Mockito.mock(ParameterConfig.TenantConfig.class);
        Mockito.when(tt.enabled()).thenReturn(false);
        Mockito.when(tt.token()).thenReturn(tmp.tenant().token());

        Mockito.when(dataConfig.tenant()).thenReturn(tt);

        Mockito.when(dataConfig.key()).thenReturn(tmp.key());
        Mockito.when(dataConfig.client()).thenReturn(tmp.client());
    }

    @Test
    void tesTokenClaimArrayParamTest() {

        mockServerClient
                .when(request().withPath("/default/operator/v1/parameters/test199/test-31").withMethod(HttpMethod.PUT))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));
        operator.start();

        var m = new ParameterSpec();
        m.setKey(KEY);
        m.setProductName("test199");
        m.setApplicationId("test-31");
        m.setOrgId("default");
        m.setParameters(new HashMap<>());

        var n1 = new ParameterSpec.ParameterItem();
        n1.setValue("1");
        n1.setDisplayName("display name2");
        n1.setDescription("desc1");
        m.getParameters().put("name1", n1);

        var data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("disable-tenant").withNamespace(client.getNamespace()).build());
        data.setSpec(m);

        client.resource(data).serverSideApply();

        await().pollDelay(2, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.UPDATED);
        });

        mockServerClient.clear("mock");
    }
}
