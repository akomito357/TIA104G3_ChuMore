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
        logger.debug("開始進行用戶認證流程，電子郵件: {}", email);

        // 嘗試從一般會員中找到匹配
        MemberVO member = memberRepository.findByMemberEmail(email).orElse(null);
        if (member != null) {
            return createMemberUserDetails(member);
        }

        // 如果不是一般會員，嘗試從餐廳會員中找到匹配
        RestVO restaurant = restRepository.findByMerchantEmail(email)
                .filter(rest -> rest.getApprovalStatus() == 1 && rest.getBusinessStatus() == 1)
                .orElse(null);
        if (restaurant != null) {
            return createRestaurantUserDetails(restaurant);
        }

        // 如果都找不到
        logger.warn("用戶認證失敗，未找到相符的帳號: {}", email);
        throw new UsernameNotFoundException("找不到用戶帳號或帳號未啟用");
    }

    private UserDetails createMemberUserDetails(MemberVO member) {
        logger.info("建立一般會員認證資訊: {}", member.getMemberEmail());
        return User.builder()
                .username(member.getMemberEmail())
                .password(member.getMemberPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private UserDetails createRestaurantUserDetails(RestVO restaurant) {
        logger.info("建立餐廳會員認證資訊: {}", restaurant.getMerchantEmail());
        return User.builder()
                .username(restaurant.getMerchantEmail())
                .password(restaurant.getMerchantPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_RESTAURANT")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
