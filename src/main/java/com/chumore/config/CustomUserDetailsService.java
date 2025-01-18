
package com.chumore.config;

import com.chumore.member.model.MemberRepository;
import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestRepository;
import com.chumore.rest.model.RestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final MemberRepository memberRepository;
    private final RestRepository restRepository;

    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository, RestRepository restRepository) {
        this.memberRepository = memberRepository;
        this.restRepository = restRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("正在認證用戶，電子郵件: {}", email);

        UserDetails userDetails = findMemberByEmail(email);
        if (userDetails != null) {
            logger.info("用戶認證成功（一般會員）: {}", email);
            return userDetails;
        }

        userDetails = findRestaurantByEmail(email);
        if (userDetails != null) {
            logger.info("用戶認證成功（餐廳會員）: {}", email);
            return userDetails;
        }

        logger.warn("用戶認證失敗，未找到相符的帳號: {}", email);
        throw new UsernameNotFoundException("找不到用戶帳號或帳號未啟用");
    }

    /**
     * 尋找一般會員
     */
    private UserDetails findMemberByEmail(String email) {
        MemberVO member = memberRepository.findByMemberEmail(email).orElse(null);
        if (member != null) {
            return createUserDetails(
                    member.getMemberEmail(),
                    member.getMemberPassword(),
                    "ROLE_MEMBER"
            );
        }
        return null;
    }

    /**
     * 尋找餐廳會員
     */
    private UserDetails findRestaurantByEmail(String email) {
        logger.debug("尋找餐廳會員，Email: {}", email);
        
        RestVO restaurant = restRepository.findByMerchantEmail(email).orElse(null);
        
        if (restaurant != null) {
            logger.debug("找到餐廳會員，狀態：approvalStatus={}, businessStatus={}", 
                        restaurant.getApprovalStatus(), 
                        restaurant.getBusinessStatus());
            
            // 只檢查營業狀態
            if (restaurant.getBusinessStatus() == 1) {
                return createUserDetails(
                        restaurant.getMerchantEmail(),
                        restaurant.getMerchantPassword(),
                        "ROLE_RESTAURANT"
                );
            } else {
                logger.warn("餐廳帳號未啟用: {}", email);
                throw new UsernameNotFoundException("餐廳帳號未啟用");
            }
        }
        
        logger.debug("未找到餐廳會員: {}", email);
        return null;
    }

    /**
     * 通用的 UserDetails 建立方法
     */
    private UserDetails createUserDetails(String email, String password, String role) {
        logger.debug("建立用戶對象，電子郵件: {}, 角色: {}", email, role);
        return User.builder()
                .username(email)
                .password(password)
                .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
