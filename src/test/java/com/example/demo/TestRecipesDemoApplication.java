package com.example.demo;

import org.springframework.boot.SpringApplication;

public class TestRecipesDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(RecipesDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
