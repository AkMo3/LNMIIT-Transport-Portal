package com.example.application.data.repository;

import com.example.application.data.entity.TripDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TripDetailRepository extends JpaRepository<TripDetail, UUID> {
    @Query("select c from TripDetail c " +
            "where lower(c.toLocation.name) like lower(concat('%', :searchTerm, '%'))")
    List<TripDetail> search(@Param("searchTerm") String searchTerm);

    List<TripDetail> findByOrderByTimeOfDepartureDesc();
}
