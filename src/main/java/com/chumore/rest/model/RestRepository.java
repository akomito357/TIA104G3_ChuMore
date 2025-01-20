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

    List<RestVO> findByRestIdIn(List<Integer> restIds);

    @Query("SELECT r FROM RestVO r WHERE r.merchantEmail = :merchantEmail")
    Optional<RestVO> findByMerchantEmailWithLog(@Param("merchantEmail") String merchantEmail);

    @Query(value = "select * from chumore.rest where business_status=1 order by rand() limit ?1;", nativeQuery = true)
    List<RestVO> findRandomRest(Integer randomCount);
    
}

