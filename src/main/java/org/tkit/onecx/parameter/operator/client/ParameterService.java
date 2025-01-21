package org.tkit.onecx.parameter.operator.client;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.client.api.QuarkusRestClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.parameter.operator.Parameter;
import org.tkit.onecx.parameter.operator.ParameterConfig;
import org.tkit.onecx.parameter.operator.client.mappers.ParameterClientMapper;

import gen.org.tkit.onecx.parameter.operator.v1.api.OperatorParametersApi;
import io.quarkus.oidc.client.reactive.filter.OidcClientRequestReactiveFilter;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.rest.client.reactive.ReactiveClientHeadersFactory;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ParameterService {

    private static final Logger log = LoggerFactory.getLogger(ParameterService.class);

    @Inject
    ParameterClientMapper mapper;

    @Inject
    ParameterConfig parameterConfig;

    public int updateParameter(Parameter parameter) {
        var spec = parameter.getSpec();
        var url = parameterConfig.client().keys().get(spec.getKey());
        if (url == null) {
            log.warn("No URL defined for the key '{}', resource: {}", spec.getKey(), parameter.getMetadata().getName());
            throw new MissingKeyConfiguration(spec.getKey());
        }
        var dto = mapper.map(spec);

        var token = createToken(parameter);
        var client = QuarkusRestClientBuilder.newBuilder()
                .baseUri(URI.create(url))
                .property(QuarkusRestClientProperties.CONNECTION_POOL_SIZE, parameterConfig.client().connectionPoolSize())
                .property(QuarkusRestClientProperties.NAME, spec.getKey())
                .property(QuarkusRestClientProperties.SHARED, parameterConfig.client().shared())
                .register(OidcClientRequestReactiveFilter.class)
                .clientHeadersFactory(new ReactiveClientHeadersFactory() {
                    @Override
                    public Uni<MultivaluedMap<String, String>> getHeaders(MultivaluedMap<String, String> incomingHeaders,
                            MultivaluedMap<String, String> clientOutgoingHeaders) {
                        MultivaluedMap<String, String> propagatedHeaders = new MultivaluedHashMap<>();
                        propagatedHeaders.putSingle(parameterConfig.token().headerParam(), token);
                        return Uni.createFrom().item(propagatedHeaders);
                    }
                })
                .build(OperatorParametersApi.class);

        try (var response = client.createOrUpdateParameterValue(spec.getProductName(), spec.getApplicationId(), spec.getName(),
                dto)) {
            log.info("Update parameter name {} status {}", spec.getName(), response.getStatus());
            return response.getStatus();
        }
    }

    private String createToken(Parameter parameter) {

        var userName = parameterConfig.token().userName();
        var orgId = parameter.getSpec().getOrgId();

        JsonObjectBuilder claims = Json.createObjectBuilder();
        claims.add(Claims.preferred_username.name(), userName);
        claims.add(Claims.sub.name(), userName);
        if (orgId != null) {
            if (parameterConfig.token().claimOrganizationParamArray()) {
                claims.add(parameterConfig.token().claimOrganizationParam(), Json.createArrayBuilder().add(orgId));
            } else {
                claims.add(parameterConfig.token().claimOrganizationParam(), orgId);
            }
        }
        return Jwt.claims(claims.build()).sign(KeyFactory.PRIVATE_KEY);
    }

    public static class MissingKeyConfiguration extends RuntimeException {

        public MissingKeyConfiguration(String key) {
            super("Missing configuration for key " + key);
        }
    }
}
