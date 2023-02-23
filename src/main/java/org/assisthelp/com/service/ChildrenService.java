package org.assisthelp.com.service;

import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.entity.Children;
import org.assisthelp.com.exception.ChildrenException;
import org.assisthelp.com.form.ChildrenDTO;

import java.util.List;
import java.util.Optional;

public interface ChildrenService {

    Children addChildren(ChildrenDTO childrenDTO, AppUser appUser) throws ChildrenException;

    Children updateChildren(ChildrenDTO childrenDTO) throws ChildrenException;

    void deleteChildrensByUser(AppUser user) throws ChildrenException;

    void deleteChildrenById(Long childrenId) throws ChildrenException;

    List<Children> findAllByUser(AppUser user);

    Optional<Children> findById(Long childrenId);

}
