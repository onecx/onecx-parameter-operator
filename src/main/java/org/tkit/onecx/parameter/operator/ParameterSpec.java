package org.tkit.onecx.parameter.operator;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParameterSpec {

    @JsonProperty(value = "orgId", required = true)
    private String orgId;

    @JsonProperty(value = "key", required = true)
    private String key;

    @JsonProperty(value = "applicationId", required = true)
    private String applicationId;

    @JsonProperty(value = "productName", required = true)
    private String productName;

    @JsonProperty(value = "parameters", required = true)
    private Map<String, ParameterItem> parameters;

    public Map<String, ParameterItem> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ParameterItem> parameters) {
        this.parameters = parameters;
    }

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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static class ParameterItem {

        @JsonProperty(value = "displayName", required = true)
        private String displayName;

        @JsonProperty(value = "description", required = true)
        private String description;

        @JsonProperty(value = "value", required = true)
        private String value;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
