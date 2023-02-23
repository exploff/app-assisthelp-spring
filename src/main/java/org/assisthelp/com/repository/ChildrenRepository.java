package org.assisthelp.com.repository;

import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.entity.Children;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildrenRepository extends JpaRepository<Children, Long> {
    List<Children> findAllByUser(AppUser user);
}
