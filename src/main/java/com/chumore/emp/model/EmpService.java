package com.chumore.emp.model;

import com.chumore.emp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EmpService {

	@Autowired
	private EmpRepository empRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// @Autowired
	// private EmailService emailService;

	// ====== 一般員工相關方法 ======

	// 查看個人資料
	@PreAuthorize("hasRole('ROLE_USER')")
	public EmpBasicViewDTO getOwnBasicInfo(Integer empId) {
		return empRepository.findBasicInfoById(empId).orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));
	}

	// 更新個人資料
	@PreAuthorize("hasRole('ROLE_USER')")
	public void updateOwnBasicInfo(Integer empId, EmpBasicUpdateDTO dto) {
		// 檢查是否存在相同電話或Email
		if (empRepository.existsByEmpPhoneAndEmpIdNot(dto.getEmpPhone(), empId)) {
			throw new IllegalArgumentException("此手機號碼已被使用");
		}
		if (empRepository.existsByEmpEmailAndEmpIdNot(dto.getEmpEmail(), empId)) {
			throw new IllegalArgumentException("此Email已被使用");
		}

		empRepository.updateBasicInfo(empId, dto.getEmpPhone(), dto.getEmpEmail());
	}

	// ====== 管理員相關方法 ======

	// 管理員查看所有員工資料
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EmpFullDTO> getAllEmpsFullInfo() {
		return empRepository.findAllFullInfo();
	}

	// 管理員查看單一員工完整資料
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EmpFullDTO getEmpById(Integer empId) {
		EmpVO emp = empRepository.findById(empId).orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));
		return EmpFullDTO.fromEntity(emp);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String resetPassword(Integer empId) { // 改為回傳密碼字串
		EmpVO emp = empRepository.findById(empId).orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));

		// 生成臨時密碼
		String tempPassword = generateTempPassword();

		// 加密臨時密碼
		emp.setEmpPassword(passwordEncoder.encode(tempPassword));

		// 儲存到資料庫
		empRepository.save(emp);

		// 直接回傳臨時密碼，而不是發送郵件
		return tempPassword;
	}

//  此為透過信箱發送暫時密碼修改密碼的方法，待核心功能開發後再啟用	
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	public void addEmp(EmpFullDTO dto) {
//	    // 檢查帳號是否已存在
//	    if (empRepository.existsByEmpAccount(dto.getEmpAccount())) {
//	        throw new IllegalArgumentException("此帳號已存在");
//	    }
//
//	    // 生成隨機密碼
//	    String tempPassword = generateTempPassword();
//
//	    // 轉換DTO為Entity並儲存
//	    EmpVO emp = new EmpVO();
//	    emp.setEmpName(dto.getEmpName());
//	    emp.setEmpAccount(dto.getEmpAccount());
//	    emp.setEmpPassword(passwordEncoder.encode(tempPassword));  // 加密隨機密碼
//	    emp.setEmpPhone(dto.getEmpPhone());
//	    emp.setEmpEmail(dto.getEmpEmail());
//	    emp.setEmpAccountStatus(dto.getEmpAccountStatus());
//	    emp.setEmpRole(dto.getEmpRole());
//	    emp.setEmpHireDate(dto.getEmpHireDate());
//	    emp.setEmpResignDate(dto.getEmpResignDate());
//	    
//	    empRepository.save(emp);
//
//	    // 發送密碼到員工信箱
//	    emailService.sendPasswordResetEmail(emp.getEmpEmail(), tempPassword);
//	}
	// 更新員工資料
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateEmp(Integer empId, EmpFullDTO dto) {
		EmpVO emp = empRepository.findById(empId).orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));

		// 不更新密碼，只更新其他資料
		emp.setEmpName(dto.getEmpName());
		emp.setEmpAccount(dto.getEmpAccount());
		emp.setEmpPhone(dto.getEmpPhone());
		emp.setEmpEmail(dto.getEmpEmail());
		emp.setEmpRole(dto.getEmpRole());
		emp.setEmpAccountStatus(dto.getEmpAccountStatus());
		emp.setEmpHireDate(dto.getEmpHireDate());
		emp.setEmpResignDate(dto.getEmpResignDate());

		empRepository.save(emp);
	}

	// 生成臨時密碼的方法
	private String generateTempPassword() {
		// 生成8位隨機密碼，包含數字和字母
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}

	// 查詢在職員工
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EmpVO> getActiveEmps() {
		return empRepository.findAllActiveEmps();
	}
	//新增員工
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void addEmp(EmpFullDTO dto) {
		// 檢查帳號是否已存在
		if (empRepository.existsByEmpAccount(dto.getEmpAccount())) {
			throw new IllegalArgumentException("此帳號已存在");
		}

		// 檢查手機號碼是否已存在
		if (empRepository.existsByEmpPhone(dto.getEmpPhone())) {
			throw new IllegalArgumentException("此手機號碼已被使用");
		}

		// 檢查Email是否已存在
		if (empRepository.existsByEmpEmail(dto.getEmpEmail())) {
			throw new IllegalArgumentException("此Email已被使用");
		}

		// 建立新的員工實體
		EmpVO emp = new EmpVO();
		emp.setEmpName(dto.getEmpName());
		emp.setEmpAccount(dto.getEmpAccount());
		emp.setEmpPassword(passwordEncoder.encode(dto.getEmpPassword())); // 加密密碼
		emp.setEmpPhone(dto.getEmpPhone());
		emp.setEmpEmail(dto.getEmpEmail());
		emp.setEmpAccountStatus(dto.getEmpAccountStatus());
		emp.setEmpRole(dto.getEmpRole());
		emp.setEmpHireDate(dto.getEmpHireDate());
		emp.setEmpResignDate(dto.getEmpResignDate());

		empRepository.save(emp);
	}
	// ====== 共用方法 ======

	private void updateEmpFromDTO(EmpVO emp, EmpFullDTO dto) {
		emp.setEmpName(dto.getEmpName());
		emp.setEmpAccount(dto.getEmpAccount());
		// 加密密碼
		emp.setEmpPassword(passwordEncoder.encode(dto.getEmpPassword()));
		emp.setEmpPhone(dto.getEmpPhone());
		emp.setEmpEmail(dto.getEmpEmail());
		emp.setEmpAccountStatus(dto.getEmpAccountStatus());
		emp.setEmpRole(dto.getEmpRole());
		emp.setEmpHireDate(dto.getEmpHireDate());
		emp.setEmpResignDate(dto.getEmpResignDate());
	}

	// 根據帳號查詢員工（用於登入驗證）
	public EmpVO getEmpByAccount(String account) {
		return empRepository.findByEmpAccount(account).orElseThrow(() -> new UsernameNotFoundException("帳號不存在"));
	}
}