package com.example.application.data.generator;

import com.example.application.data.entity.Person;
import com.example.application.data.entity.Place;
import com.example.application.data.entity.TripDetail;
import com.example.application.data.repository.PersonRepository;
import com.example.application.data.repository.PlaceRepository;
import com.example.application.data.repository.TripDetailRepository;
import org.springframework.context.annotation.Profile;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringComponent
public class DataGenerator {

    @Bean
    @Profile({"!production"})
    public CommandLineRunner loadData(TripDetailRepository tripDetailRepository, PersonRepository personRepository,
                                      PlaceRepository placeRepository) {

        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (tripDetailRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");
            ExampleDataGenerator<Person> personGenerator = new ExampleDataGenerator<>(Person.class, LocalDateTime.now());
            personGenerator.setData(Person::setFirstName, DataType.FIRST_NAME);
            personGenerator.setData(Person::setLastName, DataType.LAST_NAME);
            personGenerator.setData(Person::setRollNumber, DataType.EMAIL);
            personGenerator.setData(Person::setHashedPassword, DataType.TWO_WORDS);
            List<Person> personList = personRepository.saveAll(personGenerator.create(5, seed));
            List<Place> placeList = placeRepository.saveAll(Stream.of("Raja Park", "Railway Station"
                    , "LNMIIT").map(Place::new).collect(Collectors.toList()));

            logger.info("... generating 50 Trip entities...");
            ExampleDataGenerator<TripDetail> tripDetailGenerator = new ExampleDataGenerator<>(TripDetail.class,
                    LocalDateTime.now());
            tripDetailGenerator.setData(TripDetail::setOccupancyLeft, DataType.NUMBER_UP_TO_10);
            tripDetailGenerator.setData(TripDetail::setTimeOfDeparture, DataType.DATETIME_NEXT_7_DAYS);

            Random r = new Random(seed);
            List<TripDetail> tripDetails = tripDetailGenerator.create(50, seed).stream().peek(tripDetail -> {
                tripDetail.setTripCreator(personList.get(r.nextInt(personList.size())));
                tripDetail.setToLocation(placeList.get(r.nextInt(placeList.size())));
                Place source = placeList.get(r.nextInt(placeList.size()));
                while (source == tripDetail.getToLocation()) source = placeList.get(r.nextInt(placeList.size()));
                tripDetail.setFromLocation(source);
            }).collect(Collectors.toList());

            tripDetailRepository.saveAll(tripDetails);

            logger.info("Generated demo data");
        };
    }

}
