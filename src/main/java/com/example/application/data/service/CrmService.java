package com.example.application.data.service;

import com.example.application.data.entity.Place;
import com.example.application.data.entity.TripDetail;
import com.example.application.data.repository.PlaceRepository;
import com.example.application.data.repository.TripDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrmService {

    private final TripDetailRepository tripDetailRepository;
    private final PlaceRepository placeRepository;

    public CrmService(TripDetailRepository tripDetailRepository, PlaceRepository placeRepository) {
        this.tripDetailRepository = tripDetailRepository;
        this.placeRepository = placeRepository;
    }

    public List<TripDetail> findAllDestination(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return tripDetailRepository.findAll();
        }
        else {
            return tripDetailRepository.search(stringFilter);
        }
    }

    public void deleteTrip(TripDetail tripDetail) {
        tripDetailRepository.delete(tripDetail);
    }

    public void saveTrip(TripDetail tripDetail) {
        if (tripDetail == null) {
            System.err.println("Contact is null. Are you sure you have connected your form to the application?");
            return;
        }
        tripDetailRepository.save(tripDetail);
    }

    public List<Place> findAllPlaces() {
        return placeRepository.findAll();
    }
}
