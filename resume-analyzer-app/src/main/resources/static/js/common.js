// Toast
export function showToast(message, type = 'success', duration = 3000) {
	const container = document.getElementById('toastContainer');
	if (!container) return;

	const toast = document.createElement('div');
	toast.className = `toast ${type}`;
	toast.textContent = message;
	container.appendChild(toast);

	requestAnimationFrame(() => toast.classList.add('show'));
	setTimeout(() => {
		toast.classList.remove('show');
		toast.addEventListener('transitionend', () => toast.remove());
	}, duration);
}

// Spinner
export function showSpinner() {
	const spinner = document.getElementById('spinner-overlay');
	if (!spinner) return;
	spinner.style.display = 'flex';
}

export function hideSpinner() {
	const spinner = document.getElementById('spinner-overlay');
	if (!spinner) return;
	spinner.style.display = 'none';
}

//Alert// Alert (glass, blocking) — replaces the broken version
export function showAlert(message) {
	return new Promise(resolve => {
		const modal = document.getElementById("alertModal");
		if (!modal) {
			// fallback to regular alert if modal isn't in DOM
			alert(message);
			resolve();
			return;
		}

		const glassBox = modal.querySelector('.modal-glass-box');
		const msgEl = document.getElementById("alertMessage");
		const okBtn = document.getElementById("alertOkBtn");

		if (!glassBox || !msgEl || !okBtn) {
			alert(message);
			resolve();
			return;
		}

		// set message
		msgEl.textContent = message;

		// show overlay, then fade-in inner glass box (same pattern used for deleteModal)
		modal.classList.add('show');            // shows .modal-overlay via CSS
		// ensure internal content transitions (start hidden)
		glassBox.classList.add('hidden');
		requestAnimationFrame(() => glassBox.classList.remove('hidden'));

		// close handler
		function close() {
			// start fade-out animation
			glassBox.classList.add('hidden');

			// after transition hide overlay AND resolve
			setTimeout(() => {
				modal.classList.remove('show');
				resolve(); // ✅ resolve only after modal fully gone
			}, 300); // must match your CSS transition duration

			okBtn.removeEventListener('click', onOk);
			document.removeEventListener('keydown', onKey);
		}


		function onOk() { close(); }

		// optional: allow Esc key to close the modal (keeps it friendly)
		function onKey(e) {
			if (e.key === 'Escape') close();
		}

		okBtn.addEventListener('click', onOk);
		document.addEventListener('keydown', onKey);
	});
}

//Delete Modal Functions
export function openDeleteModal() {
	const modal = document.getElementById('deleteModal');
	const glassBox = modal.querySelector('.modal-glass-box');

	modal.classList.add('show');
	glassBox.classList.add('hidden');
	requestAnimationFrame(() => glassBox.classList.remove('hidden'));
}

export function closeDeleteModal() {
	const modal = document.getElementById('deleteModal');
	const glassBox = modal.querySelector('.modal-glass-box');

	glassBox.classList.add('hidden');
	setTimeout(() => modal.classList.remove('show'), 300);
}

// Analyze Function
export async function runAnalysis(jdId, resumeId, headers) {
	console.log("Running analysis for:", jdId, resumeId);
	const url = `http://localhost:8080/api/analyzepage/analyze?jdId=${jdId}&resumeId=${resumeId}`;

	const response = await fetch(url, {
		method: "POST",
		headers: headers,
	});

	if (!response.ok) {
		throw new Error(`Server error: ${response.status}`);
	}

	const result = await response.json();
	console.log("Analysis result:", result);
	return result;
}

const token = sessionStorage.getItem("token");
const headers = {
	"Authorization": `Bearer ${token}`,
	"Content-Type": "application/json"
};


// ------------------- ANALYZE SELECTION MODAL -------------------

