package com.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("crm")
public class CustomerController {

	// @Autowired
	// StaffService staffService;

	@RequestMapping("telephone_solicitation")
	public String telephoneSolicitation(Model model) {

		return "crm/telephone_solicitation";
	}

	@RequestMapping("customer_data")
	public String customerData(Model model) {

		return "crm/customer_data";
	}
}
