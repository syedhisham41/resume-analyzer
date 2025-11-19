import { showAlert } from './common.js';

async function guestTokenGeneration() {
	try {
		const response = await fetch('/api/guest/login', {
			method: 'POST'
		});

		if (!response.ok) {
			showAlert("Guest token generation failed");
			throw new Error('Failed to generate guest_token');
		}

		const tokenData = await response.json();
		sessionStorage.setItem("guest_token", tokenData.token);
		return tokenData.token;
	} catch (err) {
		console.error(err);
	}
}

async function populateResultCard(jdText, resumeText) {
	const token = sessionStorage.getItem("guest_token");

	const response = await fetch('/api/guest/analyze', {
		method: "POST",
		headers: {
			"Authorization": `Bearer ${token}`,
			"Content-Type": "application/json"
		},
		body: JSON.stringify({ jd_text: jdText, resume_text: resumeText })
	});

	if (!response.ok) {
		showAlert("Analysis failed");
		throw new Error('Failed to analyze');
	}

	const data = await response.json();
	return data;
}

document.addEventListener("DOMContentLoaded", () => {

	guestTokenGeneration();

	const analyzeBtn = document.getElementById("analyzeBtn");
	const clearBtn = document.getElementById("clearBtn");
	const jdText = document.getElementById("jdText");
	const resumeText = document.getElementById("resumeText");
	const spinner = document.getElementById("spinner");
	const resultCard = document.getElementById("resultCard");
	const skillList = document.getElementById("skillList");
	const inputSection = document.querySelector(".input-section");
	const actionButtons = document.querySelector(".action-buttons");

	analyzeBtn.addEventListener("click", async () => {
		const jd = jdText.value.trim();
		const resume = resumeText.value.trim();
		if (!jd || !resume) {
			showAlert("Please paste both Job Description and Resume.");
			return;
		}

		// Fade out
		inputSection.style.transition = "opacity 0.5s ease, transform 0.5s ease";
		actionButtons.style.transition = "opacity 0.5s ease";
		inputSection.style.opacity = 0;
		actionButtons.style.opacity = 0;
		inputSection.style.transform = "translateY(-20px)";

		setTimeout(async () => {
			inputSection.style.display = "none";
			actionButtons.style.display = "none";
			spinner.classList.remove("hidden");

			try {
				const data = await populateResultCard(jd, resume);
				spinner.classList.add("hidden");

				// Populate result card
				const fitScore = data.overallFit * 100;
				document.getElementById("fitScore").textContent = fitScore + '%';

				let verdict = "Analyzing your match...";
				if (fitScore >= 70) verdict = "🔥 Excellent Match! You're almost job-ready.";
				else if (fitScore >= 50) verdict = "💡 Good Match — a few tweaks could perfect it.";
				else verdict = "⚙️ Poor Match — some key skills might be missing.";

				document.getElementById("fitVerdict").textContent = verdict;

				if (data.topSkills.length > 0) {
					skillList.innerHTML = "";
					data.topSkills.forEach(skill => {
						const li = document.createElement("li");
						li.textContent = skill;
						skillList.appendChild(li);
					});
				} else {
					skillList.innerHTML = "";
					const li = document.createElement("li");
					li.textContent = "No matching skills found";
					li.style.color = "#777"; // optional subtle style
					skillList.appendChild(li);
					document.querySelector('.skills-note').classList.add("hidden");
				}


				resultCard.classList.add("visible");

			} catch (err) {
				console.error(err);
				showAlert("Failed to analyze");
				spinner.classList.add("hidden");
			}

		}, 400); // allow fade to complete
	});

	clearBtn.addEventListener("click", () => {
		// Reset view
		inputSection.style.display = "flex";
		inputSection.style.opacity = 1;
		actionButtons.style.display = "flex";
		actionButtons.style.opacity = 1;
		resultCard.classList.remove("visible");
		spinner.classList.add("hidden");
		jdText.value = "";
		resumeText.value = "";
	});
});
