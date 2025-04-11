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
class ParameterClaimArrayTest extends AbstractTest {

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

        var dt = Mockito.mock(ParameterConfig.TokenConfig.class);
        Mockito.when(dt.userName()).thenReturn(tmp.token().userName());
        Mockito.when(dt.headerParam()).thenReturn(tmp.token().headerParam());
        Mockito.when(dt.claimOrganizationParamArray()).thenReturn(true);
        Mockito.when(dt.claimOrganizationParam()).thenReturn(tmp.token().claimOrganizationParam());

        Mockito.when(dataConfig.token()).thenReturn(dt);

        Mockito.when(dataConfig.key()).thenReturn(tmp.key());
        Mockito.when(dataConfig.client()).thenReturn(tmp.client());
    }

    @Test
    void tesTokenClaimArrayParamTest() {

        mockServerClient
                .when(request().withPath("/default/operator/v1/parameters/test1/test-3").withMethod(HttpMethod.PUT))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));
        operator.start();

        var m = new ParameterSpec();
        m.setKey(KEY);
        m.setProductName("test1");
        m.setApplicationId("test-3");
        m.setOrgId("default");
        m.setParameters(new HashMap<>());

        var n1 = new ParameterSpec.ParameterItem();
        n1.setValue("");
        n1.setDisplayName("display name");
        n1.setDescription("desc");
        m.getParameters().put("name", n1);

        var data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("config-array").withNamespace(client.getNamespace()).build());
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
