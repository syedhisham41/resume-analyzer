import { logActivity } from './common.js';

function attachCardClickHandlers() {
	const cards = document.querySelectorAll(".analysis-card");
	const toggleBtn = document.getElementById("toggleDetailsBtn");
	const toggleInsightsBtn = document.getElementById("toggleInsightsBtn");
	const herocard = document.getElementById("hero-card-id");
	const cardsContainer = document.querySelector(".analysis-cards-container");

	cards.forEach(card => {
		card.addEventListener("click", async () => {
			// --- Collapse existing details before loading new one ---
			const content = document.getElementById("detailsContent");
			const insights = document.getElementById("insightsContainer");

			if (content && content.classList.contains("expanded")) {
				content.classList.remove("expanded");
				content.style.maxHeight = "0px";
				toggleBtn.textContent = "View Details ▼";
				cardsContainer.style.maxHeight = "330px";
			}

			if (insights && insights.classList.contains("expanded")) {
				insights.classList.remove("expanded");
				insights.style.maxHeight = "0px";
				toggleInsightsBtn.textContent = "View Details ▼";
			}

			// --- Load new analysis data ---
			const analyzeId = card.dataset.id;
			const data = await fetchAnalysisDataById(analyzeId);

			if (data) {
				updateHeroCard(data);
				updateInsightsSection(data);
				updateDetailsSection(data);
				herocard.scrollIntoView({ behavior: "smooth", block: "start", hoverOffset: 45 });
			}
		});
	});
}

async function fetchAnalysisDataById(analyzeId) {
	try {
		const token = sessionStorage.getItem("token");
		const headers = {
			"Authorization": `Bearer ${token}`,
			"Content-Type": "application/json"
		};
		const response = await fetch(`/api/analyzepage/analysis?analyzeId=${analyzeId}`, { headers });
		if (!response.ok) throw new Error("Failed to fetch analysis data");
		return await response.json();
	} catch (err) {
		console.error(err);
		return null;
	}
}

function updateHeroCard(data) {
	if (!data) return;

	const heroCard = document.getElementById("hero-card-id");
	heroCard.dataset.id = data.analyzeId;

	// Basic info
	document.getElementById("hero-jd-title").textContent = data.jdTitle || "-";
	document.getElementById("hero-jd-company").textContent = data.jdCompany || "-";
	document.getElementById("hero-resume-title").textContent = data.resumeTitle || "-";
	document.getElementById("hero-analyze-date").textContent = data.createdAt || "-";

	// Fit percentage & comment
	const fitPercent = Math.round((data.overall_fit || 0) * 100);
	document.getElementById("hero-fit-percent").textContent = `${fitPercent}%`;
	document.getElementById("hero-fit-comment").textContent =
		fitPercent >= 75 ? "Excellent fit!" :
			fitPercent >= 55 ? "Good alignment!" :
				fitPercent >= 40 ? "Fair fit" :
					"Needs improvement";

	// Breakdown bars
	document.getElementById("hero-skill-match").value = data.skill_match_pct || 0;
	document.getElementById("hero-verb-match").value = data.verb_match_pct || 0;
	document.getElementById("hero-title-match").value = data.title_match_pct || 0;
	document.getElementById("hero-qual-match").value = data.qual_match_pct || 0;

}