export const AnalyzeSelectionModal = (function() {
	let modal, closeBtn, jdStep, resumeStep, finalActionsRow, runBtn, backBtn;
	let recentJdsRow, recentResumesRow;

	let preselectedJDId = null;
	let preselectedResumeId = null;
	let jdSearchTimer = null;
	let resumeSearchTimer = null;

	function init() {
		console.log("Initializing AnalyzeSelectionModal...");
		modal = document.getElementById('analyzeSelectionModal');
		if (!modal) {
			console.log('modal is empty');
			return;
		}

		closeBtn = document.getElementById('closeSelectionModalBtn');
		jdStep = document.getElementById('jdStep');
		resumeStep = document.getElementById('resumeStep');
		finalActionsRow = document.getElementById('finalActionsRow');
		runBtn = document.getElementById('runAnalysisBtn');
		backBtn = document.getElementById('backBtn');

		recentJdsRow = document.getElementById('recentJdsRow');
		recentResumesRow = document.getElementById('recentResumesRow');

		closeBtn?.addEventListener('click', closeModal);
		backBtn?.addEventListener('click', backToJDStep);
		runBtn?.addEventListener('click', handleRunAnalysis);

		const jdSearchInput = document.getElementById('jdSearchInput');
		const resumeSearchInput = document.getElementById('resumeSearchInput');

		// 🔍 Debounced search for JD
		jdSearchInput?.addEventListener('input', () => {
			clearTimeout(jdSearchTimer);
			jdSearchTimer = setTimeout(() => {
				const query = jdSearchInput.value.trim();
				searchAndPopulate('jd', query, recentJdsRow);
			}, 400);
		});

		// 🔍 Debounced search for Resume
		resumeSearchInput?.addEventListener('input', () => {
			clearTimeout(resumeSearchTimer);
			resumeSearchTimer = setTimeout(() => {
				const query = resumeSearchInput.value.trim();
				searchAndPopulate('resume', query, recentResumesRow);
			}, 400);
		});

		// Legacy button triggers (still supported)
		document.getElementById('jdSearchBtn')?.addEventListener('click', searchJDs);
		document.getElementById('resumeSearchBtn')?.addEventListener('click', searchResumes);
	}

	async function openModal(options = {}) {
		if (!modal) return;

		preselectedJDId = options.preselectJDId || null;
		preselectedResumeId = options.preselectResumeId || null;

		modal.classList.remove('hidden');
		showSpinner();

		try {
			await fetchAndPopulate('jd', recentJdsRow);
			await fetchAndPopulate('resume', recentResumesRow);

			recentJdsRow.querySelectorAll('.mini-a4').forEach(c => c.classList.remove('selected'));
			recentResumesRow.querySelectorAll('.mini-a4').forEach(c => c.classList.remove('selected'));

			if (preselectedJDId) {
				const jdCard = recentJdsRow.querySelector(`[data-id="${preselectedJDId}"]`);
				if (jdCard) jdCard.classList.add('selected');

				backBtn?.addEventListener('click', closeModal);
				jdStep.classList.add('hidden');
				resumeStep.classList.remove('hidden');
				finalActionsRow.classList.remove('hidden');
				runBtn.disabled = true;

				recentResumesRow.querySelectorAll('.mini-a4').forEach(card => {
					card.onclick = () => {
						recentResumesRow.querySelectorAll('.mini-a4').forEach(c => c.classList.remove('selected'));
						card.classList.add('selected');
						runBtn.disabled = false;
					};
				});
			} else if (preselectedResumeId) {
				const resumeCard = recentResumesRow.querySelector(`[data-id="${preselectedResumeId}"]`);
				if (resumeCard) resumeCard.classList.add('selected');

				backBtn?.addEventListener('click', closeModal);
				jdStep.classList.remove('hidden');
				resumeStep.classList.add('hidden');
				finalActionsRow.classList.remove('hidden');
				runBtn.disabled = true;

				recentJdsRow.querySelectorAll('.mini-a4').forEach(card => {
					card.onclick = () => {
						recentJdsRow.querySelectorAll('.mini-a4').forEach(c => c.classList.remove('selected'));
						card.classList.add('selected');
						runBtn.disabled = false;
					};
				});
			} else {
				jdStep.classList.remove('hidden');
				resumeStep.classList.add('hidden');
				finalActionsRow.classList.add('hidden');
				runBtn.disabled = true;
			}
		} finally {
			hideSpinner();
		}
	}

	function closeModal() {
		modal?.classList.add('hidden');
		preselectedJDId = null;
		preselectedResumeId = null;
	}

	function backToJDStep() {
		resumeStep.classList.add('hidden');
		finalActionsRow.classList.add('hidden');
		jdStep.classList.remove('hidden');
	}

	async function fetchAndPopulate(type, container) {
		if (!container) return;
		showSpinner();
		try {
			const data = await fetch(`/api/viewpage/data?type=${type}`, { headers })
				.then(res => res.json())
				.catch(() => ({ recent5Uploads: [] }));
			populateMiniA4(container, data.recent5Uploads || [], type);
		} finally {
			hideSpinner();
		}
	}

	function searchJDs() {
		const query = document.getElementById('jdSearchInput')?.value || '';
		searchAndPopulate('jd', query, recentJdsRow);
	}

	function searchResumes() {
		const query = document.getElementById('resumeSearchInput')?.value || '';
		searchAndPopulate('resume', query, recentResumesRow);
	}

	async function searchAndPopulate(type, query, container) {
		if (!container) return;
		showSpinner();
		try {
			const subtext = document.querySelector(`#${type}Step .modal-subtext`);
			
			if (!query) {
				// 🧭 If empty query, reload recents
				subtext?.classList.remove("hidden");
				await fetchAndPopulate(type, container);
				return;
			}
			
			subtext?.classList.add("hidden");

			const data = await fetch(
				type === 'jd'
					? `/api/jd/search?query=${encodeURIComponent(query)}`
					: `/api/resume/search?query=${encodeURIComponent(query)}`, { headers }
			)
				.then(res => res.json())
				.catch(() => ({ content: [] }));

			// Handle Page content or recent list gracefully
			populateMiniA4(container, data.content || data.recent5Uploads || [], type);
		} finally {
			hideSpinner();
			
		}
	}

	function populateMiniA4(container, items, type) {
		container.innerHTML = '';
		items.forEach(item => {
			const div = document.createElement('div');
			div.className = 'mini-a4';
			div.dataset.id = item.jdId || item.resumeId || item.id;
			div.onclick = () => {
				if (type === 'jd') selectJDCard(div, item.jdId || item.id);
				else selectResumeCard(div, item.resumeId || item.id);
			};
			div.innerHTML = `
				<div class="corner-fold"></div>
				<div class="mini-a4-content">
					<h5>${item.title || item.name || item.fileName}</h5>
					<p class="meta-mini">${item.companyName || ''}</p>
					<p class="meta-mini">${item.createdAt || ''}</p>
				</div>`;
			container.appendChild(div);
		});
	}

	function selectJDCard(card, jdId) {
		recentJdsRow.querySelectorAll('.mini-a4')?.forEach(c => c.classList.remove('selected'));
		card.classList.add('selected');

		// 🧠 Check if a resume is already preselected
		if (preselectedResumeId) {
			finalActionsRow.classList.remove('hidden');
			runBtn.disabled = false; // Enable Run Analysis directly
		} else {
			// Normal flow: move to resume selection
			setTimeout(() => {
				jdStep.classList.add('hidden');
				resumeStep.classList.remove('hidden');
				finalActionsRow.classList.remove('hidden');
			}, 300);
		}
	}

	function selectResumeCard(card, resumeId) {
		recentResumesRow.querySelectorAll('.mini-a4')?.forEach(c => c.classList.remove('selected'));
		card.classList.add('selected');
		runBtn.disabled = false;
	}

	async function handleRunAnalysis() {
		const selectedJD = recentJdsRow.querySelector('.mini-a4.selected');
		const selectedResume = recentResumesRow.querySelector('.mini-a4.selected');

		const jdId = selectedJD?.dataset.id || preselectedJDId;
		const resumeId = selectedResume?.dataset.id || preselectedResumeId;

		if (!jdId || !resumeId) {
			await showAlert('Please select both a JD and a Resume.');
			return;
		}

		runBtn.disabled = true;
		showSpinner();

		try {
			const result = await runAnalysis(jdId, resumeId, headers);

			closeModal();

			console.log(">>> Analyze button clicked");
			showAnalysisCompleted(`${result.overall_fit * 100}`);

			if (typeof window.loadUserDashboardData === 'function')
				await window.loadUserDashboardData(headers);

			if (typeof window.loadLastAnalysisSummary === 'function')
				await window.loadLastAnalysisSummary(headers);

			if (typeof window.loadRecentActivity === 'function')
				await window.loadRecentActivity(headers);

			if (typeof window.loadAnalyzePageData === 'function')
				window.loadAnalyzePageData();

		} catch (err) {
			console.error("Analysis failed:", err);
			closeModal();
			showAlert("Analysis failed. Please try again.");
		} finally {
			hideSpinner();
			runBtn.disabled = false;
		}
	}

	return { init, openModal, closeModal };
})();



