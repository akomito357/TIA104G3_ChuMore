package com.chumore.config;

import com.chumore.auth.dto.AuthenticatedUser;
import com.chumore.member.model.MemberRepository;
import com.chumore.rest.model.RestRepository;
import com.chumore.rest.model.RestVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final MemberRepository memberRepository;
    private final RestRepository restRepository;
    private final HttpSession session;

    @Autowired
    public CustomUserDetailsService(
            MemberRepository memberRepository,
            RestRepository restRepository,
            HttpSession session) {
        this.memberRepository = memberRepository;
        this.restRepository = restRepository;
        this.session = session;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("開始認證用戶，Email: {}", email);

        UserDetails userDetails = findMemberByEmail(email);
        if (userDetails != null) {
            logger.info("認證成功（一般會員）: {}", email);
            return userDetails;
        }

        userDetails = findRestaurantByEmail(email);
        if (userDetails != null) {
            logger.info("認證成功（餐廳會員）: {}", email);
            return userDetails;
        }

        logger.error("認證失敗，找不到對應的用戶: {}", email);
        throw new UsernameNotFoundException("找不到用戶或用戶未啟用");
    }

    private UserDetails findMemberByEmail(String email) {
        return memberRepository.findByMemberEmail(email)
                .map(member -> {
                    logger.debug("找到一般會員: {}", member);
                    
                    // 建立 AuthenticatedUser 物件
                    AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                            .userId(member.getMemberId())
                            .memberId(member.getMemberId())
                            .email(member.getMemberEmail())
                            .password(member.getMemberPassword())
                            .userType(AuthenticatedUser.TYPE_MEMBER)
                            .name(member.getMemberName())
                            .approvalStatus(1)  // 一般會員預設為已啟用
                            .build();

                    // 設置會話屬性
                    session.setAttribute("memberId", member.getMemberId());
                    session.setAttribute("userType", AuthenticatedUser.TYPE_MEMBER);
                    
                    return authenticatedUser;
                })
                .orElseGet(() -> {
                    logger.debug("未找到一般會員: {}", email);
                    return null;
                });
    }

    private UserDetails findRestaurantByEmail(String email) {
        return restRepository.findByMerchantEmail(email)
                .map(restaurant -> {
                    logger.debug("找到餐廳會員: {}", restaurant);

                    if (restaurant.getApprovalStatus() != 1) {
                        logger.warn("餐廳會員未通過審核，但允許登入: {}", restaurant.getMerchantEmail());
                    }

                    // 建立 AuthenticatedUser 物件
                    AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                            .userId(restaurant.getRestId())
                            .restId(restaurant.getRestId())
                            .email(restaurant.getMerchantEmail())
                            .password(restaurant.getMerchantPassword())
                            .userType(AuthenticatedUser.TYPE_RESTAURANT)
                            .name(restaurant.getRestName())
                            .approvalStatus(restaurant.getApprovalStatus())
                            .businessStatus(restaurant.getBusinessStatus())
                            .build();

                    session.setAttribute("restId", restaurant.getRestId());
                    session.setAttribute("userType", AuthenticatedUser.TYPE_RESTAURANT);

                    return authenticatedUser;
                })
                .orElseGet(() -> {
                    logger.warn("未找到餐廳會員: {}", email);
                    return null;
                });
    }
}