function setupSearchForAnalysis() {
	const inputEl = document.getElementById("searchInput");
	const startEl = document.getElementById("startDate");
	const endEl = document.getElementById("endDate");

	if (!inputEl) return;

	const filterItems = () => {
		const term = inputEl.value.toLowerCase().trim();
		const start = startEl.value ? new Date(startEl.value) : null;
		const end = endEl.value ? new Date(endEl.value) : null;
		if (start) start.setHours(0, 0, 0, 0);
		if (end) end.setHours(23, 59, 59, 999);

		const cards = document.querySelectorAll(".analysis-card");
		let anyVisible = false;

		cards.forEach(card => {
			const title = card.querySelector("h4")?.textContent.toLowerCase() || "";
			const company = card.querySelector(".company")?.textContent.toLowerCase() || "";
			const resume = card.querySelector(".resume-title")?.textContent.toLowerCase() || "";
			const dateText = card.querySelector(".analyze-date")?.textContent || "";
			const itemDate = dateText ? new Date(dateText) : null;

			// Text-based match (title, company, resume, or date text)
			const textMatch =
				!term ||
				title.includes(term) ||
				company.includes(term) ||
				resume.includes(term) ||
				dateText.toLowerCase().includes(term);

			// Inclusive date range match
			let dateMatch = true;
			if (itemDate && (start || end)) {
				if (start && end) dateMatch = itemDate >= start && itemDate <= end;
				else if (start) dateMatch = itemDate >= start;
				else if (end) dateMatch = itemDate <= end;
			}

			const visible = textMatch && dateMatch;

			card.style.display = visible ? "flex" : "none";
			anyVisible ||= visible;
		});

		let emptyState = document.getElementById("emptyStateMsg");
		if (!emptyState) {
			emptyState = document.createElement("div");
			emptyState.id = "emptyStateMsg";
			emptyState.textContent = "No analyses found.";
			document.querySelector(".analysis-cards-container").appendChild(emptyState);
		}
		emptyState.classList.toggle("show", !anyVisible);
	};

	const debounce = (fn, delay = 200) => {
		let timeout;
		return (...args) => {
			clearTimeout(timeout);
			timeout = setTimeout(() => fn(...args), delay);
		};
	};

	const debouncedFilter = debounce(filterItems, 200);

	inputEl.addEventListener("input", debouncedFilter);
	startEl.addEventListener("change", filterItems);
	endEl.addEventListener("change", filterItems);
}


async function loadAnalysisCards(containerId) {
	try {
		const token = sessionStorage.getItem("token");
		const headers = {
			"Authorization": `Bearer ${token}`,
			"Content-Type": "application/json"
		};

		const response = await fetch(`/api/analyzepage/data`, { headers });
		if (response.status === 401) return window.location.href = "/login";
		if (!response.ok) throw new Error("Failed to fetch analyses");

		const data = await response.json();

		console.log("loadAnalysisCards: ", data);

		// assuming getAllAnalyzePerUser is returned as `data.allAnalyses`
		const analyses = data.allAnalyzes || [];
		console.log("analysis: ", analyses);
		populateAnalysisCards(containerId, analyses);

		document.getElementById("totalCount").textContent = data.totalAnalyzeCount;
		document.getElementById("lastUploadedDate").textContent = data.lastAnalyzeDate;
		
		setupSearchForAnalysis();

	} catch (error) {
		console.error("Error loading analysis cards:", error);
	}
}

function populateAnalysisCards(containerId, analyses) {
	const container = document.getElementById(containerId);
	if (!container) return;

	//container.innerHTML = analyses.map(createAnalysisCard).join('');
	container.innerHTML = analyses.sort((a, b) => b.analyzeId - a.analyzeId).map(createAnalysisCard).join('');
}


function createAnalysisCard(analysis) {
	const jdTitle = analysis.jdTitle || "-";
	const company = analysis.jdCompany || "-";
	const resumeTitle = analysis.resumeTitle || "-";
	const analyzeDate = analysis.createdAt ? analysis.createdAt : "-";
	const fitPercent = analysis.overall_fit ? Math.round(analysis.overall_fit * 100) : 0;
	const analyzeId = analysis.analyzeId;


	return `
		        <div class="analysis-card" data-id=${analyzeId}>
		            <div class="type-item-header">
		                <div class="header-left">
		                    <h4>${jdTitle}</h4>
		                    <p class="company">${company}</p>
		                    <p class="resume-title">${resumeTitle}</p>
		                </div>
		                <div class="header-right">
		                    <span class="analyze-date">${analyzeDate}</span>
		                </div>
		            </div>
		            <div class="fit-circle-small">
		                <span>${fitPercent}%</span>
		            </div>
		        </div>
		    `;
}

