package org.assisthelp.com.service;


import org.assisthelp.com.entity.AppRole;
import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.exception.AccountException;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    AppUser addNewUser(AppUser appUser) throws AccountException;

    AppRole addNewRole(AppRole appRole);

    void addRoleToUser(String username, String roleName) throws AccountException;

    AppUser findByUsername(String username);

    Optional<AppUser> findByUserId(Long userId);

    List<AppUser> findAllUsers();

    List<AppRole> findAllRoles();

    AppRole findRoleByName(String roleName);

    AppRole updateRole(AppRole appRole);

    AppUser updateUser(AppUser appUser) throws AccountException;

    void deleteRoleToUser(String username, String roleName) throws AccountException;

    void deleteRole(String roleName) throws AccountException;

    void deleteUser(String username) throws AccountException;
}
