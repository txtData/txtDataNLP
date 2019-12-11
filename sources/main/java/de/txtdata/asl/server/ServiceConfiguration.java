package de.txtdata.asl.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class ServiceConfiguration extends Configuration {
    @NotEmpty
    private String serviceName;
    private String offlineMode;

    @JsonProperty
    public String getServiceName() {
        return this.serviceName;
    }

    @JsonProperty
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonProperty
    public String getOfflineMode() {
        return this.offlineMode;
    }

    @JsonProperty
    public void setOfflineMode(String serviceName) {
        this.offlineMode = serviceName;
    }
}