// ----------------------
// Analysis Completion Modal
// ----------------------
export function showAnalysisCompleted(fitScore = null) {
	return new Promise((resolve) => {
		const modal = document.getElementById("completionModal");
		if (!modal) {
			showAlert("Modal not loaded");
			resolve();
			return;
		}

		// Match your actual CSS class names
		const glassBox = modal.querySelector(".analyze-modal-glass-box");
		const fitScoreEl = document.getElementById("fitScoreValue");
		const closeBtn = document.getElementById("completionCloseBtn");
		const viewBtn = document.getElementById("completionViewReportBtn");

		if (!glassBox || !fitScoreEl || !closeBtn || !viewBtn) {
			showAlert("Modal elements missing");
			resolve();
			return;
		}

		// Set the score text dynamically (if provided)
		if (fitScore === null || fitScore === undefined) {
			fitScoreEl.textContent = "Fit Score unavailable";
		} else if (fitScore === 0) {
			fitScoreEl.textContent = "Fit Score: 0% — Fit score is zero for this analysis";
		} else {
			fitScoreEl.textContent = `Fit Score: ${fitScore}%`;
		}

		// --- Show Modal ---
		modal.classList.add("show"); // makes overlay visible
		glassBox.classList.remove("hidden"); // ensure content visible

		// --- Event Handlers ---
		function closeModal() {
			// Start fade-out
			glassBox.classList.add("hidden");

			setTimeout(() => {
				modal.classList.remove("show");
				resolve(); // resolve only after fade-out
			}, 300); // matches your CSS transition duration

			// Cleanup listeners
			closeBtn.removeEventListener("click", onCloseClick);
			viewBtn.removeEventListener("click", onViewClick);
			document.removeEventListener("keydown", onEscKey);
		}

		function onCloseClick() {
			closeModal();
		}

		function onViewClick() {
			closeModal();
			window.location.href = "/reports"; // adjust as needed
		}

		function onEscKey(e) {
			if (e.key === "Escape") closeModal();
		}

		// Attach listeners
		closeBtn.addEventListener("click", onCloseClick);
		viewBtn.addEventListener("click", onViewClick);
		document.addEventListener("keydown", onEscKey);
	});
}

