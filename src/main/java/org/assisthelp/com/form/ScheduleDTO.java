package org.assisthelp.com.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private long scheduleId;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date scheduleDate;

    private LocalTime additionalHour;

    private LocalTime startHour;

    private LocalTime endHour;

    private boolean snack;

    private boolean meal;

    private Long childrenId;
}