// Function to populate Detailed Analysis Section
async function updateDetailsSection(data) {
	if (!data) return;

	const content = document.getElementById("detailsContent");

	// --- Extract skill info ---
	const jdSkills = data.jd_skills || [];
	const matchedSkills = data.matched_skills || {};
	const unmatchedSkills = data.unmatched_skills || [];
	const matched = data.matched_verb_phrases || {};
	const unmatched = data.unmatched_verb_phrases || [];

	const totalJDSkills = jdSkills.length;
	const matchedCount = Object.keys(matchedSkills).length;
	const matchedPercent = data.skill_match_pct ? Math.round(data.skill_match_pct * 100) : 0;

	const matchedVerbCount = Object.keys(matched).length;
	const matchedVerbPercent = data.verb_match_pct ? Math.round(data.verb_match_pct * 100) : 0;
	const jdVerbs = data.jd_verbs || [];
	const totalJDVerbs = jdVerbs.length;

	const jdQuals = data.jd_qualifications || [];
	const matchedQuals = data.matched_qualifications || [];
	const totalJdQuals = jdQuals.length;
	const matchedQualCount = matchedQuals.length;
	const qualMatchPercent = data.qual_match_pct ? Math.round(data.qual_match_pct * 100) : 0;


	// --- Categorize matched skills ---
	const highMatches = [];
	const mediumMatches = [];
	const lowMatches = [];

	Object.entries(matchedSkills).forEach(([skill, score]) => {
		const pct = Math.round(score * 100);
		if (pct >= 70) highMatches.push([skill, pct]);
		else if (pct >= 30) mediumMatches.push([skill, pct]);
		else if (pct > 0 && pct < 30) lowMatches.push([skill, pct]);
	});

	const makeSkillItem = (skill, pct, level) => `
		        <div class="skill-item">
		            <span class="skill-name">${skill}</span>
		            <div class="bar-container">
		                <div class="bar-fill ${level}" style="width: ${Math.min(pct * 1.8, 180)}px;"></div>
		            </div>
		            <span class="skill-score">${pct}%</span>
		        </div>
		    `;

	const makeSkillSection = (title, items, level) =>
		items.length
			? `
		            <div class="skill-section ${level}">
		                <h6>${title}</h6>
		                <div class="skill-grid">
		                    ${items.map(([s, pct]) => makeSkillItem(s, pct, level)).join("")}
		                </div>
		            </div>`
			: "";

	const skillComparisonHTML = `
		        <div class="detail-block">
		            <h5>Skill Comparison</h5>

		            <div class="skill-summary">
		                <p><strong>Matched:</strong> ${matchedCount}/${totalJDSkills} skills</p>
						<p><strong>Overall Skill match Percentage:</strong> ${matchedPercent}%</p>
		                <div class="progress-bar">
		                    <div class="fill" style="width: ${matchedPercent}%;"></div>
		                </div>
		            </div>

		            ${makeSkillSection("Strong Matches (≥70%)", highMatches, "high")}
		            ${makeSkillSection("Moderate Matches (30–69%)", mediumMatches, "medium")}
		            ${makeSkillSection("Weak Matches (<30%)", lowMatches, "low")}

		            ${unmatchedSkills.length
			? `
		                <div class="unmatched-section">
		                    <h6>Unmatched Skills</h6>
		                    <div class="unmatched-grid">
		                        ${unmatchedSkills.map((s) => `<span class="unmatched-skill">${s}</span>`).join("")}
		                    </div>
		                </div>`
			: ""
		}
		        </div>

		        <!-- Visualization Row -->
		        <div class="skill-visuals">
		            <div class="chart-section">
		                <h4 class="chart-title">Skill Match Breakdown</h4>
		                <canvas id="skillBreakdownChart"></canvas>
		            </div>
		            <div class="chart-section">
		                <h4 class="chart-title">Skill Match Distribution</h4>
		                <canvas id="skillDistributionChart"></canvas>
		            </div>
		        </div>

		        <!-- Insight Box -->
		        <div class="skill-insight" id="skillInsightBox"></div>
		    `;

	const detailsHTML = `
			  ${skillComparisonHTML}

			  <div class="detail-block action-phrase-analysis">
			    <h5>Action Phrase Analysis</h5>
				<div class="skill-summary">
										                <p><strong>Matched:</strong> ${matchedVerbCount}/${totalJDVerbs} skills</p>
														<p><strong>Overall verbs match percentage:</strong> ${matchedVerbPercent}%</p>
										            </div>
			    <div class="action-phrase-list"></div>
			  </div>

			  <div class="detail-block qualification-analysis">
			      <h5>Qualification Comparison</h5>
			      <div class="skill-summary" id="qual-summary" style="display:none;">
			          <p><strong>Matched:</strong> <span id="qualMatchedCount">0</span>/<span id="totalJDQuals">0</span> qualifications</p>
			          <p><strong>Overall qualification match:</strong> <span id="qualMatchPercent">0%</span></p>
			      </div>
			      <div class="qual-list-container">
			          <!-- JD qualifications will be populated here -->
			      </div>
			  </div>

			  <div class="buttons-analysis">
			  <button class="download-btn">Download Report</button>
			  <button class="go-to-top">Go to Top</button>
			  </div>
			`;

	content.innerHTML = detailsHTML;

	// --- Populate Action Phrase Analysis ---
	(function populateActionPhrases() {
		const container = document.querySelector(".action-phrase-list");
		if (!container) return;



		let html = "";

		Object.entries(matched).forEach(([jdPhrase, score]) => {
			const pct = Math.round(score * 100);
			const matchClass = pct >= 60 ? "high" : pct >= 30 ? "medium" : "low";
			const label = pct >= 60 ? "Strong Match" : pct >= 30 ? "Partial Match" : "Weak Match";

			html += `
			      <div class="action-phrase-item">
			        <span class="jd-phrase">${jdPhrase}</span>
			        <div class="match-bar">
			          <div class="match-fill ${matchClass}" style="width:${pct}%;"></div>
			        </div>
			        <span class="match-percent">${pct}%</span>
			        <span class="match-label ${matchClass}">${label}</span>
			      </div>
			    `;
		});

		unmatched.forEach(jdPhrase => {
			html += `
			      <div class="action-phrase-item">
			        <span class="jd-phrase">${jdPhrase}</span>
			        <div class="match-bar">
			          <div class="match-fill low" style="width:0%;"></div>
			        </div>
			        <span class="match-percent">0%</span>
			        <span class="match-label low">No Match</span>
			      </div>
			    `;
		});

		container.innerHTML = html;
	})();


	// --- Populate Qualification Comparison ---
	(function populateQualifications() {
		const container = document.querySelector(".qual-list-container");
		const jdQuals = data.jd_qualifications || [];
		const matchedQuals = data.matched_qualifications || [];
		const summaryDiv = document.getElementById("qual-summary");

		const totalJDQuals = jdQuals.length;
		const matchedCount = matchedQuals.length;
		const qualMatchPercent = totalJDQuals > 0 ? Math.round((matchedCount / totalJDQuals) * 100) : 0;

		// Update summary
		document.getElementById("qualMatchedCount").textContent = matchedCount;
		document.getElementById("totalJDQuals").textContent = totalJDQuals;
		document.getElementById("qualMatchPercent").textContent = qualMatchPercent + "%";

		if (totalJDQuals === 0) {
			// No JD quals, hide summary, show info message
			summaryDiv.style.display = "none";
			container.innerHTML = `<p style="color:#aaa; font-style: italic;">JD doesn't demand any qualifications</p>`;
			return;
		}

		summaryDiv.style.display = "block";
		container.innerHTML = jdQuals.map(q => {
			const isMatched = matchedQuals.includes(q);
			return `<div class="qual-item ${isMatched ? 'matched' : ''}">${q}</div>`;
		}).join('');
	})();


	// --- Visualization: Donut + Bar Charts ---
	requestAnimationFrame(() => {
		const skillBreakdownCanvas = document.getElementById("skillBreakdownChart");
		const skillDistributionCanvas = document.getElementById("skillDistributionChart");

		if (!skillBreakdownCanvas || !skillDistributionCanvas) return;

		const strong = highMatches.length;
		const moderate = mediumMatches.length;
		const weak = lowMatches.length;
		const unmatched = unmatchedSkills.length;

		const chartColors = ["#FFD37D", "#FFB366", "#FF924F", "#E76530"];
		const textColor = "#555";

		// --- Donut Chart ---
		const skillCtx = skillBreakdownCanvas.getContext("2d");
		if (window.skillBreakdownChart?.destroy) window.skillBreakdownChart.destroy();

		window.skillBreakdownChart = new Chart(skillCtx, {
			type: "doughnut",
			data: {
				labels: ["Strong (≥70%)", "Moderate (30–69%)", "Weak (<30%)", "Unmatched"],
				datasets: [{
					data: [strong, moderate, weak, unmatched],
					backgroundColor: chartColors,
					borderWidth: 1,
					hoverOffset: 4
				}]
			},
			options: {
				responsive: true,
				cutout: "45%",
				plugins: {
					legend: {
						position: "bottom",
						labels: { color: textColor, font: { size: 12 }, boxWidth: 14 }
					}
				}
			}
		});

		// --- Bar Chart ---
		const distCtx = skillDistributionCanvas.getContext("2d");
		if (window.skillDistributionChart?.destroy) window.skillDistributionChart.destroy();

		window.skillDistributionChart = new Chart(distCtx, {
			type: "bar",
			data: {
				labels: ["Strong", "Moderate", "Weak", "Unmatched"],
				datasets: [{
					label: "Number of Skills",
					data: [strong, moderate, weak, unmatched],
					backgroundColor: chartColors,
					borderWidth: 1,
					borderRadius: 8
				}]
			},
			options: {
				responsive: true,
				scales: {
					x: {
						ticks: { color: textColor, font: { size: 12, weight: "500" } },
						grid: { display: false }
					},
					y: {
						ticks: { color: textColor, font: { size: 12 } },
						grid: { color: "rgba(85,85,85,0.15)" },
						beginAtZero: true
					}
				},
				plugins: {
					legend: { display: false },
					tooltip: {
						enabled: true,
						backgroundColor: "#333",
						titleColor: "#fff",
						bodyColor: "#fff",
						titleFont: { size: 9, weight: "bold" },
						bodyFont: { size: 9 },
						padding: 8,
						displayColors: true,
						cornerRadius: 6,
						boxPadding: 4
					}
				}
			}
		});

	});

	// --- Insight Box ---
	generateSkillInsight(matchedPercent, unmatchedSkills.length);

	document
		.querySelector(".download-btn")
		.addEventListener("click", downloadReportAsPDF);

	document.querySelector('.go-to-top').addEventListener('click', () => window.scrollTo({ top: 50, behavior: 'smooth' }));

}