//----------UPLOAD PROMPT-------------//

export function showUploadPrompt({ title, placeholder }) {
	return new Promise((resolve) => {
		let overlay = document.getElementById("uploadPromptOverlay");

		if (!overlay) {
			overlay = document.createElement("div");
			overlay.id = "uploadPromptOverlay";
			overlay.className = "upload-prompt-overlay";
			overlay.innerHTML = `
                <div class="upload-prompt-box">
                    <h3>${title}</h3>
                    <textarea id="uploadPromptInput" placeholder="${placeholder}"></textarea>
                    <div class="upload-prompt-buttons">
                        <button id="uploadCancelBtn" class="secondary">Cancel</button>
                        <button id="uploadConfirmBtn" class="primary">Upload</button>
                    </div>
                </div>
            `;
			document.body.appendChild(overlay);
		}

		const inputField = overlay.querySelector("#uploadPromptInput");
		const confirmBtn = overlay.querySelector("#uploadConfirmBtn");
		const cancelBtn = overlay.querySelector("#uploadCancelBtn");

		inputField.value = "";
		inputField.placeholder = placeholder;

		overlay.classList.add("show");

		const closeModal = () => overlay.classList.remove("show");

		cancelBtn.onclick = () => {
			closeModal();
			resolve(null);
		};

		confirmBtn.onclick = () => {
			const value = inputField.value.trim();
			if (!value) {
				alert("Please enter some text.");
				return;
			}
			closeModal();
			resolve(value);
		};
	});
}

export async function logActivity(actionType, entity, entityName, details, status) {
	const headers = {
		"Content-Type": "application/json",
		"Authorization": `Bearer ${sessionStorage.getItem("token")}`
	};

	const body = {
		actionType,
		entity,
		entityName,
		details,
		status
	};

	try {
		await fetch("/api/activity/upload", {
			method: "POST",
			headers,
			body: JSON.stringify(body)
		});
	} catch (err) {
		console.error("Activity logging failed:", err);
	}
}





