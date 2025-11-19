import {
	showSpinner, hideSpinner, showAlert, showToast, runAnalysis,
	AnalyzeSelectionModal, showAnalysisCompleted, showUploadPrompt
} from './common.js';

import { openAnalyzeModal } from './viewpage.js';
let uploadedTypeId = null;
let uploadedContent = null;
let type = null;

// ======== JD Upload Page JS ========

// Show section (Raw Text / PDF / DOCX)
function showSection(id, event) {
	document.querySelectorAll('.option-btn').forEach(btn => btn.classList.remove('active'));
	document.querySelectorAll('.form-section').forEach(sec => sec.classList.remove('active'));
	document.getElementById(id).classList.add('active');
	if (event) event.currentTarget.classList.add('active');
}

// ======== Preview JD ========
function previewJD(btn) {
	type = btn.dataset.type;
	const contentValue = type === 'jd' ? document.getElementById("jdContent").value.trim() : document.getElementById("resumeContent").value.trim();
	if (!contentValue) {
		showAlert(`Please enter ${type} text before previewing.`);
		return;
	}

	const previewBtn = document.querySelector('#rawText .action-buttons .primary-btn');
	previewBtn.disabled = true;
	previewBtn.textContent = uploadedTypeId ? "Updating..." : "Uploading...";

	const token = sessionStorage.getItem('token');

	// Decide endpoint & method
	const url = uploadedTypeId ? `/api/uploadpage/${type}/${uploadedTypeId}` : `/api/uploadpage/${type}`;
	const method = uploadedTypeId ? 'PUT' : 'POST'; // POST = new, PUT = update

	fetch(url, {
		method,
		headers: {
			'Content-Type': 'text/plain',
			'Authorization': `Bearer ${token || ''}`
		},
		body: contentValue
	})
		.then(res => {
			if (!res.ok) throw new Error(`Failed to upload ${type} text.`);
			return res.json(); // backend returns { jdId, titles: [...], companies: [...] }
		})
		.then(data => {
			uploadedTypeId = type === 'jd' ? data.jdId : data.resumeId; // store the JD ID for future edits

			uploadedContent = data.content;

			// Hide preview button
			previewBtn.style.display = "none";

			const hidden = document.getElementById("hiddenFields");
			hidden.style.display = "block";
			hidden.style.opacity = "0";
			hidden.style.maxHeight = "0";
			setTimeout(() => {
				hidden.classList.add("visible");
				hidden.style.transition = "opacity 0.4s ease, max-height 0.5s ease";
				hidden.style.opacity = "1";
				hidden.style.maxHeight = "500px";
			}, 50);

			// Add Edit Type button if not present
			if (!document.getElementById("editType")) {
				const editBtn = document.createElement("button");
				editBtn.className = "secondary-btn";
				editBtn.id = "editType";
				editBtn.textContent = type === 'jd' ? "Edit JD" : "Edit Resume";
				editBtn.onclick = editType;
				hidden.querySelector(".action-buttons").appendChild(editBtn);
			}

			// Populate candidates dynamically based on type
			if (type === 'jd') populateCandidates();
			if (type === 'resume' && data.title) {
				const defaultName = document.getElementById("defaultResumeName");
				const customNameInput = document.getElementById("customResumeName");

				if (defaultName) {
					defaultName.textContent = data.title; // show the auto-generated name
				}

				if (customNameInput) {
					customNameInput.value = ""; // clear any old input
					customNameInput.placeholder = "Type to change the name (optional)";
				}
			}
		})
		.catch(err => {
			console.error(err);
			showAlert(err.message || `Failed to upload ${type}.`);
		})
		.finally(() => {
			previewBtn.disabled = false;
			previewBtn.textContent = uploadedTypeId ? "Update & Preview" : "Preview & Upload";
		});
}

