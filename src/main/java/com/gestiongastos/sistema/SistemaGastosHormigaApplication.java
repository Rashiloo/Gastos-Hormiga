package com.gestiongastos.sistema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.gestiongastos.sistema")
@EntityScan("com.gestiongastos.sistema.model")
@EnableJpaRepositories("com.gestiongastos.sistema.repository")
public class SistemaGastosHormigaApplication {
	public static void main(String[] args) {
		SpringApplication.run(SistemaGastosHormigaApplication.class, args);
	}
}