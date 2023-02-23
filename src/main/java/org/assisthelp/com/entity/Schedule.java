package org.assisthelp.com.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"scheduleDate", "idChildren"}))
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(nullable = false)
    private Date scheduleDate;

    private LocalTime additionalHour;

    private LocalTime startHour;

    private LocalTime endHour;

    private boolean snack;

    private boolean meal;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "idChildren", nullable = false)
    private Children children;
}
