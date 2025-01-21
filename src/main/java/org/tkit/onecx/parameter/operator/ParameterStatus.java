package org.tkit.onecx.parameter.operator;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;

public class ParameterStatus extends ObservedGenerationAwareStatus {

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("response-code")
    private int responseCode;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("message")
    private String message;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public enum Status {

        ERROR,

        UPDATED,

        UNDEFINED;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
