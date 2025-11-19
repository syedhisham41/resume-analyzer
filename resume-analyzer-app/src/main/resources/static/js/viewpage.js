import {
	showSpinner, hideSpinner, showAlert, showToast, runAnalysis,
	AnalyzeSelectionModal, showAnalysisCompleted
} from './common.js';

const token = sessionStorage.getItem("token");
if (!token) window.location.href = "/login";

const headers = {
	"Authorization": `Bearer ${token}`,
	"Content-Type": "application/json"
};

const handleUnauthorized = (res) => {
	if (res.status === 401) {
		window.location.href = "/login";
	}
};

// ------------------- LOAD PAGE DATA -------------------
async function loadViewPageData(type) {
	try {
		showSpinner(); // show spinner immediately

		const response = await fetch(`/api/viewpage/data?type=${type}`, { headers });
		if (!response.ok) throw new Error("Failed to load data");

		const data = await response.json();
		populateViewPage(data, type);

	} catch (error) {
		console.error("Error loading view page data:", error);
	} finally {
		hideSpinner();
	}
}

// ------------------- POPULATE PAGE -------------------
function populateViewPage(data, type) {
	// 1. Update header stats
	document.getElementById('totalCount').textContent = data.totalCount;
	document.getElementById('lastUploadedDate').textContent = data.lastUploadedDate || '—';

	// 2. Populate recent 5 uploads
	const recentContainer = document.getElementById('recentUploads');
	recentContainer.innerHTML = '';

	data.recent5Uploads.forEach(item => {
		const div = document.createElement('div');
		div.className = 'a4-paper';
		div.style.position = "relative"; // for corner fold positioning
		div.setAttribute(type === 'jd' ? 'data-jd-id' : 'data-resume-id',
			type === 'jd' ? item.jdId : item.resumeId
		);

		const content = item.content;
		const truncatedContent = content.split('\n').slice(0, 3).join(' ') + (content.split('\n').length > 3 ? '...' : '');

		div.innerHTML = `
	        <div class="corner-fold"></div>
	        <h4>${item.title}</h4>
	        <div class="meta">
	            <span class="company">${item.companyName || '—'}</span>
	            <span class="date">${new Date(item.createdAt).toLocaleDateString()}</span>
	        </div>
	        <p class="preview-content">${truncatedContent}</p>
	    `;

		recentContainer.appendChild(div);
	});

	// 3. Populate all items
	const allContainer = document.getElementById('allUploads');
	allContainer.innerHTML = '';

	data.allItems.forEach(item => {
		const itemDiv = document.createElement('div');
		itemDiv.className = type === 'jd' ? 'jd-item' : 'resume-item';
		itemDiv.id = type === 'jd' ? item.jdId : item.resumeId;

		itemDiv.innerHTML = `
		  <div class="type-item-header">
		    <h4>${item.title}</h4>
			<div class="meta-right">
			    <span class="company">${type === 'jd' ? item.companyName : ''}</span>
			    <span class="separator"> — </span>
			    <span class="date" data-date="${item.createdAt}">${new Date(item.createdAt).toDateString()}</span>
			  </div>
		  </div>
		  <div class="${type}-item-content">
		      <div class="jd-content-box">
		        <pre>${item.content}</pre>
		      </div>
		      <div class="type-actions">
		        ${type === 'jd' ? `
		          <button class="analyze-btn" data-type="jd" data-id="${item.jdId}" data-content="${item.content}">Analyze JD</button>
		          <button class="match-btn" data-id="${item.jdId}" data-type="jd" >Match with Resume</button>
		          <button class="delete-btn">Delete</button>
		          <button class="to-top-btn">Got to Top</button>
		        ` : `
		          <button class="analyze-btn" data-type="resume" data-id="${item.resumeId}" data-content="${item.content}">Analyze Resume</button>
		          <button class="match-btn" data-id="${item.resumeId}" data-type="resume" >Match with JD</button>
		          <button class="delete-btn">Delete</button>
		          <button class="to-top-btn">Got to Top</button>
		        `}
		      </div>
		  </div>
		`;
		allContainer.appendChild(itemDiv);
	});

	// 4. Attach behaviors
	if (type === 'jd') {
		setupItemToggle('.jd-item', '.type-item-header');
		setupOpenFromRecent('.a4-paper', 'data-jd-id', '.jd-item');
		//setupSearch('searchJD', 'filterCompany', 'filterTitle', '.jd-item');
		setupSearch('.jd-item'); // No need for inputId/filterCompany/filterTitle

	} else {
		setupItemToggle('.resume-item', '.type-item-header');
		setupOpenFromRecent('.a4-paper', 'data-resume-id', '.resume-item');
		//setupSearch('searchResume', 'filterCompany', 'filterTitle', '.resume-item');
		setupSearch('.resume-item'); // No need for inputId/filterCompany/filterTitle

	}

	document.querySelectorAll('.to-top-btn').forEach(btn => {
		btn.addEventListener('click', () => {
			window.scrollTo({ top: 0, behavior: 'smooth' });
		});
	});

	setupDeleteHandlers(type);
	AnalyzeSelectionModal.init();
	setupAnalyzeHandlers();
}

