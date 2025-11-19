// js/dashboard.js
import {
	showToast, showSpinner, hideSpinner, showAlert, runAnalysis,
	AnalyzeSelectionModal
} from './common.js';

document.addEventListener("DOMContentLoaded", () => {
	const token = sessionStorage.getItem("token");
	if (!token) return window.location.href = "/login";

	const headers = {
		"Authorization": `Bearer ${token}`,
		"Content-Type": "application/json"
	};

	// -------------------- INIT PAGE --------------------
	async function initDashboard() {
		showSpinner();
		let dashboardError = null;
		let analysisError = null;

		try {
			await loadUserDashboardData(headers);
		} catch (err) {
			dashboardError = "Could not load dashboard: " + err.message;
		}

		try {
			await loadRecentActivity(headers);
		} catch (err) {
			dashboardError = "Could not load recent activity: " + err.message;
		}

		try {
			await loadLastAnalysisSummary(headers);
		} catch (err) {
			analysisError = "Could not load last analysis";
			const lastAnalysisContainer = document.getElementById("lastAnalysisMetrics");
			if (lastAnalysisContainer) {
				lastAnalysisContainer.innerHTML = `<div class="card metric-card"><h4>Could not load last analysis</h4></div>`;
			}
		} finally {
			hideSpinner();
		}

		if (dashboardError && analysisError) {
			await showAlert(dashboardError);
			await showAlert(analysisError);
		} else if (dashboardError) {
			await showAlert(dashboardError);
		} else if (analysisError) {
			await showAlert(analysisError);
		}

		setupModals();
	}

	// -------------------- SETUP QUICK ACTIONS --------------------
	function setupActions() {
		document.getElementById("uploadResume")?.addEventListener("click", () => window.location.href = "/resumeupload");
		document.getElementById("uploadJD")?.addEventListener("click", () => window.location.href = "/jdupload");

		// Use event delegation in case modal button is rendered dynamically
		/*
		document.body.addEventListener("click", (e) => {
			if (document.getElementById("analyze")) setupModals();
		});
		*/

		// Dropdown toggle
		const dropdownToggle = document.querySelector(".dropdown-toggle");
		const dropdownMenu = document.querySelector(".dropdown-menu");
		if (dropdownToggle && dropdownMenu) {
			dropdownToggle.addEventListener("click", () => dropdownMenu.classList.toggle("show"));
			window.addEventListener("click", e => {
				if (!e.target.matches('.dropdown-toggle') && dropdownMenu.classList.contains("show")) {
					dropdownMenu.classList.remove("show");
				}
			});
		}

		// Logout
		document.getElementById("logoutButton")?.addEventListener("click", () => {
			sessionStorage.removeItem("token");
			window.location.href = "/welcome";
		});

		document.getElementById("editProfile")?.addEventListener("click", () => {
			window.location.href = "/settings";
		});

	}

	// -------------------- INIT --------------------

	setupActions();
	initDashboard();

});


function setupModals() {
	// Initialize modal DOM references first
	AnalyzeSelectionModal.init();



	const openBtn = document.getElementById('analyze');
	if (!openBtn) {
		console.error('Start New Analysis button not found!');
		return;
	}

	openBtn.addEventListener('click', () => {
		AnalyzeSelectionModal.openModal();
	});

}
// =============================================================
//  FUNCTION 1️⃣ → Load User Dashboard Info & Metrics
// =============================================================
export async function loadUserDashboardData(headers) {
	const res = await fetch("/api/dashboard/metrics", { headers });
	if (res.status === 401) return window.location.href = "/login";
	if (!res.ok) throw new Error("Failed to fetch metrics");

	const data = await res.json();

	const elems = {
		totalResumes: document.getElementById("totalResumes"),
		totalJDs: document.getElementById("totalJDs"),
		totalAnalyzes: document.getElementById("totalAnalyzes"),
		averageFit: document.getElementById("averageFit"),
		userName: document.getElementById("userName"),
		userEmail: document.getElementById("userEmail"),
		userRole: document.getElementById("userRole"),
		welcomeMessage: document.getElementById("welcomeMessage")
	};

	if (elems.totalResumes) elems.totalResumes.textContent = data.resumeCount;
	if (elems.totalJDs) elems.totalJDs.textContent = data.jdCount;
	if (elems.totalAnalyzes) elems.totalAnalyzes.textContent = data.analyzeCount;
	if (elems.averageFit) elems.averageFit.textContent = (data.averageOverallFit * 100).toFixed(1) + "%";
	if (elems.userName) elems.userName.textContent = data.name;
	if (elems.userEmail) elems.userEmail.textContent = data.email;
	if (elems.userRole) elems.userRole.textContent = `${data.currentRole} at ${data.currentCompany}`;
	if (elems.welcomeMessage) elems.welcomeMessage.textContent = `Welcome, ${data.name}`;
}

