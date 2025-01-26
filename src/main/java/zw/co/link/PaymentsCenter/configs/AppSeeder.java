package zw.co.link.PaymentsCenter.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zw.co.link.PaymentsCenter.domain.Role;
import zw.co.link.PaymentsCenter.domain.User;
import zw.co.link.PaymentsCenter.domain.repositories.RoleRepository;
import zw.co.link.PaymentsCenter.domain.repositories.UserRepository;
import zw.co.link.PaymentsCenter.services.UserService;


import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class AppSeeder {

    @Bean
    CommandLineRunner run(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        return args -> {
            long userCount = userRepository.count();
            long roleCount = roleRepository.count();

            if (roleCount == 0) {
                List<Role> roles = List.of(
                        Role.builder().name("ADMIN").build(),
                        Role.builder().name("VENDOR").build()
                );
                roleRepository.saveAll(roles);
            }
            // Seed Users
            if (userCount == 0) {
                log.info("GENERATING ADMIN USER");

                User adminUser = User.builder()
                        .firstName("Administrator")
                        .lastName("Administrator")
                        .password("Pass123*")
                        .email("admin@pay.link.co.zw")
                        .gender("male")
                        .roles(Set.of(Role.builder().id(1L).build()))
                        .build();

                userService.saveUser(adminUser);

                User vendorUser = User.builder()
                        .firstName("Vendor")
                        .lastName("Vendor")
                        .password("Pass123*")
                        .email("vend@pay.link.co.zw")
                        .gender("male")
                        .roles(Set.of(Role.builder().id(2L).build()))
                        .build();


                userService.saveUser(vendorUser);// Use the service to handle password encoding if needed
                log.info("DONE GENERATING ADMIN, VENDOR USER");
            }


        };
    }
}