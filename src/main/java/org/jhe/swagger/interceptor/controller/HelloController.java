package org.jhe.swagger.interceptor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/hellos")
@Api(tags = "Hellos")
public class HelloController {
	@GetMapping
	public String sayHello() {
		return "Hello, world !";
	}

}
