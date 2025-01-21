package org.tkit.onecx.parameter.operator.client.mappers;

import org.mapstruct.Mapper;
import org.tkit.onecx.parameter.operator.ParameterSpec;

import gen.org.tkit.onecx.parameter.operator.v1.model.ParameterUpdateRequest;

@Mapper
public interface ParameterClientMapper {
    ParameterUpdateRequest map(ParameterSpec spec);
}
