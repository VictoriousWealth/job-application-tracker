package com.nick.job_application_tracker.mapper;

import java.util.Set;


public interface mapper<M, D> {

    static final Set<String> patchableFields = Set.of();

    M updateEntityWithDTOInfo(M model, D dto, M...models);

    
    
}
