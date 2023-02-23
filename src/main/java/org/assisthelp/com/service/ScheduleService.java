package org.assisthelp.com.service;


import org.assisthelp.com.entity.Children;
import org.assisthelp.com.entity.Schedule;
import org.assisthelp.com.exception.ScheduleException;
import org.assisthelp.com.form.ScheduleDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    Optional<Schedule> findById(Long id);

    Schedule addSchedule(ScheduleDTO scheduleDTO, Children children) throws ScheduleException;

    Schedule updateSchedule(ScheduleDTO scheduleDTO) throws ScheduleException;

    void deleteSchedulesByChildren(Children children) throws ScheduleException;

    void deleteScheduleById(Long scheduleId) throws ScheduleException;

    void deleteByScheduleDateMonthAndYearAndChildren(int year, int month, Children children) throws ScheduleException;

    List<Schedule> findLastByChildren(Children children, Pageable pageable) throws ScheduleException;

    List<Schedule> findLastSchedulesByUserId(Long userId, Pageable pageable) throws ScheduleException;

    List<Schedule> findByScheduleDateMonthAndYearAndChildren(int month, int year, Children children) throws ScheduleException;

}