// ------------------- DELETE HANDLER -------------------
let deleteTarget = null;

function setupDeleteHandlers(type) {
	document.querySelectorAll('.delete-btn').forEach(btn => {
		btn.addEventListener('click', (event) => {
			event.stopPropagation();
			deleteTarget = { type, btn };
			openDeleteModal();
		});
	});
}

function openDeleteModal() {
	const modal = document.getElementById('deleteModal');
	const glassBox = modal.querySelector('.modal-glass-box');

	modal.classList.add('show');
	glassBox.classList.add('hidden');
	requestAnimationFrame(() => glassBox.classList.remove('hidden'));
}

function closeDeleteModal() {
	const modal = document.getElementById('deleteModal');
	const glassBox = modal.querySelector('.modal-glass-box');

	glassBox.classList.add('hidden');
	setTimeout(() => modal.classList.remove('show'), 300);
}

const confirmBtn = document.getElementById('confirmDeleteBtn');
const cancelBtn = document.getElementById('cancelDeleteBtn');

if (confirmBtn && cancelBtn) {
	confirmBtn.addEventListener('click', async () => {
		if (!deleteTarget) return;
		const { type, btn } = deleteTarget;
		const parentItem = btn.closest(type === 'jd' ? '.jd-item' : '.resume-item');
		const itemId = parentItem.id;

		try {
			showSpinner();
			const response = await fetch(`/api/${type}/delete?${type}Id=${itemId}`, {
				method: 'DELETE',
				headers
			});

			if (!response.ok) throw new Error('Failed to delete');

			parentItem.remove();
			showToast("Deleted successfully!", "success");
			loadViewPageData(type);

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

// ------------------- TOGGLE & OPEN -------------------
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

// ------------------- SEARCH -------------------
function setupSearch(itemClass) {
	const inputEl = document.getElementById("searchInput");
	const startDateEl = document.getElementById("startDate");
	const endDateEl = document.getElementById("endDate");
	const toggleBtns = document.querySelectorAll(".toggle-btn");
	const container = document.querySelector(itemClass).parentElement;

	if (!inputEl || !toggleBtns.length) return;

	let searchMode = "title"; // default

	// --- Toggle button behavior ---
	toggleBtns.forEach(btn => {
		btn.addEventListener("click", () => {
			// Set active button
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

		if (start) start.setHours(0, 0, 0, 0); // inclusive start of day
		if (end) end.setHours(23, 59, 59, 999); // inclusive end of day
		
		let anyVisible = false;
		let activeAnimations = 0;

		const items = document.querySelectorAll(itemClass);

		items.forEach(item => {
			const h4El = item.querySelector("h4");
			const companySpan = item.querySelector(".company");
			const dateSpan = item.querySelector(".date");

			let itemTitle = h4El.textContent;
			let itemCompany = companySpan ? companySpan.textContent : "";
			let itemDate = dateSpan ? new Date(dateSpan.dataset.date || dateSpan.textContent) : null;

			let match = false;
			if (searchMode === "title") {
				match = !term || itemTitle.toLowerCase().includes(term);
			} else if (searchMode === "company") {
				match = !term || itemCompany.toLowerCase().includes(term);
			} else if (searchMode === "date") {
				if (!start && !end) match = true;
				else if (itemDate) {
					if (start && end) match = itemDate >= start && itemDate <= end;
					else if (start) match = itemDate >= start;
					else if (end) match = itemDate <= end;
				}
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
				} else {
					h4El.innerHTML = itemTitle;
					if (companySpan) companySpan.innerHTML = itemCompany;
				}
			} else {
				if (!item.classList.contains("fade-out")) {
					item.classList.remove("fade-in");
					item.classList.add("fade-out");

					activeAnimations++;
					item.addEventListener("transitionend", () => {
						item.style.display = "none";
						activeAnimations--;
						if (activeAnimations === 0) {
							updateEmptyState();
						}
					}, { once: true });
				}
				h4El.innerHTML = itemTitle;
				if (companySpan) companySpan.innerHTML = itemCompany;
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

		// ✅ handle empty state AFTER transitions
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

// ------------------- ANALYZE MODAL -------------------
function setupAnalyzeHandlers() {
	// Open modal
	document.querySelectorAll(".analyze-btn").forEach(btn => {
		btn.addEventListener("click", () => {
			const type = btn.getAttribute("data-type");
			const id = btn.getAttribute("data-id");
			const content = btn.getAttribute("data-content");
			openAnalyzeModal(type, id, content);
		});
	});

	// Close modal buttons (persistent listeners)
	const modal = document.getElementById("analyzeModal");
	const closeBtns = [
		document.getElementById("analyzeCloseBtn"),
		document.getElementById("closeAnalyzeModalBtn")
	];
	closeBtns.forEach(btn => btn.addEventListener("click", () => modal.classList.remove("show")));


	document.querySelectorAll('.match-btn').forEach(matchBtn => {
		matchBtn.addEventListener('click', () => {
			console.log(">>> Match button clicked", matchBtn.dataset);
			const type = matchBtn.dataset.type;
			const itemId = matchBtn.dataset.id;
			let options = null;
			if (type === 'jd') {
				options = { preselectJDId: itemId };
			} else {
				options = { preselectResumeId: itemId }
			}
			console.log("Opening AnalyzeSelectionModal with options:", options);
			AnalyzeSelectionModal.openModal(options);
		});
	});
}

export async function openAnalyzeModal(type, id, content) {
	const modal = document.getElementById("analyzeModal");
	const skillsEl = document.getElementById("parsedSkills");
	const skillsCountEl = document.getElementById("skillsCount");

	// Reset content
	skillsEl.innerHTML = "<li>Loading...</li>";
	skillsCountEl.textContent = "0 Skills";

	const endpoint = `/api/viewpage/extractData`;
	try {
		showSpinner();
		const response = await fetch(endpoint, {
			method: 'POST',
			headers,
			body: JSON.stringify({ text: content })
		});

		if (!response.ok) throw new Error("Failed to fetch analysis");
		const data = await response.json();

		// Populate skills
		if (data.skills?.length) {
			skillsEl.innerHTML = data.skills.map(s => `<li>${s}</li>`).join("");
			skillsCountEl.textContent = `${data.skills.length} Skill${data.skills.length > 1 ? 's' : ''}`;
		} else {
			skillsEl.innerHTML = "<li>No skills found</li>";
			skillsCountEl.textContent = "0 Skills";
		}

	} catch (err) {
		console.error("Error fetching analysis:", err);
		skillsEl.innerHTML = "<li>Error loading skills</li>";
		skillsCountEl.textContent = "0 Skills";
		showAlert("Analysis could not be fetched");
	} finally {
		hideSpinner();
	}

	// Show modal
	modal.classList.add("show");
}

// Initialize handlers on page load
document.addEventListener("DOMContentLoaded", setupAnalyzeHandlers);


// ------------------- INIT -------------------
document.addEventListener('DOMContentLoaded', () => {
	const isJdPage = document.querySelector('[data-jd-page]');
	const isResumePage = document.querySelector('[data-resume-page]');

	if (isJdPage) {
		loadViewPageData('jd');
	} else if (isResumePage) {
		loadViewPageData('resume');
	}
});
