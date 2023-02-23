package org.assisthelp.com.web.constants;

import org.springframework.http.HttpStatus;

public class APIConstants {

    public static final String ERROR_BAD_REQUEST = "error : " + HttpStatus.BAD_REQUEST;
    public static final String ROLE_NAME_REQUIRED = "Attribute roleName is required";
    public static final String USERNAME_REQUIRED = "Attribute username is required";
    public static final String ROLE_INFO_REQUIRED = "Role info is required";
    public static final String USER_INFO_REQUIRED = "User info is required";
    public static final String CHILDREN_INFO_REQUIRED = "Children info is required";

    public static final String SCHEDULE_INFO_REQUIRED = "Schedule info is required";

    public static final String MESSAGE = "message";
}