// ======== Populate Candidates from API ========
function populateCandidates() {
	if (!uploadedTypeId) return;

	const token = sessionStorage.getItem('token');
	fetch(`/api/jd/${uploadedTypeId}/candidates`, {
		method: 'GET',
		headers: { 'Authorization': `Bearer ${token || ''}` }
	})
		.then(res => res.json())
		.then(data => {
			const titleSelect = document.getElementById("jdTitleSelect");
			const companySelect = document.getElementById("companyNameSelect");
			const titleOther = document.getElementById("jdTitleOther");
			const companyOther = document.getElementById("companyOther");

			// Clear existing dropdown options completely
			titleSelect.innerHTML = '<option value="">Select Title...</option>';
			companySelect.innerHTML = '<option value="">Select Company...</option>';

			// Reset “Other” fields to disabled and empty
			titleOther.disabled = true;
			titleOther.value = '';
			titleOther.classList.remove("editable");

			companyOther.disabled = true;
			companyOther.value = '';
			companyOther.classList.remove("editable");

			// Populate new candidate options
			data.forEach(c => {
				const opt = document.createElement("option");
				opt.value = c.candidateValue;
				opt.textContent = c.candidateValue;
				if (c.selected) opt.selected = true;

				if (c.type === "TITLE") titleSelect.add(opt);
				else if (c.type === "COMPANY") companySelect.add(opt);
			});

			// Always append “Other” as the last option
			titleSelect.insertAdjacentHTML('beforeend', `<option value="Other">Other</option>`);
			companySelect.insertAdjacentHTML('beforeend', `<option value="Other">Other</option>`);

			titleSelect.addEventListener("change", (e) => {
				if (e.target.value === "Other") {
					titleOther.disabled = false;
					titleOther.classList.add("editable");
					titleOther.focus();
				} else {
					titleOther.disabled = true;
					titleOther.classList.remove("editable");
					titleOther.value = '';
				}
			});

			companySelect.addEventListener("change", (e) => {
				if (e.target.value === "Other") {
					companyOther.disabled = false;
					companyOther.classList.add("editable");
					companyOther.focus();
				} else {
					companyOther.disabled = true;
					companyOther.classList.remove("editable");
					companyOther.value = '';
				}
			});
		})
		.catch(err => console.error("Failed to fetch candidates", err));
}


// ======== Select candidate and update JD ========
function confirmCandidateSelection() {
	const selectedOption = document.querySelector('#candidateList option:checked');
	if (!selectedOption) {
		showAlert("Please select a candidate.");
		return;
	}

	const token = sessionStorage.getItem('token');

	fetch(`/api/jd/${uploadedTypeId}/select`, {
		method: 'POST',
		headers: {
			'Authorization': `Bearer ${token || ''}`,
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			jdValueType: "COMPANY", // you can determine dynamically from candidatesData if needed
			selectedJdValue: selectedOption.value
		})
	})
		.then(res => res.json())
		.then(updatedJD => {
			showAlert("JD updated successfully!");
			console.log("JD updated:", updatedJD);
		})
		.catch(err => showAlert(err.message));
}

// ======== Edit JD/Resume ========
function editType() {
	const hiddenFields = document.getElementById("hiddenFields");
	const previewBtn = document.querySelector('#rawText .action-buttons .primary-btn');
	const jdBlock = document.getElementById("jdBlock");
	const resumeBlock = document.getElementById("resumeBlock");
	const editBtn = document.getElementById("editType");

	hiddenFields.style.transition = "opacity 0.4s ease, max-height 0.4s ease";
	hiddenFields.style.opacity = "0";
	hiddenFields.style.maxHeight = "0";

	if (editBtn) {
		editBtn.style.transition = "opacity 0.4s ease";
		editBtn.style.opacity = "0";
	}

	setTimeout(() => {
		hiddenFields.classList.remove("visible");
		hiddenFields.style.display = "none";
		if (editBtn) editBtn.remove();

		if (previewBtn) {
			previewBtn.style.display = "inline-block";
			previewBtn.style.opacity = "0";
			previewBtn.style.transform = "translateY(-10px)";
			setTimeout(() => {
				previewBtn.style.transition = "opacity 0.4s ease, transform 0.4s ease";
				previewBtn.style.opacity = "1";
				previewBtn.style.transform = "translateY(0)";
			}, 50);
		}

		type === 'jd' ? jdBlock.classList.remove("partial-retract") : resumeBlock.classList.remove("partial-retract");
	}, 400);
}

