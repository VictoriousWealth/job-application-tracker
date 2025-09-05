package com.nick.job_application_tracker.service.specialised_common;

import com.nick.job_application_tracker.dto.create.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.response.JobApplicationResponseDTO;
import com.nick.job_application_tracker.dto.update.JobApplicationUpdateDTO;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.service.common.ServiceInterface;


/**
 * Service interface for managing {@link JobApplication} entities.
 *
 * <p>Extends the generic {@link ServiceInterface} to inherit core CRUD operations,
 * while allowing for specific documentation or customization of behavior related to job applications.
 */
public interface JobApplicationServiceInterface extends ServiceInterface<JobApplicationCreateDTO,  JobApplicationResponseDTO, JobApplicationUpdateDTO, JobApplicationDetailDTO, JobApplication> {
    
}
