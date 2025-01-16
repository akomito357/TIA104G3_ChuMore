package com.chumore.discpts.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chumore.discpts.dto.DiscPtsAvailableDTO;
import com.chumore.discpts.dto.DiscPtsSummaryDTO;

@Repository
public interface DiscPtsRepository extends JpaRepository<DiscPtsVO, Integer> {
    
    @Query("SELECT new com.chumore.discpts.dto.DiscPtsSummaryDTO(" +
           "d.rest.restName, " +
           "CAST(SUM(CASE WHEN d.expDate >= CURRENT_DATE THEN d.discPtsQty ELSE 0 END) AS long), " +
           "(SELECT CAST(SUM(d2.discPtsQty) AS long) " +
           " FROM DiscPtsVO d2 " +
           " WHERE d2.rest.restId = d.rest.restId " +
           " AND d2.member.memberId = d.member.memberId " +
           " AND d2.expDate = (" +
           "    SELECT MIN(d3.expDate) " +
           "    FROM DiscPtsVO d3 " +
           "    WHERE d3.rest.restId = d.rest.restId " +
           "    AND d3.member.memberId = d.member.memberId " +
           "    AND d3.expDate >= CURRENT_DATE" +
           ")), " +
           "(SELECT MIN(d4.expDate) " +
           " FROM DiscPtsVO d4 " +
           " WHERE d4.rest.restId = d.rest.restId " +
           " AND d4.member.memberId = d.member.memberId " +
           " AND d4.expDate >= CURRENT_DATE)) " +
           "FROM DiscPtsVO d " +
           "WHERE d.member.memberId = :memberId " +
           "GROUP BY d.rest.restId, d.rest.restName " +
           "HAVING SUM(CASE WHEN d.expDate >= CURRENT_DATE THEN d.discPtsQty ELSE 0 END) > 0 " +  // 新增這行
           "ORDER BY d.rest.restName")
    List<DiscPtsSummaryDTO> findPointsSummaryByMember(
        @Param("memberId") Integer memberId
    );

    @Query("SELECT new com.chumore.discpts.dto.DiscPtsAvailableDTO "
    		+ "(dp.member.memberId, dp.rest.restId, SUM(dp.discPtsQty) AS availablePoints)"
    		+ " FROM DiscPtsVO dp " 
    		+ "WHERE expDate >= CURRENT_DATE() AND dp.rest.restId = :restId " 
    		+ "GROUP BY dp.member.memberId HAVING dp.member.memberId = :memberId")
    DiscPtsAvailableDTO findAvailablePointsByMemberAndRest(@Param("memberId") Integer memberId, @Param("restId") Integer restId);
    
    @Query("FROM DiscPtsVO dp WHERE expDate >= CURRENT_DATE() AND dp.rest.restId = :restId AND dp.member.memberId = :memberId ORDER BY dp.expDate ASC")
    List<DiscPtsVO> findAvailablePointsListByMemberAndRest(@Param("memberId") Integer memberId, @Param("restId") Integer restId);
    
    @Query("FROM DiscPtsVO dp WHERE expDate = :expDate AND dp.rest.restId = :restId AND dp.member.memberId = :memberId")
    List<DiscPtsVO> findRecentPointsByMemberAndRest(@Param("memberId") Integer memberId, @Param("restId") Integer restId, @Param("expDate") LocalDate expDate);

}