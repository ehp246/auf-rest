package me.ehp246.aufrest.integration.local.method.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/method", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class MethodController {
	@GetMapping
	public String get() {
		return "GET";
	}

	@PostMapping
	public String post() {
		return "POST";
	}

	@PutMapping
	public String put() {
		return "PUT";
	}

	@DeleteMapping
	public String delete() {
		return "DELETE";
	}

	@PatchMapping
	public String patch() {
		return "PATCH";
	}
}
