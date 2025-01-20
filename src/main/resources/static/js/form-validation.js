// form-validation.js
const cityDistricts = {
  "臺北市": [
    "中正區","大同區","中山區","萬華區","信義區","松山區","大安區",
    "南港區","北投區","內湖區","士林區","文山區"
  ],
  "新北市": [
    "板橋區","新莊區","泰山區","林口區","淡水區","金山區","八里區",
    "萬里區","石門區","三芝區","瑞芳區","汐止區","平溪區","貢寮區",
    "雙溪區","深坑區","石碇區","新店區","坪林區","烏來區","中和區",
    "永和區","土城區","三峽區","樹林區","鶯歌區","三重區","蘆洲區",
    "五股區"
  ],
  "基隆市": [
    "仁愛區","中正區","信義區","中山區","安樂區","暖暖區","七堵區"
  ],
  "桃園市": [
    "桃園區","中壢區","平鎮區","八德區","楊梅區","蘆竹區","龜山區",
    "龍潭區","大溪區","大園區","觀音區","新屋區","復興區"
  ],
  "新竹縣": [
    "竹北市","竹東鎮","新埔鎮","關西鎮","峨眉鄉","寶山鄉","北埔鄉",
    "橫山鄉","芎林鄉","湖口鄉","新豐鄉","尖石鄉","五峰鄉"
  ],
  "新竹市": [
    "東區","北區","香山區"
  ],
  "苗栗縣": [
    "苗栗市","通霄鎮","苑裡鎮","竹南鎮","頭份鎮","後龍鎮","卓蘭鎮",
    "西湖鄉","頭屋鄉","公館鄉","銅鑼鄉","三義鄉","造橋鄉","三灣鄉",
    "南庄鄉","大湖鄉","獅潭鄉","泰安鄉"
  ],
  "臺中市": [
    "中區","東區","南區","西區","北區","北屯區","西屯區","南屯區",
    "太平區","大里區","霧峰區","烏日區","豐原區","后里區","東勢區",
    "石岡區","新社區","和平區","神岡區","潭子區","大雅區","大肚區",
    "龍井區","沙鹿區","梧棲區","清水區","大甲區","外埔區","大安區"
  ],
  "南投縣": [
    "南投市","埔里鎮","草屯鎮","竹山鎮","集集鎮","名間鄉","鹿谷鄉",
    "中寮鄉","魚池鄉","國姓鄉","水里鄉","信義鄉","仁愛鄉"
  ],
  "彰化縣": [
    "彰化市","員林鎮","和美鎮","鹿港鎮","溪湖鎮","二林鎮","田中鎮",
    "北斗鎮","花壇鄉","芬園鄉","大村鄉","永靖鄉","伸港鄉","線西鄉",
    "福興鄉","秀水鄉","埔心鄉","埔鹽鄉","大城鄉","芳苑鄉","竹塘鄉",
    "社頭鄉","二水鄉","田尾鄉","埤頭鄉","溪州鄉"
  ],
  "雲林縣": [
    "斗六市","斗南鎮","虎尾鎮","西螺鎮","土庫鎮","北港鎮","莿桐鄉",
    "林內鄉","古坑鄉","大埤鄉","崙背鄉","二崙鄉","麥寮鄉","臺西鄉",
    "東勢鄉","褒忠鄉","四湖鄉","口湖鄉","水林鄉","元長鄉"
  ],
  "嘉義縣": [
    "太保市","朴子市","布袋鎮","大林鎮","民雄鄉","溪口鄉","新港鄉",
    "六腳鄉","東石鄉","義竹鄉","鹿草鄉","水上鄉","中埔鄉","竹崎鄉",
    "梅山鄉","番路鄉","大埔鄉","阿里山鄉"
  ],
  "嘉義市": [
    "東區","西區"
  ],
  "臺南市": [
    "中西區","東區","南區","北區","安平區","安南區","永康區","歸仁區",
    "新化區","左鎮區","玉井區","楠西區","南化區","仁德區","關廟區",
    "龍崎區","官田區","麻豆區","佳里區","西港區","七股區","將軍區",
    "學甲區","北門區","新營區","後壁區","白河區","東山區","六甲區",
    "下營區","柳營區","鹽水區","善化區","大內區","山上區","新市區",
    "安定區"
  ],
  "高雄市": [
    "楠梓區","左營區","鼓山區","三民區","鹽埕區","前金區","新興區",
    "苓雅區","前鎮區","小港區","旗津區","鳳山區","大寮區","鳥松區",
    "林園區","仁武區","大樹區","大社區","岡山區","路竹區","橋頭區",
    "梓官區","彌陀區","永安區","燕巢區","田寮區","阿蓮區","茄萣區",
    "湖內區","旗山區","美濃區","內門區","杉林區","甲仙區","六龜區",
    "茂林區","桃源區","那瑪夏區"
  ],
  "屏東縣": [
    "屏東市","潮州鎮","東港鎮","恆春鎮","萬丹鄉","長治鄉","麟洛鄉",
    "九如鄉","里港鄉","鹽埔鄉","高樹鄉","萬巒鄉","內埔鄉","竹田鄉",
    "新埤鄉","枋寮鄉","新園鄉","崁頂鄉","林邊鄉","南州鄉","佳冬鄉",
    "琉球鄉","車城鄉","滿州鄉","枋山鄉","霧台鄉","瑪家鄉","泰武鄉",
    "來義鄉","春日鄉","獅子鄉","牡丹鄉","三地門鄉"
  ],
  "宜蘭縣": [
    "宜蘭市","羅東鎮","蘇澳鎮","頭城鎮","礁溪鄉","壯圍鄉","員山鄉",
    "冬山鄉","五結鄉","三星鄉","大同鄉","南澳鄉"
  ],
  "花蓮縣": [
    "花蓮市","鳳林鎮","玉里鎮","新城鄉","吉安鄉","壽豐鄉","秀林鄉",
    "光復鄉","豐濱鄉","瑞穗鄉","萬榮鄉","富里鄉","卓溪鄉"
  ],
  "臺東縣": [
    "臺東市","成功鎮","關山鎮","長濱鄉","海端鄉","池上鄉","東河鄉",
    "鹿野鄉","延平鄉","卑南鄉","金峰鄉","大武鄉","達仁鄉","綠島鄉",
    "蘭嶼鄉","太麻里鄉"
  ],
  "澎湖縣": [
    "馬公市","湖西鄉","白沙鄉","西嶼鄉","望安鄉","七美鄉"
  ],
  "金門縣": [
    "金城鎮","金湖鎮","金沙鎮","金寧鄉","烈嶼鄉","烏坵鄉"
  ],
  "連江縣": [
    "南竿鄉","北竿鄉","莒光鄉","東引鄉"
  ]
};


