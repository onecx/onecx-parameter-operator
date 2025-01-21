package org.tkit.onecx.parameter.operator;

import jakarta.inject.Singleton;

import io.javaoperatorsdk.operator.api.config.LeaderElectionConfiguration;

@Singleton
public class LeaderConfiguration extends LeaderElectionConfiguration {

    public LeaderConfiguration(ParameterConfig config) {
        super(config.leaderElectionConfig().leaseName());
    }
}
