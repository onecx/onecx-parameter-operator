package org.tkit.onecx.parameter.operator.client.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.parameter.operator.ParameterSpec;

import gen.org.tkit.onecx.parameter.operator.v1.model.ParameterUpdateRequest;
import gen.org.tkit.onecx.parameter.operator.v1.model.ParametersUpdateRequest;

@Mapper
public interface ParameterClientMapper {

    default ParametersUpdateRequest map(ParameterSpec spec) {
        ParametersUpdateRequest r = create(spec.getProductName(), spec.getApplicationId());
        return r;
    }

    @Mapping(target = "parameters", ignore = true)
    ParametersUpdateRequest create(String productName, String applicationId);

    ParameterUpdateRequest map(String name, ParameterSpec.ParameterItem parameter);
}
