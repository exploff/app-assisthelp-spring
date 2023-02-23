package org.assisthelp.com;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.assisthelp.com.config.RsaKeyConfig;
import org.assisthelp.com.entity.AppUser;
import org.assisthelp.com.entity.Children;
import org.assisthelp.com.form.ChildrenDTO;
import org.assisthelp.com.form.ScheduleDTO;
import org.assisthelp.com.service.AccountService;
import org.assisthelp.com.entity.AppRole;
import org.assisthelp.com.service.ChildrenService;
import org.assisthelp.com.service.ScheduleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfig.class)
public class AssistHelpApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistHelpApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    CommandLineRunner start(AccountService accountService, ChildrenService childrenService, ScheduleService scheduleService) {
        return args -> {

            accountService.addNewRole(new AppRole(null, "USER", new Date(), new Date()));
            accountService.addNewRole(new AppRole(null, "ADMIN", new Date(), new Date()));



            AppUser user = accountService.addNewUser(new AppUser(null, "admin", "1234",
                    "admin@test.com", "", "", "", "", "", "", new Date(), new Date(), new Date(), "", "",
                    0.0, new ArrayList<>(), new ArrayList<>()
                    ));

            accountService.addNewUser(new AppUser(null, "user1", "1234",
                    "user@test.com", "", "", "", "", "", "", new Date(), new Date(), new Date(), "", "",
                    0.0, new ArrayList<>(), new ArrayList<>()
            ));

            accountService.addRoleToUser("admin", "ADMIN");



            Children children = childrenService.addChildren(new ChildrenDTO(0, "enfant 1", "nom", 2, 1, 2, 4, 500, true, user.getId()), user);
            childrenService.addChildren(new ChildrenDTO(0, "enfant 2", "nom famille", 4, 2, 5, 5, 243, true, user.getId()), user);


            scheduleService.addSchedule(new ScheduleDTO(0, new Date(), LocalTime.of(2, 0), LocalTime.of(9, 0), LocalTime.of(17, 0), true, true, children.getId()), children);
            scheduleService.addSchedule(new ScheduleDTO(0, new Date(), LocalTime.of(2, 0), LocalTime.of(6, 0), LocalTime.of(19, 15), true, true, children.getId()), children);
            scheduleService.addSchedule(new ScheduleDTO(0, new Date(), LocalTime.of(2, 0), LocalTime.of(9, 30), LocalTime.of(16, 0), true, true, children.getId()), children);
            scheduleService.addSchedule(new ScheduleDTO(0, new Date(), LocalTime.of(2, 0), LocalTime.of(10, 0), LocalTime.of(20, 0), true, true, children.getId()), children);


            List<AppUser> users = accountService.findAllUsers();
            users.forEach(System.out::println);
        };
    }
}
