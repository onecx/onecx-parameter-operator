package org.tkit.onecx.parameter.operator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParameterSpec {

    @JsonProperty("orgId")
    private String orgId;

    @JsonProperty("key")
    private String key;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "displayName", required = true)
    private String displayName;

    @JsonProperty(value = "applicationId", required = true)
    private String applicationId;

    @JsonProperty(value = "productName", required = true)
    private String productName;

    @JsonProperty(value = "description", required = true)
    private String description;

    @JsonProperty(value = "importValue", required = true)
    private String importValue;

    @JsonProperty(value = "value")
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImportValue() {
        return importValue;
    }

    public void setImportValue(String importValue) {
        this.importValue = importValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
}
