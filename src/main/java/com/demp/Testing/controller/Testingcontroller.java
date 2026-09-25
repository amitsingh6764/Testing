package com.demp.Testing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/find")
public class Testingcontroller {

	@GetMapping("name")
	public String getName() {
		return "Hello Amit Singh";
	}

}
