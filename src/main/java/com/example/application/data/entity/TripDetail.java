package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class TripDetail extends AbstractEntity {

    @ManyToOne
    private Person tripCreator;

    @NotNull
    private LocalDateTime timeOfDeparture;

    @ManyToOne
    private Place fromLocation;

    @ManyToOne
    private Place toLocation;

    private Integer occupancyLeft;

    public Person getTripCreator() {
        return tripCreator;
    }

    public void setTripCreator(Person tripCreator) {
        this.tripCreator = tripCreator;
    }

    public LocalDateTime getTimeOfDeparture() {
        return timeOfDeparture;
    }

    public void setTimeOfDeparture(LocalDateTime timeOfDeparture) {
        this.timeOfDeparture = timeOfDeparture;
    }

    public Place getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Place placeOfDeparture) {
        this.fromLocation = placeOfDeparture;
    }

    public Place getToLocation() {
        return toLocation;
    }

    public void setToLocation(Place toLocation) {
        this.toLocation = toLocation;
    }

    public Integer getOccupancyLeft() {
        return occupancyLeft;
    }

    public void setOccupancyLeft(Integer occupancyLeft) {
        this.occupancyLeft = occupancyLeft;
    }
}
