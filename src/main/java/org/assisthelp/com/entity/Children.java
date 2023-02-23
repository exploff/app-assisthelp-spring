package org.assisthelp.com.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Children {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private float snackPrice;

    private float mealPrice;

    private float additionalHourPrice;

    private float hourPrice;

    private double salary;

    private boolean gender;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private AppUser user;

    @JsonIgnore
    @OneToMany(mappedBy = "children", fetch = FetchType.EAGER)
    private Collection<Schedule> schedules;

}
