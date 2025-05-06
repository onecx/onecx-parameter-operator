package org.tkit.onecx.parameter.operator;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("onecx-parameter-operator.adoc")
@ConfigMapping(prefix = "onecx.parameters.operator")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface ParameterConfig {

    /**
     * Operator key configuration.
     */
    @WithName("key")
    @WithDefault("onecx")
    String key();

    /**
     * Multi-tenancy configuration.
     */
    @WithName("tenant")
    TenantConfig tenant();

    /**
     * Client configuration.
     */
    @WithName("client")
    ConfigClient client();

    /**
     * Leader election configuration
     */
    @WithName("leader-election")
    LeaderElectionConfig leaderElectionConfig();

    /**
     * Client configuration.
     */
    interface ConfigClient {

        /**
         * Set to true to share the HTTP client between REST clients.
         */
        @WithName("shared")
        @WithDefault("true")
        boolean shared();

        /**
         * The size of the rest client connection pool.
         */
        @WithName("connection-pool-size")
        @WithDefault("30")
        int connectionPoolSize();

        /**
         * Client URL configuration.
         */
        @WithName("url")
        @WithDefault("http://onecx-parameter-svc:8080")
        String url();
    }

    /**
     * Multi-tenancy configuration
     */
    interface TenantConfig {

        /**
         * Enabled or disable multi-tenancy configuration.
         */
        @WithName("enabled")
        @WithDefault("true")
        boolean enabled();

        /**
         * Token configuration.
         */
        @WithName("token")
        TokenConfig token();

    }

    /**
     * Token configuration.
     */
    interface TokenConfig {

        /**
         * Username for rest call.
         */
        @WithName("user-name")
        @WithDefault("parameter-orchestrator-operator")
        String userName();

        /**
         * Token header parameter.
         */
        @WithName("header-param")
        @WithDefault("apm-principal-token")
        String headerParam();

        /**
         * Token claim organization parameter.
         */
        @WithName("claim-organization-param")
        @WithDefault("orgId")
        String claimOrganizationParam();

        /**
         * Token claim organization parameter array.
         */
        @WithName("claim-organization-param-array")
        @WithDefault("false")
        boolean claimOrganizationParamArray();

    }

    /**
     * Leader election config
     */
    interface LeaderElectionConfig {

        /**
         * Lease name
         */
        @WithName("lease-name")
        @WithDefault("onecx-parameter-operator-lease")
        String leaseName();
    }
}