// =============================================================
//  FUNCTION 2️⃣ → Load Last Analysis Summary & Charts
// =============================================================
export async function loadLastAnalysisSummary(headers) {
	const lastAnalysisContainer = document.getElementById("lastAnalysisMetrics");
	const skillChartContainer = document.getElementById("skillDistributionChart");

	const res = await fetch("/api/dashboard/latest", { headers });
	if (res.status === 401) return window.location.href = "/login";

	const data = await res.json().catch(() => null);
	if (!res.ok || !data || res.status === 404) {
		if (lastAnalysisContainer) {
			lastAnalysisContainer.innerHTML = `<div class="card metric-card"><h4>No Analysis Yet</h4></div>`;
		}
		return;
	}

	// --- Metric Cards ---
	if (lastAnalysisContainer) {
		lastAnalysisContainer.innerHTML = "";
		const metrics = [
			{ label: "Overall Fit", value: (data.overall_fit * 100).toFixed(2) + "%" },
			{ label: "Skill Match", value: (data.skill_match_pct * 100).toFixed(2) + "%" },
			{ label: "Title Match", value: (data.title_match_pct * 100).toFixed(2) + "%" },
			{ label: "Qualification Match", value: (data.qual_match_pct * 100).toFixed(2) + "%" }
		];
		metrics.forEach(m => {
			const card = document.createElement("div");
			card.classList.add("card", "metric-card");
			card.innerHTML = `<h4>${m.label}</h4><p>${m.value}</p>`;
			lastAnalysisContainer.appendChild(card);
		});
	}

	// --- Charts ---
	if (skillChartContainer) {
		skillChartContainer.innerHTML = `
            <div class="chart-section">
                <h4 class="chart-title">Skill Match Breakdown</h4>
                <canvas id="skillBreakdownChart"></canvas>
            </div>
            <div class="chart-section">
                <h4 class="chart-title">Fit Composition</h4>
                <canvas id="fitCompositionChart"></canvas>
            </div>
        `;

		// Categorize skill matches
		const matchedSkills = data.matched_skills || {};
		let strong = 0, partial = 0, unmatched = 0;

		Object.values(matchedSkills).forEach(score => {
			if (score >= 0.8) strong++;
			else if (score >= 0.3) partial++;
			else unmatched++;
		});

		// Donut Chart
		const skillCtx = document.getElementById("skillBreakdownChart")?.getContext("2d");
		if (skillCtx) {
			if (window.skillBreakdownChart?.destroy) window.skillBreakdownChart.destroy();
			window.skillBreakdownChart = new Chart(skillCtx, {
				type: "doughnut",
				data: {
					labels: ["Strong Matches (>80%)", "Partial Matches (30–80%)", "Weak/Unmatched (<30%)"],
					datasets: [{
						data: [strong, partial, unmatched],
						backgroundColor: ["#FFD37D", "#FF924F", "#E76530"],
						hoverOffset: 5
					}]
				},
				options: {
					responsive: true,
					plugins: {
						legend: { position: "bottom" },
						title: { display: true, text: "Skill Match Quality" }
					}
				}
			});
		}

		// Fit Composition Donut
		const fitMetrics = {
			"Skill Match": data.skill_match_pct || 0,
			"Verb Match": data.verb_match_pct || 0,
			"Title Match": data.title_match_pct || 0,
			"Qualification Match": data.qual_match_pct || 0
		};
		const fitLabels = Object.keys(fitMetrics);
		const fitValues = Object.values(fitMetrics).map(v => v * 100);
		const fitCtx = document.getElementById("fitCompositionChart")?.getContext("2d");
		if (fitCtx) {
			if (window.fitCompositionChart?.destroy) window.fitCompositionChart.destroy();
			window.fitCompositionChart = new Chart(fitCtx, {
				type: "doughnut",
				data: { labels: fitLabels, datasets: [{ data: fitValues, backgroundColor: ["#FFD37D", "#FFB366", "#FF924F", "#E76530"], hoverOffset: 5 }] },
				options: { responsive: true, plugins: { legend: { position: "bottom" } } }
			});
		}
	}
}

async function loadRecentActivity(headers) {
	const recentActivityList = document.getElementById("recentActivityList");
	const recentActivityContent = document.getElementById("recentActivityContent");

	try {
		const res = await fetch("/api/dashboard/recentactivity", { headers });

		if (res.status === 401) {
			window.location.href = "/login";
			return;
		}

		const data = await res.json().catch(() => null);

		if (!res.ok || !data || data.length === 0) {
			recentActivityContent.innerHTML = `
				<div class="card metric-card">
					<h4>No Activity Yet</h4>
				</div>`;
			return;
		}

		// Clear existing
		recentActivityList.innerHTML = "";

		data.forEach(activity => {
			// Tick for success, cross for failure/others
			const statusIcon = activity.status === "SUCCESS" ? "✓" : "✗";

			const li = document.createElement("li");
			li.classList.add("activity-item");

			li.innerHTML = `
					        <span class="activity-icon">${statusIcon}</span>
					        <div class="activity-info">
					            <span class="activity-details">${activity.details}</span>
					            <span class="activity-meta">
					                • <span class="activity-actionType" style="color: #ff924f; font-weight: 700;">${activity.actionType}</span> 
					                • ${activity.entityName} 
					                • ${new Date(activity.createdAt).toLocaleString()}
					            </span>
					        </div>
					    `;

			recentActivityList.appendChild(li);
		});
	} catch (err) {
		console.error("Failed to load recent activity:", err);
		recentActivityContent.innerHTML = `
			<div class="card metric-card">
				<h4>Error loading activity</h4>
			</div>`;
	}
}


// Expose functions globally if needed
window.loadUserDashboardData = loadUserDashboardData;
window.loadLastAnalysisSummary = loadLastAnalysisSummary;
window.loadRecentActivity = loadRecentActivity;
