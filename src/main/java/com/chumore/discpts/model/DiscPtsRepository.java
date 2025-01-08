package com.chumore.discpts.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.chumore.discpts.dto.DiscPtsSummaryDTO;
import java.util.List;



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
}