class FormValidator {
    constructor() {
        this.isSubmitting = false;
        this.initializeFormValidation();
        this.initializePasswordToggle();
        this.initializeLocationSelectors();
        this.initializeRegistrationValidation();
		this.initializePasswordValidation();
    }

	initializeFormValidation() {
	    const form = document.querySelector('.needs-validation');
	    const submitButton = form.querySelector('button[type="submit"]');

	    form.addEventListener('submit', async (event) => {
	        event.preventDefault();
	        
	        if (this.isSubmitting) return;

	        // 驗證密碼
	        const isPasswordValid = this.validatePassword();
	        
	        // 檢查所有表單欄位的有效性
	        if (!form.checkValidity() || !isPasswordValid) {
	            event.stopPropagation();
	            form.classList.add('was-validated');
	            return;
	        }

	        try {
	            this.isSubmitting = true;
	            submitButton.disabled = true;
	            
	            const formData = new FormData(form);
	            
	            // 確保密碼欄位有被包含在formData中
	            if (!formData.get('password') || !formData.get('confirmPassword')) {
	                throw new Error('密碼欄位不能為空');
	            }

	            const response = await fetch('/auth/register/restaurant', {
	                method: 'POST',
	                body: formData,
	                headers: {
	                    'Accept': 'application/json'
	                }
	            });

	            if (!response.ok) {
	                const errorData = await response.json();
	                throw new Error(errorData.message || '註冊失敗');
	            }

	            this.showSuccess('註冊成功！請等待系統審核');
	            setTimeout(() => {
	                window.location.href = '/auth/login?registration=success';
	            }, 2000);

	        } catch (error) {
	            console.error('註冊錯誤:', error);
	            this.showError(error.message || '註冊過程發生錯誤，請稍後再試');
	        } finally {
	            this.isSubmitting = false;
	            submitButton.disabled = false;
	        }
	    });
	}

	validatePassword() {
	    const password = document.querySelector('input[name="password"]');
	    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
	    
	    if (!password || !confirmPassword) return false;

	    const passwordValue = password.value;
	    const confirmValue = confirmPassword.value;

	    // 只在需要時才進行完整驗證
	    if (passwordValue.length < 8) {
	        return true; // 允許繼續輸入
	    }

	    const requirements = {
	        length: passwordValue.length >= 8 && passwordValue.length <= 20,
	        uppercase: /[A-Z]/.test(passwordValue),
	        lowercase: /[a-z]/.test(passwordValue),
	        number: /\d/.test(passwordValue)
	    };

	    const isValid = Object.values(requirements).every(Boolean);
	    
	    if (!isValid) {
	        password.setCustomValidity('密碼必須包含大小寫字母、數字，長度為8-20個字元');
	        return false;
	    }

	    if (confirmValue && passwordValue !== confirmValue) {
	        confirmPassword.setCustomValidity('密碼與確認密碼不符');
	        return false;
	    }

	    password.setCustomValidity('');
	    confirmPassword.setCustomValidity('');
	    return true;
	}

