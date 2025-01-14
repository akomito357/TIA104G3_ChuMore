package com.chumore.rest.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RestRepository extends JpaRepository<RestVO, Integer> {

	@Query(value = "from RestVO r where r.restCity = ?1 and r.restDist = ?2")
	List<RestVO> findByCityAndDist(String city, String district);

	// 添加以下方法來支援認證功能
	Optional<RestVO> findByMerchantEmail(String merchantEmail);

	boolean existsByMerchantEmail(String merchantEmail);

	boolean existsByPhoneNumber(String phoneNumber);

	// 新增食品業者登錄字號驗證方法
	boolean existsByRestRegist(String restRegist);

	// 建議新增的其他實用方法
	Optional<RestVO> findByRestRegist(String restRegist);

	Optional<RestVO> findByMerchantEmailAndApprovalStatus(String merchantEmail, Integer approvalStatus);

}
