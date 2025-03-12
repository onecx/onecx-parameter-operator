package org.tkit.onecx.parameter.operator;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigDocFilename("onecx-parameter-operator.adoc")
@ConfigMapping(prefix = "onecx.parameter.operator")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface ParameterConfig {

    /**
     * Token configuration.
     */
    @WithDefault("token")
    TokenConfig token();

    /**
     * Client configuration.
     */
    @WithDefault("client")
    ConfigClient client();

    /**
     * Leader election configuration
     */
    @WithName("leader-election")
    LeaderElectionConfig leaderElectionConfig();

    /**
     * Clients key configuration
     */
    @WithName("clients")
    Map<String, String> clients();

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
