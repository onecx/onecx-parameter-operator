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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.parameter.test.AbstractTest;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ParameterControllerTest extends AbstractTest {

    static final Logger log = LoggerFactory.getLogger(ParameterControllerTest.class);

    @Inject
    Operator operator;

    @Inject
    KubernetesClient client;

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @BeforeAll
    static void init() {
        Awaitility.setDefaultPollDelay(2, SECONDS);
        Awaitility.setDefaultPollInterval(2, SECONDS);
        Awaitility.setDefaultTimeout(10, SECONDS);
    }

    @Test
    void productEmptySpecTest() {
        operator.start();

        var spec = new ParameterSpec();
        spec.setKey(KEY);

        Parameter data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("empty-spec").withNamespace(client.getNamespace()).build());
        data.setSpec(spec);

        log.info("Creating test permission object: {}", data);
        client.resource(data).serverSideApply();

        log.info("Waiting 4 seconds and status muss be still null");

        await().pollDelay(2, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.ERROR);
        });

    }

    @Test
    void productNullSpecTest() {
        operator.start();

        Parameter data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("null-spec").withNamespace(client.getNamespace()).build());
        data.setSpec(null);

        client.resource(data).serverSideApply();

        await().pollDelay(4, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNull();
        });
    }

    @Test
    void productUpdateEmptySpecTest() {
        // create mock rest endpoint for workspace api
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
        data.setMetadata(new ObjectMetaBuilder().withName("to-update-spec").withNamespace(client.getNamespace()).build());
        data.setSpec(m);

        log.info("Creating test parameter object: {}", data);
        client.resource(data).serverSideApply();

        await().pollDelay(2, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.UPDATED);
        });

        client.resource(data).inNamespace(client.getNamespace())
                .edit(s -> {
                    s.setSpec(null);
                    return s;
                });

        await().pollDelay(4, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.UPDATED);
        });
        mockServerClient.clear("mock");
    }

    @Test
    void productRestClientExceptionTest() {
        // create mock rest endpoint for workspace api
        mockServerClient
                .when(request().withPath("/custom/operator/v1/parameters/test1/test-error-1")
                        .withBody("{\"a\":123}")
                        .withMethod(HttpMethod.PUT))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(500));
        operator.start();
        var m = new ParameterSpec();
        m.setKey(KEY);
        m.setUrl(MOCK_URL + "/custom");
        m.setProductName("test1");
        m.setApplicationId("test-error-1");
        m.setParameters(new HashMap<>());

        var n1 = new ParameterSpec.ParameterItem();
        n1.setValue("{\"a\":123}");
        n1.setDisplayName("display name");
        n1.setDescription("desc");
        m.getParameters().put("n1", n1);

        var data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("client-error").withNamespace(client.getNamespace()).build());
        data.setSpec(m);
        client.resource(data).serverSideApply();

        await().pollDelay(2, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.ERROR);
        });
        mockServerClient.clear("mock");
    }

}
