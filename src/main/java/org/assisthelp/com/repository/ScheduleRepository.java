package org.assisthelp.com.repository;

import org.assisthelp.com.entity.Children;
import org.assisthelp.com.entity.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByChildren(Children children);

    List<Schedule> findByScheduleDateBetween(Date start, Date end);

    @Query("SELECT s FROM Schedule s WHERE YEAR(s.scheduleDate) = :year AND MONTH(s.scheduleDate) = :month AND s.children = :children")
    List<Schedule> findByScheduleDateMonthAndYearAndChildren(@Param("month") int month, @Param("year") int year, @Param("children") Children children);

    Long countAllByChildren(Children children);

    Long countAllByScheduleDateBetween(Date start, Date end);

    @Query("SELECT COUNT(s.id) FROM Schedule s WHERE YEAR(s.scheduleDate) = :year AND MONTH(s.scheduleDate) = :month AND s.children = :children")
    Long countAllScheduleDateMonthAndYearAndChildren(@Param("month") int month, @Param("year") int year, @Param("children") Children children);

    @Query("SELECT s FROM Schedule s JOIN s.children c WHERE c.user.id = :userId ORDER BY s.modifiedDate DESC")
    List<Schedule> findLastSchedulesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.children = :children ORDER BY s.modifiedDate DESC")
    List<Schedule> findLastSchedulesByChildren(@Param("children") Children children, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM Schedule s WHERE YEAR(s.scheduleDate) = :year AND MONTH(s.scheduleDate) = :month AND s.children = :children")
    void deleteByScheduleDateMonthAndYearAndChildren(@Param("month") int month, @Param("year") int year, @Param("children") Children children);


    @Modifying
    @Query(value = "DELETE FROM Schedule s WHERE s.children = :children")
    void deleteByChildren(@Param("children") Children children);


}
