package com.example.optimalschedule.common.secutity.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private int id;
    private String fullName;
    private String email;
    /*
        1: admin
        2: driver
        3: passenger
     */
    private int role;
    @JsonIgnore
    private String password;

    public UserDetailsImpl(int id, String fullName, String username, int role, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = username;
        this.role = role;
        this.password = password;
    }

    public static UserDetailsImpl build(int id, String fullName, String email, int role, String password) {
        return new UserDetailsImpl(id, fullName, email, role, password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_PASSENGER");
        if (role == 1) authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        else if (role == 2) authority = new SimpleGrantedAuthority("ROLE_DRIVER");
        return Arrays.asList(authority);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(fullName, user.fullName);
    }
}