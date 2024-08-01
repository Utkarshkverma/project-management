package com.vermau2k01.project_management.security;

import com.vermau2k01.project_management.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail)
            throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByEmail(userEmail).orElseThrow(()->
                new UsernameNotFoundException("User not found"));
    }
}
