package org.tkit.onecx.parameter.operator.client.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.tkit.onecx.parameter.operator.ParameterSpec;

import gen.org.tkit.onecx.parameter.operator.v1.model.ParameterUpdateRequest;
import gen.org.tkit.onecx.parameter.operator.v1.model.ParametersUpdateRequest;

@Mapper
public abstract class ParameterClientMapper {

    @Inject
    ParametersValueMapper valueMapper;

    public abstract ParametersUpdateRequest map(ParameterSpec spec);

    public List<ParameterUpdateRequest> map(Map<String, ParameterSpec.ParameterItem> value) {
        if (value == null) {
            return List.of();
        }
        List<ParameterUpdateRequest> parameters = new ArrayList<>();
        value.forEach(
                (k, v) -> parameters.add(create(k, v.getDisplayName(), v.getDisplayName(), valueMapper.toMap(v.getValue()))));
        return parameters;
    }

    @BeanMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    public abstract ParameterUpdateRequest create(String name, String displayName, String description, Object value);

}
