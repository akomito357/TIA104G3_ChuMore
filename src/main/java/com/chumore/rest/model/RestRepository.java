package com.chumore.rest.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RestRepository extends JpaRepository<RestVO, Integer> {

    @Query(value = "FROM RestVO r WHERE r.restCity = ?1 AND r.restDist = ?2")
    List<RestVO> findByCityAndDist(String city, String district);

    Optional<RestVO> findByMerchantEmail(String merchantEmail);

    boolean existsByMerchantEmail(String merchantEmail);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT r.restId FROM RestVO r " +
            "WHERE (:city IS NULL OR r.restCity = :city) " +
            "AND (:district IS NULL OR r.restDist = :district) " +
            "AND (:cuisineTypeId IS NULL OR r.cuisineType.cuisineTypeId = :cuisineTypeId)")
    List<Integer> findRestIdsByOptionalFields(
            @Param("city") String city,
            @Param("district") String district,
            @Param("cuisineTypeId") Integer cuisineTypeId
    );


    @Query("SELECT r FROM RestVO r WHERE r.merchantEmail = :merchantEmail")
    Optional<RestVO> findByMerchantEmailWithLog(@Param("merchantEmail") String merchantEmail);

}

