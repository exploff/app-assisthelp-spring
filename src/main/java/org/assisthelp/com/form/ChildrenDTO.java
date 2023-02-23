package org.assisthelp.com.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildrenDTO {

    private long childrenId;

    private String firstName;

    private String lastName;

    private float snackPrice;

    private float mealPrice;

    private float additionalHourPrice;

    private float hourPrice;

    private double salary;

    private boolean gender;

    private Long userId;
}
