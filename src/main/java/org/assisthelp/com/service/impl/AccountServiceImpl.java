package org.assisthelp.com.service.impl;

import org.assisthelp.com.entity.AppRole;
import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.exception.AccountException;
import org.assisthelp.com.exception.ChildrenException;
import org.assisthelp.com.repository.AppRoleRepository;
import org.assisthelp.com.repository.AppUserRepository;
import org.assisthelp.com.service.AccountService;
import org.assisthelp.com.service.ChildrenService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private ChildrenService childrenService;
    private PasswordEncoder passwordEncoder;


    public AccountServiceImpl(AppUserRepository appUserRepository, ChildrenService childrenService,
                              AppRoleRepository appRoleRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.childrenService = childrenService;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser addNewUser(AppUser appUser) throws AccountException {
        String password = appUser.getPassword();
        appUser.setPassword(this.passwordEncoder.encode(password));

        AppRole roleUser = appRoleRepository.findByRoleName("USER");
        if (roleUser == null) {
            roleUser = new AppRole(null, "USER", new Date(), new Date());
            appRoleRepository.save(roleUser);
        }
        if (!appUser.getRoles().contains(roleUser)) {
            appUser.getRoles().add(roleUser);
        }

        appUser.setCreatedDate(new Date());
        appUser.setModifiedDate(new Date());

        try {
            return this.appUserRepository.save(appUser);
        } catch (Exception e) {
            throw new AccountException("User already exists");
        }
    }

    @Override
    public AppRole addNewRole(AppRole appRole) {
        appRole.setCreatedDate(new Date());
        appRole.setModifiedDate(new Date());
        appRole.setRoleName(appRole.getRoleName().toUpperCase());
        return this.appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String roleName) throws AccountException {
        AppUser appUser = this.appUserRepository.findByUsername(username);
        AppRole appRole = this.appRoleRepository.findByRoleName(roleName);
        if (appUser != null && appRole != null) {
            if (!appUser.getRoles().contains(appRole)) {
                appUser.getRoles().add(appRole);
            } else {
                throw new AccountException("User already has this role");
            }
        } else {
            throw new AccountException("User or role not found");
        }
    }

    @Override
    public void deleteRoleToUser(String username, String roleName) throws AccountException {
        AppUser appUser = this.appUserRepository.findByUsername(username);
        AppRole appRole = this.appRoleRepository.findByRoleName(roleName);
        if (appUser != null && appRole != null) {
            if (appUser.getRoles().contains(appRole)) {
                appUser.getRoles().remove(appRole);
            } else {
                throw new AccountException("User doesn't have this role");
            }
        } else {
            throw new AccountException("User or role not found");
        }
    }

    @Override
    public void deleteRole(String roleName) throws AccountException {
        AppRole appRole = this.appRoleRepository.findByRoleName(roleName);

        List<AppUser> appUsers = this.appUserRepository.findByRoles(appRole);
        for (AppUser appUser : appUsers) {
            appUser.getRoles().remove(appRole);
        }

        if (appRole != null) {
            this.appRoleRepository.delete(appRole);
        } else {
            throw new AccountException("Role not found");
        }
    }

    @Override
    public void deleteUser(String username) throws AccountException {
        AppUser appUser = this.appUserRepository.findByUsername(username);
        try {
            this.childrenService.deleteChildrensByUser(appUser);
            if (appUser != null) {
                this.appUserRepository.delete(appUser);
            } else {
                throw new AccountException("User not found");
            }
        } catch (ChildrenException e) {
            throw new AccountException(e.getMessage());
        }
    }

    @Override
    public AppUser findByUsername(String username) {
        return this.appUserRepository.findByUsername(username);
    }

    @Override
    public boolean existsAppUserByEmailOrUsername(String email, String username) {
    	return this.appUserRepository.existsAppUserByEmailOrUsername(email, username);
    }

    @Override
    public Optional<AppUser> findByUserId(Long userId) {
        return this.appUserRepository.findById(userId);
    }

    @Override
    public List<AppUser> findAllUsers() {
        return this.appUserRepository.findAll();
    }

    @Override
    public List<AppRole> findAllRoles() {
        return this.appRoleRepository.findAll();
    }

    @Override
    public AppRole findRoleByName(String roleName) {
        return this.appRoleRepository.findByRoleName(roleName.toUpperCase());
    }

    @Override
    public AppRole updateRole(AppRole appRole) {
        AppRole appRoleUpdated = this.appRoleRepository.findByRoleName(appRole.getRoleName());
        appRoleUpdated.setModifiedDate(new Date());
        return appRoleUpdated;
    }

    @Override
    public AppUser updateUser(AppUser appUser) throws AccountException {

        AppUser appUserUpdated = this.appUserRepository.findByUsername(appUser.getUsername());

        if (appUser.getPassword() != null) {
            if (!this.passwordEncoder.matches(appUser.getPassword(), appUserUpdated.getPassword())) {
                throw new AccountException("Password doesn't match");
            } else {
                appUserUpdated.setPassword(this.passwordEncoder.encode(appUser.getPassword()));
            }
        }

        if (appUser.getEmail() != null) {
        	appUserUpdated.setEmail(appUser.getEmail());
        }

        appUserUpdated.setFirstName(appUser.getFirstName());
        appUserUpdated.setLastName(appUser.getLastName());
        appUserUpdated.setPhone(appUser.getPhone());
        appUserUpdated.setZip(appUser.getZip());
        appUserUpdated.setCity(appUser.getCity());
        appUserUpdated.setAddress(appUser.getAddress());
        appUserUpdated.setCountry(appUser.getCountry());
        appUserUpdated.setBirthdayDate(appUser.getBirthdayDate());
        appUserUpdated.setIndemnite(appUser.getIndemnite());
        appUserUpdated.setDescription(appUser.getDescription());
        
        appUserUpdated.setModifiedDate(new Date());

        return appUserUpdated;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = this.appUserRepository.findByUsername(username);

        if(user == null)
            throw new UsernameNotFoundException("User with username : " + username + " not found !");
        else {
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles()
                            .stream()
                            .map(role-> new SimpleGrantedAuthority(role.getRoleName()))
                            .collect(Collectors.toSet())
            );
        }
    }
}