// --- Insight generator (unchanged)
function generateSkillInsight(matchedPercent, unmatchedCount) {
	let insight = "";

	if (matchedPercent >= 80) {
		insight = "Excellent skill alignment! You closely match the job’s technical requirements.";
	} else if (matchedPercent >= 60) {
		insight = "Good overlap — consider improving or highlighting key skills from the JD to boost your fit.";
	} else {
		insight = "Significant gaps detected — focus on the missing skills to strengthen your profile.";
	}

	if (unmatchedCount > 0) {
		insight += ` Missing ${unmatchedCount} skills from the JD.`;
	}

	document.getElementById("skillInsightBox").textContent = insight;
}

function updateInsightsSection(data) {
	const skillMatchPct = data.skill_match_pct || 0;
	const verbMatchPct = data.verb_match_pct || 0;
	const titleMatchPct = data.title_match_pct || 0;
	const qualMatchPct = data.qual_match_pct || 0;
	const overallFit = data.overall_fit || 0;

	// Get top 5 skills with 100% match
	let topSkills = [];
	if (data.matched_skills && typeof data.matched_skills === "object") {
		topSkills = Object.entries(data.matched_skills)
			.filter(([skill, pct]) => pct >= 1.0) // 100% match
			.map(([skill]) => skill)
			.slice(0, 5);
	}

	// Build a natural sentence like "Java, SQL, React"
	const skillText = topSkills.length
		? `You already demonstrate key skills: <strong>${topSkills.join(", ")}</strong>.`
		: "You demonstrate several relevant skills.";

	const insightsData = [];

	// ---- Skill Insights ----
	if (skillMatchPct >= 0.7) {
		insightsData.push({
			title: "Strong Skill Match",
			text: `${skillText} Excellent alignment with the job’s requirements!`,
			score: skillMatchPct
		});
	} else if (skillMatchPct >= 0.5) {
		insightsData.push({
			title: "Partial Skill Match",
			text: "You match some key skills. Consider adding missing tools or frameworks mentioned in the JD.",
			score: skillMatchPct
		});
	} else {
		insightsData.push({
			title: "Weak Skill Match",
			text: "Add missing or related technical skills to improve alignment with the JD.",
			score: skillMatchPct
		});
	}

	// ---- Qualification Insights ----
	if (qualMatchPct === 0) {
		insightsData.push({
			title: "No Qualification Match",
			text: "Your resume lacks qualifications required by this job description.",
			score: qualMatchPct
		});
	} else if (qualMatchPct >= 0.7) {
		insightsData.push({
			title: "Strong Qualification Alignment",
			text: "Your educational background and certifications match the job’s expectations.",
			score: qualMatchPct
		});
	} else {
		insightsData.push({
			title: "Partial Qualification Match",
			text: "Consider adding certifications or training mentioned in the JD to strengthen your profile.",
			score: qualMatchPct
		});
	}

	// ---- Action Verb Insights ----
	if (verbMatchPct >= 0.5) {
		insightsData.push({
			title: "Strong Action Verbs",
			text: "Your resume uses impactful action verbs that reflect initiative and ownership.",
			score: 0.7
		});
	} else {
		insightsData.push({
			title: "Weak Action Verbs",
			text: "Use stronger phrases like 'developed', 'implemented', or 'optimized' to better express your impact.",
			score: verbMatchPct
		});
	}

	// ---- Title Match Insights ----
	if (titleMatchPct >= 0.75) {
		insightsData.push({
			title: "Title Alignment",
			text: "Your resume title closely matches the job title — strong role fit!",
			score: titleMatchPct
		});
	} else {
		insightsData.push({
			title: "Title Mismatch",
			text: "Your resume title differs from the job title. Adjusting it can improve relevance.",
			score: titleMatchPct
		});
	}

	// ---- Overall Fit ----
	if (overallFit >= 0.70) {
		insightsData.push({
			title: "Excellent Overall Fit",
			text: "This resume is a great fit for the job — minimal improvements needed!",
			score: overallFit
		});
	} else if (overallFit >= 0.5) {
		insightsData.push({
			title: "Moderate Fit",
			text: "There’s decent alignment, but focus on improving key skills and qualifications.",
			score: overallFit
		});
	} else {
		insightsData.push({
			title: "Low Overall Fit",
			text: "Significant differences exist between the JD and your resume. Consider tailoring it more closely.",
			score: overallFit
		});
	}

	// ---- Color/Style Mapping ----
	function getClasses(score) {
		if (score >= 0.7) return { glass: 'glass-green', text: 'text-green', indicator: 'indicator-green' };
		if (score >= 0.5) return { glass: 'glass-yellow', text: 'text-yellow', indicator: 'indicator-yellow' };
		if (score >= 0.3) return { glass: 'glass-orange', text: 'text-orange', indicator: 'indicator-orange' };
		return { glass: 'glass-red', text: 'text-red', indicator: 'indicator-red' };
	}

	// ---- Render Section ----
	const container = document.getElementById("insightsContainer");
	container.innerHTML = "";

	insightsData.forEach(item => {
		const { glass, text, indicator } = getClasses(item.score);
		const div = document.createElement("div");
		div.className = `insight-card ${glass}`;
		div.innerHTML = `
					<div class="indicator ${indicator}"></div>
					<div class="content">
						<h4 class="${text}">${item.title}</h4>
						<p>${item.text}</p>
					</div>
				`;
		container.appendChild(div);
	});
}


