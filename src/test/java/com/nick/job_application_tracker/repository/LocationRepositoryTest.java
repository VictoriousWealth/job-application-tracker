package com.nick.job_application_tracker.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.Location;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.properties") 
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Should save and find Location by ID")
    public void testSaveAndFindById() {
        Location location = new Location();
        location.setCity("London");
        location.setCountry("UK");
        location = locationRepository.save(location);

        Optional<Location> found = locationRepository.findById(location.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCity()).isEqualTo("London");
        assertThat(found.get().getCountry()).isEqualTo("UK");
    }

    @Test
    @DisplayName("Should find Location by city and country")
    public void testFindByCityAndCountry() {
        Location location = new Location();
        location.setCity("Paris");
        location.setCountry("France");
        locationRepository.save(location);

        Optional<Location> found = locationRepository.findByCityAndCountry("Paris", "France");

        assertThat(found).isPresent();
        assertThat(found.get().getCity()).isEqualTo("Paris");
    }

    @Test
    @DisplayName("Should return empty if no Location matches city and country")
    public void testFindByCityAndCountryNotFound() {
        Optional<Location> found = locationRepository.findByCityAndCountry("Mars", "Galaxy");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should delete Location successfully")
    public void testDeleteLocation() {
        Location location = new Location();
        location.setCity("Berlin");
        location.setCountry("Germany");
        location = locationRepository.save(location);

        locationRepository.deleteById(location.getId());

        Optional<Location> found = locationRepository.findById(location.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should list all saved Locations")
    public void testFindAll() {
        Location one = new Location();
        one.setCity("Madrid");
        one.setCountry("Spain");

        Location two = new Location();
        two.setCity("Rome");
        two.setCountry("Italy");

        locationRepository.saveAll(List.of(one, two));

        List<Location> all = locationRepository.findAll();
        assertThat(all).extracting(Location::getCity).contains("Madrid", "Rome");
    }
}
