package com.dineout.search;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	@RequestMapping(value = "/search/ping", method = RequestMethod.GET)
	public @ResponseBody String ping() {
		return "{\"responseHeader\":{\"appName\":\"DineoutAPI\"},\"status\":\"OK\"}";
	}
}

