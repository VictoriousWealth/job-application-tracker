package com.nick.job_application_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.ScheduledCommunication.Type;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.properties") 
public class ScheduledCommunicationRepositoryTest {

    @Autowired
    private ScheduledCommunicationRepository communicationRepo;

    @Autowired
    private JobApplicationRepository jobAppRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should save and retrieve scheduled communications by job application ID")
    public void testFindByJobApplicationId() {
        User user = new User();
        user.setEmail("test@nick.dev");
        user.setPassword("pass");
        user.setEnabled(true);
        user = userRepo.save(user);

        JobApplication app = new JobApplication();
        app.setJobTitle("Dev");
        app.setCompany("Nick Inc.");
        app.setStatus(Status.INTERVIEW);
        app.setUser(user);
        app = jobAppRepo.save(app);

        ScheduledCommunication comm = new ScheduledCommunication();
        comm.setJobApplication(app);
        comm.setScheduledFor(LocalDateTime.now().plusDays(3));
        comm.setType(Type.INTERVIEW);
        comm.setNotes("Initial interview scheduled");
        communicationRepo.save(comm);

        List<ScheduledCommunication> results = communicationRepo.findByJobApplicationId(app.getId());

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getType()).isEqualTo(Type.INTERVIEW);
    }

    @Test
    @DisplayName("Should return empty list when job application ID not found")
    public void testFindByJobApplicationIdNotFound() {
        List<ScheduledCommunication> results = communicationRepo.findByJobApplicationId(-1L);
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should delete scheduled communication and not find it again")
    public void testDeleteById() {
        // Setup - create user
        User user = new User();
        user.setEmail("scheduled_test@example.com");
        user.setPassword("pass123");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        // Setup - create job application
        JobApplication job = new JobApplication();
        job.setJobTitle("Analyst");
        job.setCompany("BigCorp");
        job.setStatus(JobApplication.Status.APPLIED);
        job.setUser(user);
        job = jobAppRepo.save(job);

        // Setup - create scheduled communication
        ScheduledCommunication comm = new ScheduledCommunication();
        comm.setScheduledFor(LocalDateTime.now().plusDays(1));
        comm.setType(Type.CALL);
        comm.setNotes("Phone call");
        comm.setJobApplication(job); 

        comm = communicationRepo.save(comm);

        // Delete it
        communicationRepo.deleteById(comm.getId());

        // Verify it’s gone
        Optional<ScheduledCommunication> deleted = communicationRepo.findById(comm.getId());
        assertThat(deleted).isEmpty();
    }

}
