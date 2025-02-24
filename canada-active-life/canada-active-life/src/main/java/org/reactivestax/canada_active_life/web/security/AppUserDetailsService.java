package org.reactivestax.canada_active_life.web.security;

import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class AppUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private FamilyManagementService familyManagementService;

    public AppUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String memberLoginId) throws UsernameNotFoundException {
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(memberLoginId);

        return User.withUsername(familyMember.getMemberLoginId())
                .password(familyMember.getFamilyGroup().getFamilyPin())
                .authorities("UNVERIFIED")
                .passwordEncoder(passwordEncoder::encode)
                .build();
    }
}
