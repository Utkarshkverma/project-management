package com.vermau2k01.project_management;

import com.vermau2k01.project_management.entity.Roles;
import com.vermau2k01.project_management.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class ProjectManagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProjectManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RolesRepository roleRepository) {
		return args -> {
			if (roleRepository.findByRole("USER").isEmpty()) {
				roleRepository.save(Roles.builder().role("USER").build());
			}
		};
	}


}
