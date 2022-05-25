package com.example.application.data.repository;

import com.example.application.data.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, String> {
    public Place findByName(String name);
}
