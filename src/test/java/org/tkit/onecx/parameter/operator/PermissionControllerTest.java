package org.tkit.onecx.parameter.operator;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

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
class PermissionControllerTest extends AbstractTest {

    static final Logger log = LoggerFactory.getLogger(PermissionControllerTest.class);

    @Inject
    Operator operator;

    @Inject
    KubernetesClient client;

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @BeforeAll
    public static void init() {
        Awaitility.setDefaultPollDelay(2, SECONDS);
        Awaitility.setDefaultPollInterval(2, SECONDS);
        Awaitility.setDefaultTimeout(10, SECONDS);
    }

    @Test
    void productEmptySpecTest() {
        operator.start();

        Parameter data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("empty-spec").withNamespace(client.getNamespace()).build());
        data.setSpec(new ParameterSpec());

        log.info("Creating test permission object: {}", data);
        client.resource(data).serverSideApply();

        log.info("Waiting 4 seconds and status muss be still null");

        await().pollDelay(2, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.ERROR);
        });

        // mockServerClient.clear("mock");
    }

    @Test
    void productNullSpecTest() {
        operator.start();

        Parameter data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("null-spec").withNamespace(client.getNamespace()).build());
        data.setSpec(null);

        log.info("Creating test parameter object: {}", data);
        client.resource(data).serverSideApply();

        log.info("Waiting 4 seconds and status muss be still null");

        await().pollDelay(4, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNull();
        });
    }

    @Test
    void productUpdateEmptySpecTest() {
        // create mock rest endpoint for workspace api
        mockServerClient
                .when(request().withPath("/parametermgmt/operator/v1/parameters/test1/test-3/name").withMethod(HttpMethod.PUT))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));
        operator.start();

        var m = new ParameterSpec();
        m.setProductName("test1");
        m.setApplicationId("test-3");
        m.setImportValue("p1");
        m.setKey("parametermgmt");
        m.setOrgId("default");
        m.setName("name");
        m.setDisplayName("displayname");
        m.setDescription("desc");
        m.setValue(new Object());

        var data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("to-update-spec").withNamespace(client.getNamespace()).build());
        data.setSpec(m);

        log.info("Creating test parameter object: {}", data);
        client.resource(data).serverSideApply();

        log.info("Waiting 4 seconds and status muss be still null");

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

    //    @Test
    //    void productUpdateNoDescriptionTest() {
    //        //        ParameterUpdateRequest request = new ParameterUpdateRequest();
    //        //        request.setName("test-3");
    //        //        request.setProductName("test1");
    //        //        request.setDisplayName("");
    //        //        request.setApplicationId("app1");
    //        // create mock rest endpoint for workspace api
    //        mockServerClient
    //                .when(request().withPath("/operator/v1/parameters").withMethod(HttpMethod.PUT))
    //                .withId("mock")
    //                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
    //                        .withContentType(MediaType.APPLICATION_JSON));
    //        operator.start();
    //
    //        var m = new ParameterSpec();
    //        m.setProductName("test1");
    //        m.setName("test-3");
    //        m.setApplicationId("app1");
    //        var data = new Parameter();
    //        data.setMetadata(new ObjectMetaBuilder().withName("to-update-spec").withNamespace(client.getNamespace()).build());
    //        data.setSpec(m);
    //
    //        log.info("Updating test parameter object: {}", data);
    //        var exception = catchThrowableOfType(KubernetesClientException.class, () -> client.resource(data).serverSideApply());
    //        assertThat(exception).isNotNull();
    //        mockServerClient.clear("mock");
    //    }

    @Test
    void productRestClientExceptionTest() {
        // create mock rest endpoint for workspace api
        mockServerClient
                .when(request().withPath("/parametermgmt/operator/v1/parameters/test1/test-error-1/n1")
                        .withMethod(HttpMethod.PUT))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(500));
        operator.start();
        var m = new ParameterSpec();
        m.setProductName("test1");
        m.setApplicationId("test-error-1");
        m.setName("n1");
        m.setImportValue("p1");
        m.setKey("parametermgmt");

        var data = new Parameter();
        data.setMetadata(new ObjectMetaBuilder().withName("client-error").withNamespace(client.getNamespace()).build());
        data.setSpec(m);

        log.info("Creating test parameter object: {}", data);
        client.resource(data).serverSideApply();

        log.info("Waiting 4 seconds and status muss be still null");

        await().pollDelay(2, SECONDS).untilAsserted(() -> {
            ParameterStatus mfeStatus = client.resource(data).get().getStatus();
            assertThat(mfeStatus).isNotNull();
            assertThat(mfeStatus.getStatus()).isNotNull().isEqualTo(ParameterStatus.Status.ERROR);
        });
        mockServerClient.clear("mock");

    }

}