// ======== Confirm Upload ========
async function confirmUpload() {
	if (!uploadedTypeId) {
		showAlert(`No ${type} uploaded yet!`);
		return;
	}

	let jdTitle = null;
	let companyName = null;
	let resumeTitle = null;

	if (type === 'jd') {
		jdTitle = document.getElementById("jdTitleSelect").value === "Other"
			? document.getElementById("jdTitleOther").value.trim()
			: document.getElementById("jdTitleSelect").value;

		companyName = document.getElementById("companyNameSelect").value === "Other"
			? document.getElementById("companyOther").value.trim()
			: document.getElementById("companyNameSelect").value;
	} else {
		resumeTitle = document.getElementById("customResumeName").value.trim();
	}

	const token = sessionStorage.getItem('token');
	showSpinner();

	try {
		// Update TITLE
		if (jdTitle) {
			const titleRes = await fetch(`/api/jd/${uploadedTypeId}/select`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${token || ''}`
				},
				body: JSON.stringify({
					jdValueType: "TITLE",
					selectedJdValue: jdTitle
				})
			});
			if (!titleRes.ok) throw new Error("Failed to update JD title");
		}

		// Update COMPANY
		if (companyName) {
			const companyRes = await fetch(`/api/jd/${uploadedTypeId}/select`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${token || ''}`
				},
				body: JSON.stringify({
					jdValueType: "COMPANY",
					selectedJdValue: companyName
				})
			});
			if (!companyRes.ok) throw new Error("Failed to update JD company");
		}

		if (resumeTitle) {
			const res = await fetch(`/api/uploadpage/resume/${uploadedTypeId}/title`, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${token || ''}`
				},
				body: resumeTitle
			});
			if (!res.ok) throw new Error("Failed to update Resume with new title");
		}
		hideSpinner();
		postUploadUI();

	} catch (err) {
		hideSpinner();
		console.error(err);
		showAlert(err.message || "Failed to update JD selections.");
	}
}

//postUploadUI
function postUploadUI(contenttype = null, uploadedData = null) {
	console.log("inside postUloadUI");
	const jdBlock = document.getElementById("jdBlock");
	const resumeBlock = document.getElementById("resumeBlock");
	const hiddenFields = document.getElementById("hiddenFields");
	const quickLinks1 = document.getElementById('quickLinks1');

	type = contenttype || type;
	console.log('type', type);
	if (uploadedData) {

		if (type === 'jd') uploadedTypeId = uploadedData.jdId;
		else uploadedTypeId = uploadedData.resumeId;      // adjust based on backend response
		uploadedContent = uploadedData.content; // if needed
	}
	console.log('uploadedTypeId', uploadedTypeId);

	hiddenFields.style.transition = "opacity 0.4s ease, max-height 0.4s ease";
	hiddenFields.style.opacity = "0";
	hiddenFields.style.maxHeight = "0";

	if (jdBlock && type === 'jd') {
		console.log('inside jdBlock');
		jdBlock.style.transition = "max-height 0.5s ease, opacity 0.4s ease";
		jdBlock.classList.add("full-retract");
	}
	else if (resumeBlock && type === 'resume') {
		console.log('inside resumeBlock');
		resumeBlock.style.transition = "max-height 0.5s ease, opacity 0.4s ease";
		resumeBlock.classList.add("full-retract");
	}


	setTimeout(() => {
		hiddenFields.classList.remove("visible");
		hiddenFields.style.display = "none";
		quickLinks1.style.opacity = "1";
		quickLinks1.style.display = "block";
		quickLinks1.style.transition = "opacity 0.5s ease";
		setTimeout(() => quickLinks1.style.opacity = "0", 50);

		const postUpload = document.getElementById("postUpload");
		if (!postUpload) {
			console.log('postupload is empty');
			return;
		}
		console.log('postupload is not empty');
		const successMsg = document.createElement("div");
		successMsg.innerHTML = `<span style="color:#555;font-weight:700;font-size:16px;">✓ ${type} Uploaded Successfully!</span>`;
		successMsg.style.textAlign = "center";
		successMsg.style.marginBottom = "15px";
		successMsg.classList.add("fade-in");
		postUpload.parentNode.insertBefore(successMsg, postUpload);

		setTimeout(() => {
			postUpload.classList.add("visible");

			const quickLinks = document.getElementById("quickLinks");
			type === 'jd' ? quickLinks.innerHTML = `
                <a href="/jdupload">Upload Another JD</a>
                <a href="/dashboard">Back to Dashboard</a>
                <a href="/jdview">Go to JD Main Page</a>
            ` : quickLinks.innerHTML = `
				                <a href="/resumeupload">Upload Another Resume</a>
				                <a href="/dashboard">Go to User Dashboard</a>
				                <a href="/resumeview">Go to Resume Dashboard</a>
				            `;
			quickLinks.style.opacity = "0";
			quickLinks.style.display = "block";
			quickLinks.style.transition = "opacity 0.5s ease";
			setTimeout(() => quickLinks.style.opacity = "1", 50);
		}, 300);

		//functionality for the postupload buttons
		/*
		postUpload.querySelector("button:nth-child(1)").addEventListener("click", () => {
			if (!uploadedTypeId || !uploadedContent) {
				showAlert(`${type} not uploaded yet!`);
				return;
			}

			openAnalyzeModal(type, uploadedTypeId, uploadedContent);
		});*/
		const btn1 = postUpload.querySelector("button:nth-child(1)");
		if (btn1) btn1.onclick = () => {
			if (!uploadedTypeId || !uploadedContent) return showAlert(`${type} not uploaded yet!`);
			openAnalyzeModal(type, uploadedTypeId, uploadedContent);
		};

		// Second button placeholder
		postUpload.querySelector("button:nth-child(2)").addEventListener("click", async () => {
			console.log("Click handler attached!");
			if (type === 'jd' && uploadedTypeId) {
				// Prompt user to upload resume
				const resumeText = await showUploadPrompt({
					title: "Paste Resume Text",
					placeholder: "Paste resume content here..."
				});
				if (!resumeText) return;

				console.log(resumeText);

				showSpinner();
				try {
					const token = sessionStorage.getItem('token');
					const res = await fetch(`/api/uploadpage/resume`, {
						method: "POST",
						headers: { "Content-Type": "text/plain", "Authorization": `Bearer ${token}` },
						body: resumeText
					});
					if (!res.ok) throw new Error("Failed to upload resume");
					const data = await res.json();
					let uploadedResumeId = data.resumeId;

					// Immediately run analysis using uploadedJDId + uploadedResumeId
					const result = await runAnalysis(uploadedTypeId, uploadedResumeId, {
						"Authorization": `Bearer ${token}`,
						"Content-Type": "application/json"
					});

					console.log("fit score", result.overall_fit);
					showAnalysisCompleted(result.overall_fit * 100);

				} catch (err) {
					showAlert(err.message);
				} finally {
					hideSpinner();
				}
			} else if (type === 'resume' && uploadedTypeId) {
				// Upload JD instead
				const jdText = await showUploadPrompt({
					title: "Paste JD Text",
					placeholder: "Paste JD content here..."
				});
				if (!jdText) return;

				showSpinner();
				try {
					const token = sessionStorage.getItem('token');
					const res = await fetch(`/api/uploadpage/jd`, {
						method: "POST",
						headers: { "Content-Type": "text/plain", "Authorization": `Bearer ${token}` },
						body: jdText
					});
					if (!res.ok) throw new Error("Failed to upload JD");
					const data = await res.json();
					let uploadedJDId = data.jdId;
					console.log('uploadedJdId', uploadedJDId);
					console.log('uploadedresumeId', uploadedTypeId);



					// Immediately run analysis using uploadedJDId + uploadedResumeId
					const result = await runAnalysis(uploadedJDId, uploadedTypeId, {
						"Authorization": `Bearer ${token}`,
						"Content-Type": "application/json"
					});
					showAnalysisCompleted(result.overall_fit * 100);

				} catch (err) {
					showAlert(err.message);
				} finally {
					hideSpinner();
				}
			}
		});

		// Third button placeholder
		/*
		postUpload.querySelector("button:nth-child(3)").addEventListener("click", () => {
			AnalyzeSelectionModal.init();

			let options = null;
			if (type === 'jd')
				options = { preselectJDId: uploadedTypeId };
			else
				options = { preselectResumeId: uploadedTypeId };
			AnalyzeSelectionModal.openModal(options);
		});
		*/

		const btn3 = postUpload.querySelector("button:nth-child(3)");
		if (btn3) btn3.onclick = () => {
			AnalyzeSelectionModal.init();

			let options = null;
			if (type === 'jd')
				options = { preselectJDId: uploadedTypeId };
			else
				options = { preselectResumeId: uploadedTypeId };
			AnalyzeSelectionModal.openModal(options);
		};



	}, 400);
}

async function uploadFile(file, category, fileType) {
	const formData = new FormData();
	formData.append('file', file);

	const token = sessionStorage.getItem('token');

	// Dynamic API based on category and type
	const apiMap = {
		resume: {
			pdf: '/api/uploadpage/resumepdf',
			docx: '/api/uploadpage/resumedocx'
		},
		jd: {
			pdf: '/api/uploadpage/jdpdf',
			docx: '/api/uploadpage/jdpdf'
		}
	};

	const apiUrl = apiMap[category][fileType];

	try {
		const response = await fetch(apiUrl, {
			method: 'POST',
			body: formData,
			headers: { 'Authorization': `Bearer ${token || ''}` }
		});

		if (!response.ok) throw new Error('Upload failed');

		const data = await response.json();
		console.log(`${category.toUpperCase()} ${fileType.toUpperCase()} uploaded`, data);
		//showAlert(`${category.toUpperCase()} ${fileType.toUpperCase()} uploaded successfully!`);
		hideSpinner();
		showAlert(`${category.toUpperCase()} ${fileType.toUpperCase()} uploaded successfully!`).then(() => {
			if(fileType === 'pdf') document.getElementById('pdf').classList.remove('active');
			else document.getElementById('docx').classList.remove('active');
			postUploadUI(category, data);
			
			});
		//postUploadUI(category, data);
		//setTimeout(() => postUploadUI(category, data), 500);
	} catch (err) {
		console.error(err);
		showAlert(`Failed to upload ${category.toUpperCase()} ${fileType.toUpperCase()}`);
	}
}





// ======== Expose functions globally for HTML onclick ========
window.showSection = showSection;
window.previewJD = previewJD;
window.editType = editType;
window.confirmUpload = confirmUpload;

window.addEventListener('DOMContentLoaded', () => {
	const quickLinks1 = document.getElementById('quickLinks1');
	if (!quickLinks1) return;

	// Initially hide
	quickLinks1.style.opacity = "0";
	quickLinks1.style.display = "block";

	// Fade in after 0.25s
	setTimeout(() => {
		quickLinks1.style.transition = "opacity 0.4s ease";
		quickLinks1.style.opacity = "1";
	}, 250);

	document.querySelectorAll('.form-section').forEach(section => {

		const fileUpload = section.querySelector('.file-upload');
		if (!fileUpload) return;
		const fileType = section.dataset.fileType;   // pdf / docx
		const category = section.dataset.category;   // resume / jd

		// Click to select file
		fileUpload.addEventListener('click', () => {
			const input = document.createElement('input');
			input.type = 'file';
			input.style = 'display: none;';
			input.accept = fileType === 'pdf' ? '.pdf' : '.docx';
			input.onchange = () => {
				if (input.files.length > 0) {
					showSpinner();
					uploadFile(input.files[0], category, fileType);
				}
			};
			input.click();
		});

		// Drag & drop
		fileUpload.addEventListener('dragover', (e) => {
			e.preventDefault();
			fileUpload.classList.add('drag-over');
		});

		fileUpload.addEventListener('dragleave', (e) => {
			e.preventDefault();
			fileUpload.classList.remove('drag-over');
		});

		fileUpload.addEventListener('drop', (e) => {
			e.preventDefault();
			fileUpload.classList.remove('drag-over');
			const files = e.dataTransfer.files;
			if (files.length > 0) {
				showSpinner();
				uploadFile(files[0], category, fileType);
			}
		});
	});

});
