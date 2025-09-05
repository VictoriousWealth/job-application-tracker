package com.nick.job_application_tracker.service.specialised_common;

import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.service.common.ServiceInterface;

public interface LocationServiceInterface extends ServiceInterface<LocationCreateDTO, LocationResponseDTO, LocationUpdateDTO, LocationDetailDTO, Location>{
    
    
}
