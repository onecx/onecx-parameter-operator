package org.tkit.onecx.parameter.operator;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1")
@Group("onecx.tkit.org")
public class Parameter extends
        CustomResource<ParameterSpec, ParameterStatus>
        implements Namespaced {
}
