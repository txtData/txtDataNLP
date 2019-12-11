package de.txtdata.asl.server;

import com.codahale.metrics.health.HealthCheck;

public class ServiceHealthCheck extends HealthCheck {

    public ServiceHealthCheck() {
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}