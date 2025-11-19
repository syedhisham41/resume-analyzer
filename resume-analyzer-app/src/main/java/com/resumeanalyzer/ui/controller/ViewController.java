package com.resumeanalyzer.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

	@GetMapping("/welcome")
	public String showWelcomePage(Model model) {
		return "loginPage";
	}

	@GetMapping("/signup")
	public String showSignupPage() {
		return "signup";
	}

	@RequestMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@RequestMapping("/dashboard")
	public String showDashboardPage() {
		return "dashboard";
	}

	@RequestMapping("/jdupload")
	public String showJdUpload() {
		return "jdUpload";
	}

	@RequestMapping("/jdview")
	public String showJdView() {
		return "jdView";
	}

	@RequestMapping("/resumeupload")
	public String showResumeUpload() {
		return "resumeUpload";
	}

	@RequestMapping("/resumeview")
	public String showResumeView() {
		return "resumeView";
	}

	@RequestMapping("/analyzedashboard")
	public String showAnalyzeDashboard() {
		return "analyzeDashboard";
	}

	@RequestMapping("/reports")
	public String showReports() {
		return "reports";
	}
	
	@RequestMapping("/settings")
	public String showSettings() {
		return "settings";
	}
	
	@RequestMapping("/guest")
	public String showGuest() {
		return "guest";
	}

}
