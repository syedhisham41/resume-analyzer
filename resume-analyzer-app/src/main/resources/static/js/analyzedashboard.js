// js/analyzedashboard.js

import {
	showSpinner, hideSpinner, showAlert, showToast, runAnalysis,
	openDeleteModal, closeDeleteModal,
	AnalyzeSelectionModal, showAnalysisCompleted
} from './common.js';

document.addEventListener("DOMContentLoaded", () => {
	const token = sessionStorage.getItem("token");
	if (!token) return window.location.href = "/login";

	const headers = {
		"Authorization": `Bearer ${token}`,
		"Content-Type": "application/json"
	};

	// ------------------- INIT PAGE -------------------
	async function initAnalyzeDashboard() {
		showSpinner();
		try {
			await loadAnalyzePageData();
		} catch (err) {
			console.error("Error initializing analyze dashboard:", err);
			await showAlert("Failed to load dashboard data.");
		} finally {
			hideSpinner();
		}

		setupModals();
	}

	// ------------------- LOAD PAGE DATA -------------------
	async function loadAnalyzePageData() {
		try {
			const response = await fetch(`/api/analyzepage/data`, { headers });
			if (!response.ok) throw new Error("Failed to load data");

			const data = await response.json();
			populateViewPage(data);

		} catch (error) {
			console.error("Error loading analyze page data:", error);
			showAlert("Failed to load dashboard data.");
		}
	}

	// ------------------- POPULATE PAGE -------------------
	function populateViewPage(data) {
		// 1️⃣ Header stats
		const totalCount = document.getElementById('totalCount');
		const lastUploadedDate = document.getElementById('lastUploadedDate');
		if (totalCount) totalCount.textContent = data.totalAnalyzeCount || 0;
		if (lastUploadedDate) lastUploadedDate.textContent = data.lastAnalyzeDate || '—';

		// 2️⃣ Recent 5 analyses
		const recentContainer = document.getElementById('recentAnalyses');
		if (recentContainer) {
			recentContainer.innerHTML = '';

			(data.recent5Analyzes || []).forEach(item => {
				const div = document.createElement('div');
				div.className = 'mini-a4';
				div.style.position = "relative";
				div.setAttribute('data-analyze-id', item.analyzeId);

				div.innerHTML = `
		      <div class="corner-fold"></div>
		      <div class="mini-content">
		        <h5 class="jd-title">${item.jdTitle || 'Untitled JD'}</h5>
		        <p class="meta-mini company"><strong>${item.jdCompany || 'Unknown Company'}</strong></p>
		        <p class="meta-mini resume"><span style="color:#FF6F42;">${item.resumeTitle || 'Resume'}</span></p>
		        <p class="meta-mini date">${item.createdAt}</p>
		      </div>
		    `;

				recentContainer.appendChild(div);
			});
		}

		// 3️⃣ All analyzes
		const allContainer = document.getElementById('allAnalyzes');
		console.log('allContainer', allContainer);
		if (allContainer) {
			allContainer.innerHTML = '';
			(data.allAnalyzes || []).forEach(item => {
				const itemDiv = document.createElement('div');
				itemDiv.className = 'analyze-item';
				itemDiv.id = item.analyzeId;

				const jdTitleShort = item.jdTitle.length > 50 ? item.jdTitle.slice(0, 47) + "..." : item.jdTitle;
				const resumeTitleShort = item.resumeTitle.length > 50 ? item.resumeTitle.slice(0, 47) + "..." : item.resumeTitle;

				itemDiv.innerHTML = `
                    <div class="type-item-header">
                        <div class="header-left">
                            <h4>${jdTitleShort}</h4>
                            <p class="company">${item.jdCompany || '—'}</p>
                            <p class="resume-title">${resumeTitleShort}</p>
                        </div>
                        <div class="header-right">
                            <span class="analyze-date">${new Date(item.createdAt).toDateString()}</span>
                        </div>
                    </div>

                    <div class="analyze-item-content">
                        <div class="card-metrics">
                            <div class="metric"><span>Overall Fit: ${(item.overall_fit * 100).toFixed(1)}%</span> <progress value="${item.overall_fit * 100 || 0}" max="100"></progress></div>
                            <div class="metric"><span>Skill Match: ${(item.skill_match_pct * 100).toFixed(1)}%</span> <progress value="${item.skill_match_pct * 100 || 0}" max="100"></progress></div>
                            <div class="metric"><span>Verb Match: ${(item.verb_match_pct * 100).toFixed(1)}%</span> <progress value="${item.verb_match_pct * 100 || 0}" max="100"></progress></div>
                            <div class="metric"><span>Title Match: ${(item.title_match_pct * 100).toFixed(1)}%</span> <progress value="${item.title_match_pct * 100 || 0}" max="100"></progress></div>
                            <div class="metric"><span>Qualification Match: ${(item.qual_match_pct * 100).toFixed(1)}%</span> <progress value="${item.qual_match_pct * 100 || 0}" max="100"></progress></div>
                        </div>

                        <div class="card-details hidden">
							<div class="matched-skills">
							                                <p>Matched Skills:</p>
							                                ${item.matched_skills
						? Object.entries(item.matched_skills).map(([skill, score]) =>
							`<div class="verb-match-row"><span class="badge">${skill}</span> <span class="verb-score">${Math.round(score * 100)}%</span></div>`
						).join('')
						: '<div>—</div>'
					}
							                            </div>
                            <p>Unmatched Skills: ${item.unmatched_skills?.map(s => `<span class="badge">${s}</span>`).join(' ') || '—'}</p>
                            <div class="matched-verbs">
                                <p>Matched Verbs:</p>
                                ${item.matched_verb_phrases
						? Object.entries(item.matched_verb_phrases).map(([verb, score]) =>
							`<div class="verb-match-row"><span class="badge">${verb}</span> <span class="verb-score">${Math.round(score * 100)}%</span></div>`
						).join('')
						: '<div>—</div>'
					}
                            </div>
                            <p>Unmatched Verbs: ${item.unmatched_verb_phrases?.map(s => `<span class="badge">${s}</span>`).join(' ') || '—'}</p>
                        </div>

                        <div class="type-actions">
                            <button class="delete-btn">Delete</button>
                            <button class="toggle-details-btn">View Details</button>
							<button class="view-report-btn" data-id=${item.analyzeId}>View Complete Report</button>
							<button class="to-top-btn">Go to Top</button>
                        </div>
                    </div>
                `;
				allContainer.appendChild(itemDiv);
			});


			// Attach behaviors
			setupItemToggle('.analyze-item', '.type-item-header');
			setupOpenFromRecent('.mini-a4', 'data-analyze-id', '.analyze-item');
			setupDeleteHandlers();
			setupDetailsSlide();
			setupViewReportHandler();

			document.querySelectorAll('.to-top-btn').forEach(btn => {
				btn.addEventListener('click', () => {
					window.scrollTo({ top: 0, behavior: 'smooth' });
				});
			});
		}

		setupSearch();
	}

	// ------------------- DELETE HANDLERS -------------------
	let deleteTarget = null;
	function setupDeleteHandlers() {
		document.querySelectorAll('.delete-btn').forEach(btn => {
			if (btn.dataset.deleteAttached) return;
			btn.dataset.deleteAttached = '1';

			btn.addEventListener('click', (event) => {
				event.stopPropagation();
				deleteTarget = { btn };
				openDeleteModal();
			});
		});
	}

	const confirmBtn = document.getElementById('confirmDeleteBtn');
	const cancelBtn = document.getElementById('cancelDeleteBtn');

	if (confirmBtn && cancelBtn) {
		confirmBtn.addEventListener('click', async () => {
			if (!deleteTarget) return;
			const { btn } = deleteTarget;
			const parentItem = btn.closest('.analyze-item');
			const itemId = parentItem.id;

			try {
				showSpinner();
				const response = await fetch(`/api/analyze/delete?analyzeId=${itemId}`, { method: 'DELETE', headers });
				if (!response.ok) throw new Error('Failed to delete');

				parentItem.remove();
				showToast("Deleted successfully!", "success");
				await loadAnalyzePageData();
			} catch (error) {
				console.error("Error deleting item:", error);
				showToast("Failed to delete item.", "error");
			} finally {
				hideSpinner();
				closeDeleteModal();
				deleteTarget = null;
			}
		});

		cancelBtn.addEventListener('click', () => {
			closeDeleteModal();
			deleteTarget = null;
		});
	}

	// ------------------- VIEW REPORT HANDLER -------------------//

	function setupViewReportHandler() {
		document.querySelectorAll(".view-report-btn").forEach(btn => {
			btn.addEventListener("click", (e) => {
				const id = e.target.dataset.id;
				window.location.href = `/reports?id=${id}`;
			});
		});
	}


	// ------------------- TOGGLE DETAILS -------------------
	function setupItemToggle(itemSelector, headerSelector) {
		document.querySelectorAll(headerSelector).forEach(header => {
			header.addEventListener('click', () => {
				const parent = header.parentElement;
				parent.classList.toggle('expanded');
				parent.scrollIntoView({ behavior: "smooth", block: "start" });
			});
		});
	}

	function setupOpenFromRecent(paperSelector, dataAttr, itemClass) {
		document.querySelectorAll(paperSelector).forEach(paper => {
			paper.addEventListener('click', () => {
				const targetId = paper.getAttribute(dataAttr);
				const target = document.getElementById(targetId);
				if (target) {
					target.classList.add('expanded');
					target.scrollIntoView({ behavior: "smooth", block: "start" });
				}
			});
		});
	}


	function setupDetailsSlide() {
		document.querySelectorAll('.toggle-details-btn').forEach(btn => {
			console.log('btn.dataset.slideAttached', btn.dataset.slideAttached);
			if (btn.dataset.slideAttached) return;
			btn.dataset.slideAttached = '1';

			btn.addEventListener('click', () => {
				const parentContent = btn.closest('.analyze-item-content') || btn.closest('.analyze-item');
				if (!parentContent) return;
				const details = parentContent.querySelector('.card-details');
				if (!details) return;

				// Remove 'hidden' if present
				details.classList.remove('hidden');

				if (details.classList.contains('open')) {
					// Close
					details.style.maxHeight = details.scrollHeight + 'px';
					requestAnimationFrame(() => {
						details.style.maxHeight = '0px';
						details.style.opacity = '0';
					});
					details.classList.remove('open');
					btn.textContent = 'View Details';
				} else {
					// Open
					details.classList.add('open');
					details.style.maxHeight = details.scrollHeight + 'px';
					details.style.opacity = '1';
					btn.textContent = 'Hide Details';

					// Reset maxHeight to none after transition
					const onEnd = () => {
						if (details.classList.contains('open')) details.style.maxHeight = 'none';
						details.removeEventListener('transitionend', onEnd);
					};
					details.addEventListener('transitionend', onEnd);
				}
			});
		});
	}

	// ------------------- SEARCH SETUP -------------------
	function setupSearch() {
		const inputEl = document.getElementById("searchInput");
		const startDateEl = document.getElementById("startDate");
		const endDateEl = document.getElementById("endDate");
		const toggleBtns = document.querySelectorAll(".toggle-btn");
		const container = document.querySelector(".analyze-list"); // parent of all analyze items

		if (!inputEl || !toggleBtns.length) return;

		let searchMode = "title"; // default

		// --- Toggle button behavior ---
		toggleBtns.forEach(btn => {
			btn.addEventListener("click", () => {
				toggleBtns.forEach(b => b.classList.remove("active"));
				btn.classList.add("active");

				searchMode = btn.dataset.mode;

				// Show/hide relevant input fields
				if (searchMode === "date") {
					inputEl.style.display = "none";
					startDateEl.style.display = "inline-block";
					endDateEl.style.display = "inline-block";
				} else {
					inputEl.style.display = "inline-block";
					startDateEl.style.display = "none";
					endDateEl.style.display = "none";
					inputEl.placeholder = `Search by ${searchMode}...`;
				}

				// Clear previous inputs
				inputEl.value = "";
				startDateEl.value = "";
				endDateEl.value = "";

				filterItems();
			});
		});

		// --- Filter function ---
		const filterItems = () => {
			const term = inputEl.value.toLowerCase();
			const start = startDateEl.value ? new Date(startDateEl.value) : null;
			const end = endDateEl.value ? new Date(endDateEl.value) : null;

			if (start) start.setHours(0, 0, 0, 0);
			if (end) end.setHours(23, 59, 59, 999);

			let anyVisible = false;
			let activeAnimations = 0;

			const items = document.querySelectorAll(".analyze-item");

			items.forEach(item => {
				const h4El = item.querySelector("h4");
				const dateSpan = item.querySelector(".analyze-date");
				const resumeSpan = item.querySelector(".resume-title");
				const companySpan = item.querySelector(".company");

				const itemTitle = h4El.textContent;
				const itemDate = dateSpan ? new Date(dateSpan.dataset.date || dateSpan.textContent) : null;
				const itemResumeTitle = resumeSpan.textContent;
				const itemCompany = companySpan.textContent;

				let match = false;
				if (searchMode === "title") {
					match = !term || itemTitle.toLowerCase().includes(term);
				} else if (searchMode === "date") {
					if (!start && !end) match = true;
					else if (itemDate) {
						if (start && end) match = itemDate >= start && itemDate <= end;
						else if (start) match = itemDate >= start;
						else if (end) match = itemDate <= end;
					}
				} else if (searchMode === "resume") {
					match = !term || itemResumeTitle.toLowerCase().includes(term);
				} else if (searchMode === "company") {
					match = !term || itemCompany.toLowerCase().includes(term);
				}

				if (match) {
					anyVisible = true;

					item.style.display = "block";
					item.classList.remove("fade-out");
					requestAnimationFrame(() => item.classList.add("fade-in"));

					// highlight logic

					if (searchMode !== "date" && term) {
						if (searchMode === "title")
							h4El.innerHTML = itemTitle.replace(new RegExp(`(${term})`, "gi"), "<mark>$1</mark>");
						if (searchMode === "company" && companySpan)
							companySpan.innerHTML = itemCompany.replace(new RegExp(`(${term})`, "gi"), "<mark>$1</mark>");
						if (searchMode === "resume" && term)
							resumeSpan.innerHTML = itemResumeTitle.replace(new RegExp(`(${term})`, "gi"), "<mark>$1</mark>");
					} else {
						h4El.innerHTML = itemTitle;
						if (companySpan) companySpan.innerHTML = itemCompany;
						if (resumeSpan) resumeSpan.innerHTML = itemResumeTitle;
					}
				} else {
					if (!item.classList.contains("fade-out")) {
						item.classList.remove("fade-in");
						item.classList.add("fade-out");

						activeAnimations++;
						item.addEventListener("transitionend", () => {
							item.style.display = "none";
							activeAnimations--;
							if (activeAnimations === 0) updateEmptyState();
						}, { once: true });
					}
					h4El.innerHTML = itemTitle;
				}
			});

			// staggered reveal
			let delay = 0;
			items.forEach(item => {
				if (item.style.display === "block") {
					item.style.transitionDelay = `${delay}ms`;
					delay += 60;
				} else {
					item.style.transitionDelay = "0ms";
				}
			});

			const updateEmptyState = () => {
				let emptyState = document.getElementById("emptyStateMsg");
				if (!emptyState) {
					emptyState = document.createElement("div");
					emptyState.id = "emptyStateMsg";
					emptyState.textContent = "No items found.";
					container.appendChild(emptyState);
				}
				const hasVisible = [...items].some(it => it.style.display === "block");
				emptyState.classList.toggle("show", !hasVisible);
				//emptyState.classList.toggle("show", !anyVisible);
			};

			// if no fades in progress, update immediately
			if (activeAnimations === 0) updateEmptyState();
		};

		// --- Debounce helper ---
		const debounce = (fn, delay) => {
			let timeout;
			return (...args) => {
				clearTimeout(timeout);
				timeout = setTimeout(() => fn(...args), delay);
			};
		};

		const debouncedFilter = debounce(filterItems, 200);

		inputEl.addEventListener("input", debouncedFilter);
		startDateEl.addEventListener("change", debouncedFilter);
		endDateEl.addEventListener("change", debouncedFilter);
	}



	// ------------------- MODALS -------------------
	function setupModals() {
		// Initialize modal DOM references first
		AnalyzeSelectionModal.init();



		const openBtn = document.getElementById('openModalBtn');
		if (!openBtn) {
			console.error('Start New Analysis button not found!');
			return;
		}

		openBtn.addEventListener('click', () => {
			AnalyzeSelectionModal.openModal();
		});

		console.log('openBtn:', openBtn);
		console.log('modal:', document.getElementById('analyzeSelectionModal'));
		console.log('jdStep:', document.getElementById('jdStep'));
		console.log('resumeStep:', document.getElementById('resumeStep'));
		console.log('finalActionsRow:', document.getElementById('finalActionsRow'));


	}

	// ------------------- RUN INIT -------------------
	initAnalyzeDashboard();

	window.loadAnalyzePageData = loadAnalyzePageData;
});


