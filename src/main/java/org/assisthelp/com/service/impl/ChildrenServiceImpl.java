package org.assisthelp.com.service.impl;

import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.entity.Children;
import org.assisthelp.com.exception.ChildrenException;
import org.assisthelp.com.exception.ScheduleException;
import org.assisthelp.com.form.ChildrenDTO;
import org.assisthelp.com.repository.AppUserRepository;
import org.assisthelp.com.repository.ChildrenRepository;
import org.assisthelp.com.service.ChildrenService;
import org.assisthelp.com.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ChildrenServiceImpl implements ChildrenService {

    private ChildrenRepository childrenRepository;

    private ScheduleService scheduleService;

    private AppUserRepository appUserRepository;

    public ChildrenServiceImpl(ChildrenRepository childrenRepository, ScheduleService scheduleService, AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
        this.scheduleService = scheduleService;
        this.childrenRepository = childrenRepository;
    }

    @Override
    public Children addChildren(ChildrenDTO childrenDTO, AppUser appUser) throws ChildrenException {
        if (appUser == null || childrenDTO == null) {
            throw new ChildrenException("User or children form was null");
        }

        Children children = new Children();
        children.setCreatedDate(new Date());
        children.setModifiedDate(new Date());

        children.setFirstName(childrenDTO.getFirstName());
        children.setLastName(childrenDTO.getLastName());
        children.setGender(childrenDTO.isGender());
        children.setHourPrice(childrenDTO.getHourPrice());
        children.setAdditionalHourPrice(childrenDTO.getAdditionalHourPrice());
        children.setMealPrice(childrenDTO.getMealPrice());
        children.setSnackPrice(childrenDTO.getSnackPrice());
        children.setSalary(childrenDTO.getSalary());
        children.setUser(appUser);

        try {
            Children childrenRes = this.childrenRepository.save(children);
            appUser.getChildrens().add(childrenRes);
            this.appUserRepository.save(appUser);
            return childrenRes;
        } catch (Exception e) {
            throw new ChildrenException("Error save children : " + e.getMessage());
        }
    }

    @Override
    public Children updateChildren(ChildrenDTO childrenDTO) throws ChildrenException {
        if (childrenDTO == null || childrenDTO.getChildrenId() == 0) {
            throw new ChildrenException("Children form or childrenId was null");
        }

        Optional<Children> childrenOpt = this.childrenRepository.findById(childrenDTO.getChildrenId());
        if (childrenOpt.isPresent()) {
            Children children = childrenOpt.get();
            children.setModifiedDate(new Date());
            children.setFirstName(childrenDTO.getFirstName());
            children.setLastName(childrenDTO.getLastName());
            children.setGender(childrenDTO.isGender());
            children.setHourPrice(childrenDTO.getHourPrice());
            children.setAdditionalHourPrice(childrenDTO.getAdditionalHourPrice());
            children.setMealPrice(childrenDTO.getMealPrice());
            children.setSnackPrice(childrenDTO.getSnackPrice());
            children.setSalary(childrenDTO.getSalary());
            return children;

        }
        throw new ChildrenException("Children not found with id " + childrenDTO.getChildrenId());

    }

    @Override
    public void deleteChildrenById(Long childrenId) throws ChildrenException {
        if (childrenId == null) {
            throw new ChildrenException("ChildrenId was null");
        }
        Optional<Children> childrenOpt = this.childrenRepository.findById(childrenId);
        if (childrenOpt.isPresent()) {
            Children children = childrenOpt.get();
            try {
                this.scheduleService.deleteSchedulesByChildren(children);
            } catch (ScheduleException e) {
                throw new ChildrenException("Error delete schedules from children : " + e.getMessage());
            }
            this.childrenRepository.deleteById(childrenId);
        } else {
            throw new ChildrenException("Children not found with id " + childrenId);
        }
    }

    @Override
    public void deleteChildrensByUser(AppUser user) throws ChildrenException {
        if (user == null) {
            throw new ChildrenException("User was null");
        }
        Collection<Children> childrens = this.childrenRepository.findAllByUser(user);
        for (Children children : childrens) {
            try {
                this.scheduleService.deleteSchedulesByChildren(children);
            } catch (ScheduleException e) {
                throw new ChildrenException("Error delete schedules from children : " + e.getMessage());
            }
            this.childrenRepository.delete(children);
        }
    }

    @Override
    public List<Children> findAllByUser(AppUser user) {
        List<Children> childrens = this.childrenRepository.findAllByUser(user);
        return childrens == null ? new ArrayList<>() : childrens;
    }

    @Override
    public Optional<Children> findById(Long childrenId) {
        return this.childrenRepository.findById(childrenId);
    }

}
