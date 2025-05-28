package org.tkit.onecx.parameter.operator;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParameterStatus {

    @JsonProperty("observedGeneration")
    private Long observedGeneration;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("productName")
    private String productName;

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

    public Long getObservedGeneration() {
        return observedGeneration;
    }

    public void setObservedGeneration(Long observedGeneration) {
        this.observedGeneration = observedGeneration;
    }
}