async function downloadReportAsPDF() {
	const button = document.querySelector(".download-btn");
	const buttonTop = document.querySelector(".go-to-top");
	const content = document.getElementById("detailsContent");
	const jdTitle = document.getElementById("hero-jd-title");
	const jdCompany = document.getElementById("hero-jd-company");
	const resumeTitle = document.getElementById("hero-resume-title");

	const jdTitleText = jdTitle ? jdTitle.textContent.trim() : "";
	const jdCompanyText = jdCompany ? jdCompany.textContent.trim() : "";
	const resumeTitleText = resumeTitle ? resumeTitle.textContent.trim() : "";

	if (!content) return;

	const originalText = button ? button.textContent : "";

	// --- Functions to safely hide/show buttons ---
	const hideButtons = () => {
		if (button) { button.style.visibility = "hidden"; button.disabled = true; button.textContent = "Generating PDF..."; }
		if (buttonTop) { buttonTop.style.visibility = "hidden"; buttonTop.disabled = true; }
	};

	const showButtons = () => {
		if (button) { button.style.visibility = "visible"; button.disabled = false; button.textContent = originalText; }
		if (buttonTop) { buttonTop.style.visibility = "visible"; buttonTop.disabled = false; }
	};

	hideButtons();

	try {
		const { jsPDF } = window.jspdf;
		const pdf = new jsPDF({ orientation: "portrait", unit: "px", format: "a4" });
		const pageWidth = pdf.internal.pageSize.getWidth();
		const pageHeight = pdf.internal.pageSize.getHeight();

		const addHeader = (pageNum, firstPage = false) => {
			const accentColor = [255, 127, 80];
			const textDark = [51, 51, 51];
			const textLight = [120, 120, 120];

			if (firstPage) {
				pdf.setFillColor(255, 244, 229);
				pdf.rect(0, 0, pageWidth, 60, "F");

				pdf.setFont("helvetica", "bold");
				pdf.setFontSize(14);
				pdf.setTextColor(...accentColor);
				pdf.text("Resume Analyzer - Detailed Report", 20, 25);

				pdf.setFont("helvetica", "normal");
				pdf.setFontSize(10);
				pdf.setTextColor(...textDark);
				pdf.text(`${jdTitleText || "Job Title"} @ ${jdCompanyText || "Company"}`, 20, 40);
				pdf.text(`Resume: ${resumeTitleText || "Candidate"}`, 20, 52);

				pdf.setDrawColor(...accentColor);
				pdf.setLineWidth(0.8);
				pdf.line(20, 60, pageWidth - 20, 60);
			}

			// Footer
			pdf.setFont("helvetica", "italic");
			pdf.setFontSize(8);
			pdf.setTextColor(...textLight);
			pdf.text(`Generated on: ${new Date().toLocaleString()}`, 20, pageHeight - 20);
			pdf.text(`Page ${pageNum}`, pageWidth - 60, pageHeight - 20);
		};

		// Convert content to canvas
		const canvas = await html2canvas(content, { scale: 2, useCORS: true, backgroundColor: "#ffffff" });
		const imgWidth = pageWidth - 40;
		const scale = imgWidth / canvas.width;

		let pageNumber = 1;
		let yOffset = 0;
		const pageHeaderHeight = 60;
		const pageFooterHeight = 30;
		const pageContentHeight = pageHeight - pageHeaderHeight - pageFooterHeight;

		while (yOffset < canvas.height) {
			const pageCanvas = document.createElement("canvas");
			const pageCtx = pageCanvas.getContext("2d");

			const sliceHeight = Math.min(canvas.height - yOffset, pageContentHeight / scale);
			pageCanvas.width = canvas.width;
			pageCanvas.height = sliceHeight;

			pageCtx.drawImage(canvas, 0, yOffset, canvas.width, sliceHeight, 0, 0, canvas.width, sliceHeight);

			const imgData = pageCanvas.toDataURL("image/png");
			addHeader(pageNumber, pageNumber === 1);

			const yPosition = pageNumber === 1 ? pageHeaderHeight : 20;
			pdf.addImage(imgData, "PNG", 20, yPosition, imgWidth, sliceHeight * scale);

			yOffset += sliceHeight;
			if (yOffset < canvas.height) pdf.addPage();
			pageNumber++;
		}

		const reportName = `Analysis_report_` + resumeTitleText + `.pdf`;
		pdf.save(reportName);

		await logActivity("DOWNLOAD", "Report", reportName, "Report Downloaded Successfully !", "SUCCESS");

	} catch (err) {
		await logActivity("DOWNLOAD", "Report", reportName, "Report Download Failed !", "FAILURE");
		throw err;
	} finally {
		showButtons();
	}
}