    initializePasswordToggle() {
        const toggleButtons = document.querySelectorAll('.btn-outline-secondary');
        toggleButtons.forEach(button => {
            button.addEventListener('click', function() {
                const input = this.previousElementSibling;
                const icon = this.querySelector('i');
                
                input.type = input.type === 'password' ? 'text' : 'password';
                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });
        });
    }

    initializeLocationSelectors() {
        const citySelect = document.getElementById('city');
        const districtSelect = document.getElementById('district');
        
        if (!citySelect || !districtSelect) return;

        const cityFragment = document.createDocumentFragment();
        cityFragment.appendChild(new Option('請選擇縣市', ''));

        Object.keys(cityDistricts).forEach(city => {
            cityFragment.appendChild(new Option(city, city));
        });

        citySelect.innerHTML = '';
        citySelect.appendChild(cityFragment);
        
        citySelect.addEventListener('change', () => {
            this.updateDistricts(citySelect.value, districtSelect);
        });
    }

    updateDistricts(selectedCity, districtSelect) {
        districtSelect.innerHTML = '<option value="">請選擇地區</option>';
        districtSelect.disabled = !selectedCity;
        
        if (selectedCity && cityDistricts[selectedCity]) {
            const fragment = document.createDocumentFragment();
            cityDistricts[selectedCity].forEach(district => {
                fragment.appendChild(new Option(district, district));
            });
            districtSelect.appendChild(fragment);
        }
    }

    initializeRegistrationValidation() {
        const registrationInput = document.getElementById('registrationNumber');
        if (registrationInput) {
            registrationInput.addEventListener('input', function() {
                const value = this.value;
                const pattern = /^[A-Za-z]-[0-9]{9}-[0-9]{5}-[0-9]{1}$/;
                
                this.setCustomValidity(
                    pattern.test(value) ? '' : 
                    '請輸入正確格式的食品業者登錄字號（例：B-123456789-12345-1）'
                );
            });
        }
    }
	
	initializePasswordValidation() {
	    const password = document.querySelector('input[name="password"]');
	    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
	    
	    if (password && confirmPassword) {
	        // 修改驗證邏輯，移除自動 reportValidity
	        const validateInputs = () => {
	            const passwordValue = password.value;
	            const confirmValue = confirmPassword.value;

	            // 只在使用者完成輸入後才進行驗證
	            if (passwordValue.length >= 8) {
	                const requirements = {
	                    length: passwordValue.length >= 8 && passwordValue.length <= 20,
	                    uppercase: /[A-Z]/.test(passwordValue),
	                    lowercase: /[a-z]/.test(passwordValue),
	                    number: /\d/.test(passwordValue)
	                };

	                if (!Object.values(requirements).every(Boolean)) {
	                    password.setCustomValidity('密碼必須包含大小寫字母、數字，長度為8-20個字元');
	                } else {
	                    password.setCustomValidity('');
	                }
	            }

	            // 只在確認密碼欄位有值時才檢查是否相符
	            if (confirmValue) {
	                if (passwordValue !== confirmValue) {
	                    confirmPassword.setCustomValidity('密碼與確認密碼不符');
	                } else {
	                    confirmPassword.setCustomValidity('');
	                }
	            }
	        };

	        // 修改事件監聽器，使用 'change' 而不是 'input'
	        password.addEventListener('change', validateInputs);
	        confirmPassword.addEventListener('change', validateInputs);
	        
	        // 防止密碼輸入時的自動跳轉
	        password.addEventListener('keydown', (e) => {
	            if (e.key === 'Tab') {
	                // 允許 Tab 鍵的預設行為
	                return;
	            }
	            // 阻止其他可能導致焦點轉移的行為
	            e.stopPropagation();
	        });
	    }
	}
	
	

    showMessage(message, type) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} mt-3`;
        alertDiv.textContent = message;
        
        const form = document.querySelector('form');
        form.parentNode.insertBefore(alertDiv, form.nextSibling);
        
        setTimeout(() => alertDiv.remove(), 5000);
    }

    showError(message) {
        this.showMessage(message, 'danger');
    }

    showSuccess(message) {
        this.showMessage(message, 'success');
    }
	
	
}

// 當 DOM 載入完成後初始化表單驗證
document.addEventListener('DOMContentLoaded', () => {
    try {
        new FormValidator();
        console.log('表單初始化完成');
    } catch (error) {
        console.error('初始化錯誤:', error);
    }
});