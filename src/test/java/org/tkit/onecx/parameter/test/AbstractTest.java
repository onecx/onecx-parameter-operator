package org.tkit.onecx.parameter.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.tkit.onecx.parameter.operator.ParameterConfig;

import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.Mock;
import io.quarkus.test.common.QuarkusTestResource;
import io.smallrye.config.SmallRyeConfig;

@SuppressWarnings("java:S1118")
@QuarkusTestResource(MockServerTestResource.class)
public abstract class AbstractTest {

    public static class ConfigProducer {

        @Inject
        Config config;

        @Produces
        @ApplicationScoped
        @Mock
        ParameterConfig createConfig() {
            return config.unwrap(SmallRyeConfig.class).getConfigMapping(ParameterConfig.class);
        }
    }
}
