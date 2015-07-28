package com.eclat.mcws.spring.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eclat.mcws.persistence.dao.UserDAO;


public class CustomUserDetailsService implements UserDetailsService {

    private UserDAO userDAO;    
 
    public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
         
		com.eclat.mcws.persistence.entity.Users domainUser = userDAO.loadUserByUsername(username);
         
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
 
        return new User(
                domainUser.getUsername(), 
                domainUser.getPassword(), 
                enabled, 
                accountNonExpired, 
                credentialsNonExpired, 
                accountNonLocked,
                getAuthorities(domainUser.getUserRole().getRoles())
        );
    }
     
    public Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
        return authList;
    }
     
    public List<String> getRoles(String role) {
 
        List<String> roles = new ArrayList<String>();
 
        if (role.equalsIgnoreCase("admin")) {
            roles.add("ADMIN");
        } else if (role.equalsIgnoreCase("coder")) {
            roles.add("CODER");
        } else if (role.equalsIgnoreCase("qa")) {
            roles.add("QA");
        } else if (role.equalsIgnoreCase("supervisor")) {
            roles.add("SUPERVISOR");
        }
        return roles;
    }
     
    public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
         
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
