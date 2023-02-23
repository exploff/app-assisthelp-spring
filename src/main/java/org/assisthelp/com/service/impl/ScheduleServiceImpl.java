package org.assisthelp.com.service.impl;

import org.assisthelp.com.entity.Children;
import org.assisthelp.com.entity.Schedule;
import org.assisthelp.com.exception.ScheduleException;
import org.assisthelp.com.form.ScheduleDTO;
import org.assisthelp.com.repository.ChildrenRepository;
import org.assisthelp.com.repository.ScheduleRepository;
import org.assisthelp.com.service.ScheduleService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private ScheduleRepository scheduleRepository;

    private ChildrenRepository childrenRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ChildrenRepository childrenRepository) {
        this.scheduleRepository = scheduleRepository;
        this.childrenRepository = childrenRepository;
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        return this.scheduleRepository.findById(id);
    }

    @Override
    public Schedule addSchedule(ScheduleDTO scheduleDTO, Children children) throws ScheduleException {
        if (scheduleDTO == null || children == null) {
            throw new ScheduleException("ScheduleDTO or Children is null");
        }
        if (scheduleDTO.getScheduleDate() == null) {
            throw new ScheduleException("ScheduleDTO scheduleDate is null");
        }

        Schedule schedule = new Schedule();
        schedule.setScheduleDate(scheduleDTO.getScheduleDate());
        schedule.setChildren(children);

        schedule.setAdditionalHour(scheduleDTO.getAdditionalHour());
        schedule.setStartHour(scheduleDTO.getStartHour());
        schedule.setEndHour(scheduleDTO.getEndHour());
        schedule.setSnack(scheduleDTO.isSnack());
        schedule.setMeal(scheduleDTO.isMeal());
        schedule.setCreatedDate(new Date());
        schedule.setModifiedDate(new Date());

        try {
            Schedule scheduleRes = this.scheduleRepository.save(schedule);
            Collection<Schedule> schedules = children.getSchedules();
            if (schedules == null) {
                schedules = new ArrayList<>();
            }
            schedules.add(scheduleRes);
            this.childrenRepository.save(children);
            return scheduleRes;
        } catch (Exception e) {
            throw new ScheduleException("Error saving schedule : " + e.getMessage());
        }
    }

    @Override
    public Schedule updateSchedule(ScheduleDTO scheduleDTO) throws ScheduleException {
        if (scheduleDTO == null) {
            throw new ScheduleException("ScheduleDTO is null");
        }
        if (scheduleDTO.getScheduleDate() == null) {
            throw new ScheduleException("ScheduleDTO scheduleDate is null");
        }
        if (scheduleDTO.getScheduleId() == 0) {
            throw new ScheduleException("ScheduleId is null");
        }

        Optional<Schedule> scheduleOpt = this.scheduleRepository.findById(scheduleDTO.getScheduleId());
        if (scheduleOpt.isPresent()) {
            Schedule schedule = scheduleOpt.get();
            schedule.setScheduleDate(scheduleDTO.getScheduleDate());
            schedule.setAdditionalHour(scheduleDTO.getAdditionalHour());
            schedule.setStartHour(scheduleDTO.getStartHour());
            schedule.setEndHour(scheduleDTO.getEndHour());
            schedule.setSnack(scheduleDTO.isSnack());
            schedule.setMeal(scheduleDTO.isMeal());
            schedule.setModifiedDate(new Date());
            return schedule;
        }
        throw new ScheduleException("Schedule not found with id " + scheduleDTO.getScheduleId());
    }

    @Override
    public void deleteSchedulesByChildren(Children children) throws ScheduleException {
        if (children == null) {
            throw new ScheduleException("Children is null");
        }
        try {
            this.scheduleRepository.deleteByChildren(children);
        } catch (Exception e) {
            throw new ScheduleException("Error deleting schedules : " + e.getMessage());
        }
    }

    @Override
    public void deleteScheduleById(Long scheduleId) throws ScheduleException {
        if (scheduleId == null) {
            throw new ScheduleException("ScheduleId was null");
        }
        Optional<Schedule> schedule = this.scheduleRepository.findById(scheduleId);
        if (schedule.isPresent()) {
            this.scheduleRepository.deleteById(scheduleId);
        } else {
            throw new ScheduleException("Children not found with id " + scheduleId);
        }
    }

    @Override
    public void deleteByScheduleDateMonthAndYearAndChildren(int month, int year, Children children) throws ScheduleException {
        if (children == null) {
            throw new ScheduleException("Children is null");
        }
        if (year < 0) {
            throw new ScheduleException("Year is less than 0");
        }
        if (month < 0 || month > 12) {
            throw new ScheduleException("Month is less than 0 or greater than 12");
        }
        try {
            this.scheduleRepository.deleteByScheduleDateMonthAndYearAndChildren(month, year, children);
        } catch (Exception e) {
            throw new ScheduleException("Error deleting schedules : " + e.getMessage());
        }

    }

    @Override
    public List<Schedule>  findLastByChildren(Children children, Pageable pageable) throws ScheduleException {
        if (children == null || pageable == null) {
            throw new ScheduleException("Children or Pageable is null");
        }
        try {
            return this.scheduleRepository.findLastSchedulesByChildren(children, pageable);
        } catch (Exception e) {
            throw new ScheduleException("Error finding schedules : " + e.getMessage());
        }
    }

    @Override
    public List<Schedule> findLastSchedulesByUserId(Long userId, Pageable pageable) throws ScheduleException {
        if (userId == null || pageable == null) {
            throw new ScheduleException("UserId or Pageable is null");
        }
        try {
            return this.scheduleRepository.findLastSchedulesByUserId(userId, pageable);
        } catch (Exception e) {
            throw new ScheduleException("Error finding schedules : " + e.getMessage());
        }
    }

    @Override
    public List<Schedule> findByScheduleDateMonthAndYearAndChildren(int month, int year, Children children) throws ScheduleException {
        if (year == 0 || month == 0 || children == null) {
            throw new ScheduleException("Year or Month or Children is null");
        }
        if (year < 0) {
            throw new ScheduleException("Year is less than 0");
        }
        if (month < 0 || month > 12) {
            throw new ScheduleException("Month is less than 0 or greater than 12");
        }
        try {
            return this.scheduleRepository.findByScheduleDateMonthAndYearAndChildren(month, year, children);
        } catch (Exception e) {
            throw new ScheduleException("Error finding schedules : " + e.getMessage());
        }
    }

}
