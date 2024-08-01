package com.vermau2k01.project_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Users implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique = true, nullable = false)
    @NaturalId(mutable = true)
    private String email;
    private String passcode;
    private LocalDate dateOfBirth;
    private boolean accountLocked;
    private boolean enabled;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Roles> role;

    @Override
    public String getName() {
        return passcode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this
                .role
                .stream()
                .map(r->new SimpleGrantedAuthority(r.getRole()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return passcode;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getFullName()
    {
        return firstname + " " + lastname;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
