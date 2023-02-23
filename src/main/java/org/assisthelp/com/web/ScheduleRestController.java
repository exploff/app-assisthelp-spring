package org.assisthelp.com.web;

import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.entity.Children;
import org.assisthelp.com.entity.Schedule;
import org.assisthelp.com.exception.ScheduleException;
import org.assisthelp.com.form.ScheduleDTO;
import org.assisthelp.com.service.AccountService;
import org.assisthelp.com.service.ChildrenService;
import org.assisthelp.com.service.ScheduleService;
import org.assisthelp.com.web.constants.APIConstants;
import org.assisthelp.com.web.model.APIResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/schedule")
public class ScheduleRestController {

    private ScheduleService scheduleService;

    private ChildrenService childrenService;

    private AccountService accountService;

    public ScheduleRestController(ScheduleService scheduleService, AccountService accountService, ChildrenService childrenService) {
        this.scheduleService = scheduleService;
        this.accountService = accountService;
        this.childrenService = childrenService;
    }

    @GetMapping("/schedule")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> schedule(Long scheduleId) {
        if (scheduleId != null) {
            Optional<Schedule> optSchedule = this.scheduleService.findById(scheduleId);
            return optSchedule.map(schedule -> ResponseEntity.ok(new APIResponse("schedule", schedule))).orElseGet(() -> ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "Schedule with id " + scheduleId + " not found")));
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "scheduleId is required"));
        }
    }


    @PostMapping("/schedule")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> addSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        if (scheduleDTO != null) {
            if (scheduleDTO.getScheduleDate() != null && scheduleDTO.getChildrenId() != null) {
                try {
                    Optional<Children> childrenOptional = this.childrenService.findById(scheduleDTO.getChildrenId());
                    if (childrenOptional.isPresent()) {
                        Schedule schedule = this.scheduleService.addSchedule(scheduleDTO, childrenOptional.get());
                        return ResponseEntity.ok(new APIResponse("schedule", schedule));
                    } else {
                        return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                                "children not found"));
                    }
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "scheduleDate and childrenId are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "scheduleDTO is required"));
        }
    }

    @GetMapping("/lastScheduleByUsername")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> lastScheduleByUsername(String username, @RequestParam(required = false, defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(0, size);
        if (username != null) {
            AppUser user = this.accountService.findByUsername(username);
            if (user != null) {
                try {
                    List<Schedule> schedules = this.scheduleService.findLastSchedulesByUserId(user.getId(), pageable);
                    return ResponseEntity.ok(new APIResponse("schedules", schedules));
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "User with username " + username + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "username is required"));
        }
    }

    @GetMapping("/lastScheduleByChildrenId")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> lastScheduleByChildrenId(Long childrenId, @RequestParam(required = false, defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(0, size);
        if (childrenId != null) {
            Optional<Children> childrenOpt = this.childrenService.findById(childrenId);
            if (childrenOpt.isPresent()) {
                try {
                    List<Schedule> schedules = this.scheduleService.findLastByChildren(childrenOpt.get(), pageable);
                    return ResponseEntity.ok(new APIResponse("schedules", schedules));
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "Children with id " + childrenId + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "childrenId is required"));
        }
    }

    @GetMapping("/scheduleByDateMonthYearAndChildren")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> scheduleByDateMonthYearAndChildren(int year, int month, Long childrenId) {
        if (year != 0 && month != 0 && childrenId != null) {
            if (month < 1 || month > 12) {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "month must be between 1 and 12"));
            }
            Optional<Children> childrenOpt = this.childrenService.findById(childrenId);
            if (childrenOpt.isPresent()) {
                try {
                    List<Schedule> schedules = this.scheduleService.findByScheduleDateMonthAndYearAndChildren(month, year, childrenOpt.get());
                    return ResponseEntity.ok(new APIResponse("schedules", schedules));
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "Children with id " + childrenId + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "year, month and childrenId are required"));
        }
    }

    @PutMapping("/schedule")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> updateSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        if (scheduleDTO != null) {
            if (scheduleDTO.getScheduleDate() != null && scheduleDTO.getScheduleId() != 0) {
                try {
                    Schedule schedule = this.scheduleService.updateSchedule(scheduleDTO);
                    return ResponseEntity.ok(new APIResponse("schedule", schedule));
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "scheduleDate and scheduleId are required"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "scheduleDTO is required"));
        }
    }

    @DeleteMapping("/schedule")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> deleteSchedule(Long scheduleId) {
        if (scheduleId != null) {
            try {
                this.scheduleService.deleteScheduleById(scheduleId);
                return ResponseEntity.ok(new APIResponse("Schedule deleted", ""));
            } catch (ScheduleException e) {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        e.getMessage()));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "scheduleId is required"));
        }
    }

    @DeleteMapping("/scheduleByChildren")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> deleteScheduleByChildren(Long childrenId) {
        if (childrenId != null) {
            Optional<Children> childrenOpt = this.childrenService.findById(childrenId);
            if (childrenOpt.isPresent()) {
                try {
                    this.scheduleService.deleteSchedulesByChildren(childrenOpt.get());
                    return ResponseEntity.ok(new APIResponse("Schedule deleted", ""));
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "Children with id " + childrenId + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "childrenId is required"));
        }
    }


    @DeleteMapping("/scheduleByChildrenDateYearAndMonth")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ASSMAT')")
    public ResponseEntity<APIResponse> deleteScheduleByChildrenDateYearAndMonth(int year, int month, Long childrenId) {
        if (childrenId != null && year != 0 && month != 0) {
            Optional<Children> childrenOpt = this.childrenService.findById(childrenId);
            if (childrenOpt.isPresent()) {
                try {
                    this.scheduleService.deleteByScheduleDateMonthAndYearAndChildren(month, year, childrenOpt.get());
                    return ResponseEntity.ok(new APIResponse("Schedule deleted", ""));
                } catch (ScheduleException e) {
                    return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                            e.getMessage()));
                }
            } else {
                return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                        "Children with id " + childrenId + " not found"));
            }
        } else {
            return ResponseEntity.badRequest().body(new APIResponse(APIConstants.ERROR_BAD_REQUEST,
                    "childrenId and year and month is required"));
        }
    }
}