document.addEventListener("DOMContentLoaded", () => {

	const token = sessionStorage.getItem("token");
	if (!token) return window.location.href = "/login";

	const params = new URLSearchParams(window.location.search);
	const analysisId = params.get("id");


	const toggleBtn = document.getElementById("toggleDetailsBtn");
	const content = document.getElementById("detailsContent");
	const detailsHeader = document.getElementById("detailsHeader");
	const insights = document.getElementById("insightsContainer");
	const toggleInsightsBtn = document.getElementById("toggleInsightsBtn");
	const cardsContainer = document.querySelector(".analysis-cards-container");
	const insightsHeader = document.getElementById("insightsHeader");

	function toggleDetails() {
		if (content.classList.contains("expanded")) {
			// Collapse
			content.style.maxHeight = "0px";
			content.classList.remove("expanded");
			toggleBtn.textContent = "View Details ▼";

			if (insights.classList.contains("expanded")) {
				cardsContainer.style.maxHeight = "580px";
			} else {
				cardsContainer.style.maxHeight = "330px";
			}

		} else {
			// Expand
			content.style.maxHeight = content.scrollHeight + "px"; // dynamically
			content.classList.add("expanded");
			toggleBtn.textContent = "Hide Details ▲";
			if (insights.classList.contains("expanded")) {
				cardsContainer.style.maxHeight = content.scrollHeight + 680 + "px";
			} else {
				cardsContainer.style.maxHeight = content.scrollHeight + 330 + "px";
			}

		}
	}

	function toggleInsights() {
		if (insights.classList.contains("expanded")) {
			// Collapse
			insights.style.maxHeight = "0px";
			insights.classList.remove("expanded");
			toggleInsightsBtn.textContent = "View Details ▼";
			cardsContainer.style.maxHeight = "330px";


		} else {
			// Expand
			insights.style.maxHeight = "500px"; // dynamically
			insights.classList.add("expanded");
			cardsContainer.style.maxHeight = "580px";
			toggleInsightsBtn.textContent = "Hide Details ▲";
		}
	}

	toggleBtn.addEventListener("click", toggleDetails);
	detailsHeader.addEventListener("click", toggleDetails);
	insightsHeader.addEventListener("click", toggleInsights);

	loadAnalysisCards("analysis-cards").then(() => {
		let targetId = analysisId; // from URL param if present

		if (!targetId) {
			// fallback: show most recent analysis
			targetId = document.querySelector(".analysis-card:first-child")?.dataset.id;
		}

		if (targetId) {
			fetchAnalysisDataById(targetId).then((data) => {
				updateHeroCard(data);
				updateInsightsSection(data);
				updateDetailsSection(data);
			});
		}

		// Attach click handlers
		attachCardClickHandlers();
	});

	const headers = {
		"Authorization": `Bearer ${token}`,
		"Content-Type": "application/json"
	};
});