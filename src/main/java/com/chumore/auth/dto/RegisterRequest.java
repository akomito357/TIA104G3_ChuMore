package com.chumore.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.sql.Date;
import java.time.LocalDate;

/**
 * 註冊請求的數據傳輸物件
 * 用於處理會員註冊時的資料傳遞
 */
public class RegisterRequest {
    private String memberName;          // 會員姓名
    private String memberEmail;         // 會員電子郵件
    private String memberPassword;      // 會員密碼
    private String memberPhoneNumber;   // 會員手機號碼
    private Integer memberGender;       // 會員性別
    private Date memberBirthdate;       // 會員生日
    private String memberAddress;       // 會員地址
    private String userType;            // 使用者類型（會員/餐廳）

    // 預設建構子
    public RegisterRequest() {
    }

    /**
     * 帶參數的建構子
     * @param memberName 會員姓名
     * @param memberEmail 會員電子郵件
     * @param memberPassword 會員密碼
     * @param memberPhoneNumber 會員手機號碼
     * @param memberGender 會員性別
     * @param memberBirthdate 會員生日
     * @param memberAddress 會員地址
     * @param userType 使用者類型
     */
    public RegisterRequest(String memberName, String memberEmail, String memberPassword, 
                         String memberPhoneNumber, Integer memberGender, 
                         Date memberBirthdate, String memberAddress, String userType) {
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberGender = memberGender;
        this.memberBirthdate = memberBirthdate;
        this.memberAddress = memberAddress;
        this.userType = userType;
    }

    // 取得使用者類型
    public String getUserType() {
        return userType;
    }

    // 設定使用者類型
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 轉換物件內容為字串
     * 注意：密碼欄位會以 [PROTECTED] 顯示以保護隱私
     */
    @Override
    public String toString() {
        return "註冊請求資料{" +
                "會員姓名='" + memberName + '\'' +
                ", 電子郵件='" + memberEmail + '\'' +
                ", 密碼='[PROTECTED]'" +
                ", 手機號碼='" + memberPhoneNumber + '\'' +
                ", 性別=" + memberGender +
                ", 生日=" + memberBirthdate +
                ", 地址='" + memberAddress + '\'' +
                ", 使用者類型='" + userType + '\'' +
                '}';
    }

    /**
     * 比較兩個註冊請求是否相同
     * @param o 要比較的物件
     * @return 如果所有欄位值都相同則返回 true，否則返回 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterRequest that = (RegisterRequest) o;
        return memberName.equals(that.memberName) &&
                memberEmail.equals(that.memberEmail) &&
                memberPassword.equals(that.memberPassword) &&
                memberPhoneNumber.equals(that.memberPhoneNumber) &&
                memberGender.equals(that.memberGender) &&
                memberBirthdate.equals(that.memberBirthdate) &&
                memberAddress.equals(that.memberAddress) &&
                userType.equals(that.userType);
    }
    
    @NotBlank(message = "請再次輸入密碼")
    private String confirmPassword;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * 計算物件的雜湊值
     * 用於在集合中識別物件
     */
    @Override
    public int hashCode() {
        int result = memberName != null ? memberName.hashCode() : 0;
        result = 31 * result + (memberEmail != null ? memberEmail.hashCode() : 0);
        result = 31 * result + (memberPassword != null ? memberPassword.hashCode() : 0);
        result = 31 * result + (memberPhoneNumber != null ? memberPhoneNumber.hashCode() : 0);
        result = 31 * result + (memberGender != null ? memberGender.hashCode() : 0);
        result = 31 * result + (memberBirthdate != null ? memberBirthdate.hashCode() : 0);
        result = 31 * result + (memberAddress != null ? memberAddress.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        return result;
    }

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public String getMemberPassword() {
		return memberPassword;
	}

	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}

	public String getMemberPhoneNumber() {
		return memberPhoneNumber;
	}

	public void setMemberPhoneNumber(String memberPhoneNumber) {
		this.memberPhoneNumber = memberPhoneNumber;
	}

	public Integer getMemberGender() {
		return memberGender;
	}

	public void setMemberGender(Integer memberGender) {
		this.memberGender = memberGender;
	}

	public Date getMemberBirthdate() {
		return memberBirthdate;
	}

	public void setMemberBirthdate(Date memberBirthdate) {
		this.memberBirthdate = memberBirthdate;
	}

	public String getMemberAddress() {
		return memberAddress;
	}

	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}

   
}