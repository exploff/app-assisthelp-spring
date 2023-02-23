package org.assisthelp.com.repository;

import org.assisthelp.com.entity.AppRole;
import org.assisthelp.com.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
    List<AppUser> findByRoles(AppRole role);
}
