package com.chumore.emp.model;

import com.chumore.emp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
	public void updateOwnBasicInfo(EmpVO empVO) {
	    // 檢查是否存在相同電話或Email
	    if (empRepository.existsByEmpPhoneAndEmpIdNot(empVO.getEmpPhone(), empVO.getEmpId())) {
	        throw new IllegalArgumentException("此手機號碼已被使用");
	    }
	    if (empRepository.existsByEmpEmailAndEmpIdNot(empVO.getEmpEmail(), empVO.getEmpId())) {
	        throw new IllegalArgumentException("此Email已被使用");
	    }

	    // 取得原有資料
	    EmpVO originalEmp = empRepository.findById(empVO.getEmpId())
	            .orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));
	            
	    // 只更新允許修改的欄位
	    originalEmp.setEmpPhone(empVO.getEmpPhone());
	    originalEmp.setEmpEmail(empVO.getEmpEmail());
	    
	    empRepository.save(originalEmp);
	}
	@Transactional
	public void changePassword(Integer empId, String currentPassword, String newPassword, String confirmPassword) {
	    // 取得員工資料
	    EmpVO emp = empRepository.findById(empId)
	            .orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));

	    // 驗證當前密碼是否正確
	    if (!passwordEncoder.matches(currentPassword, emp.getEmpPassword())) {
	        throw new IllegalArgumentException("目前密碼不正確");
	    }

	    // 驗證新密碼
	    if (newPassword == null || newPassword.trim().isEmpty()) {
	        throw new IllegalArgumentException("新密碼不能為空");
	    }

	    // 驗證確認密碼
	    if (!newPassword.equals(confirmPassword)) {
	        throw new IllegalArgumentException("新密碼與確認密碼不符");
	    }

	    // 驗證新密碼格式（至少包含一個大寫字母和一個數字，長度8-20位）
	    if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$")) {
	        throw new IllegalArgumentException("新密碼必須包含至少一個大寫字母和一個數字，長度在8-20位之間");
	    }

	    // 加密並更新密碼
	    emp.setEmpPassword(passwordEncoder.encode(newPassword));
	    empRepository.save(emp);
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
        try {
            // 獲取現有員工資料
            EmpVO existingEmp = empRepository.findById(empId)
                    .orElseThrow(() -> new NoSuchElementException("找不到該員工資料"));

            // 檢查手機號碼唯一性（排除當前員工）
            if (!existingEmp.getEmpPhone().equals(dto.getEmpPhone()) &&
                empRepository.existsByEmpPhoneAndEmpIdNot(dto.getEmpPhone(), empId)) {
                throw new IllegalArgumentException("此手機號碼已被其他員工使用");
            }

            // 檢查 Email 唯一性（排除當前員工）
            if (!existingEmp.getEmpEmail().equals(dto.getEmpEmail()) &&
                empRepository.existsByEmpEmailAndEmpIdNot(dto.getEmpEmail(), empId)) {
                throw new IllegalArgumentException("此 Email 已被其他員工使用");
            }

            // 更新欄位
            existingEmp.setEmpName(dto.getEmpName());
            existingEmp.setEmpPhone(dto.getEmpPhone());
            existingEmp.setEmpEmail(dto.getEmpEmail());
            existingEmp.setEmpRole(dto.getEmpRole());
            existingEmp.setEmpAccountStatus(dto.getEmpAccountStatus());
            existingEmp.setEmpHireDate(dto.getEmpHireDate());
            existingEmp.setEmpResignDate(dto.getEmpResignDate());

            // 保存更新
            empRepository.save(existingEmp);
            
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("數據完整性錯誤: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalStateException("更新員工資料時發生錯誤: " + e.getMessage(), e);
        }
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
	    emp.setEmpPassword(passwordEncoder.encode("A1234567")); // 設置預設密碼
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