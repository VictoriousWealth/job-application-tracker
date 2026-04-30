package com.nick.job_application_tracker.service.specialised_common;

import com.nick.job_application_tracker.dto.create.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.dto.detail.ApplicationTimelineDetailDTO;
import com.nick.job_application_tracker.dto.response.ApplicationTimelineResponseDTO;
import com.nick.job_application_tracker.dto.update.ApplicationTimelineUpdateDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.service.common.ServiceInterface;

public interface ApplicationTimelineServiceInterface extends ServiceInterface<ApplicationTimelineCreateDTO, ApplicationTimelineResponseDTO, ApplicationTimelineUpdateDTO, ApplicationTimelineDetailDTO, ApplicationTimeline> {
    
}
