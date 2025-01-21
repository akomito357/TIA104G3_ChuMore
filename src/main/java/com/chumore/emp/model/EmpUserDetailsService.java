package com.chumore.emp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpUserDetailsService implements UserDetailsService {
    
    @Autowired
    private EmpRepository empRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根據帳號查找員工
        EmpVO emp = empRepository.findByEmpAccount(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到帳號：" + username));
        
        // 先檢查帳號狀態
        if (emp.getEmpAccountStatus() == 0) {
            throw new DisabledException("此帳號已被停用");
        }
        
        // 設定角色
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (emp.getEmpRole() == 1) {  // 1代表管理員
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // 所有員工都有USER角色
        
        // 回傳 UserDetails 物件，注意這裡不再設置 disabled
        return User.builder()
                .username(emp.getEmpAccount())
                .password(emp.getEmpPassword())
                .authorities(authorities)
                .build();
    }
}