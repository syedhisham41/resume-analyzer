//js/settings.js

import { showAlert } from './common.js';

const token = sessionStorage.getItem("token");
if (!token) {
	window.location.href = "/login";
}

const headers = {
	"Authorization": `Bearer ${token}`,
	"Content-Type": "application/json"
};

async function loadProfileDetails() {
	try {
		const response = await fetch("/api/settingpage/getdata", {
			method: "GET",
			headers: headers
		});
		if (response.status === 401) return window.location.href = "/login";
		if (!response.ok) throw new Error("Failed to fetch profile data");

		const data = await response.json();

		const fullName = document.getElementById("viewFullName");
		const userName = document.getElementById("viewUsername");
		const email = document.getElementById("viewEmail");
		const company = document.getElementById("viewCompany");
		const role = document.getElementById("viewRole");

		fullName.textContent = data.name;
		userName.textContent = data.userName;
		email.textContent = data.email;
		company.textContent = data.currentCompany;
		role.textContent = data.currentRole;

		document.getElementById("welcomeMessage").textContent = 'Welcome, ' + data.name;

	} catch (err) {
		console.error("Error loading profile:", err);
	}
}

async function updateProfileDetails() {

	const editFullName = document.getElementById("editFullName").value ? document.getElementById("editFullName").value : document.getElementById("viewFullName").textContent;
	const editCompany = document.getElementById("editCompany").value ? document.getElementById("editCompany").value : document.getElementById("viewCompany").textContent;
	const editRole = document.getElementById("editRole").value ? document.getElementById("editRole").value : document.getElementById("viewRole").textContent;

	const data = { name: editFullName, currentCompany: editCompany, currentRole: editRole };

	try {
		const res = await fetch("/api/settingpage/updatedata", {
			method: "POST",
			headers: headers,
			body: JSON.stringify(data)
		});

		if (res.status === 401) return window.location.href = "/login";
		if (!res.ok) throw new Error("Failed to update profile data");
	} catch (err) {
		console.error("Error updating profile:", err);
	}
}

async function deleteUserAccount() {

	try {
		const res = await fetch("/api/settingpage/delete", {
			method: "DELETE",
			headers: headers
		});

		if (res.status === 401) return window.location.href = "/login";
		if (!res.ok) throw new Error("Failed to delete user account");
	} catch (err) {
		console.error("Error deleting User Account:", err);
	}
}

async function updateUserPassword() {

	const currentPwd = document.getElementById("currentPassword").value;
	const newPwd = document.getElementById("newPassword").value;
	const confirmPwd = document.getElementById("confirmPassword").value;

	if (!currentPwd) {
		showAlert("Please give the current password");
		return;
	}

	if (!newPwd && !confirmPwd) {
		showAlert("new and confirm password fields should not be empty")
		return;
	}


	const data = { currentPassword: currentPwd, newPassword: newPwd, confirmPassword: confirmPwd };

	try {
		const res = await fetch("/api/settingpage/updatepwd", {
			method: "POST",
			headers: headers,
			body: JSON.stringify(data)
		});

		if (!res.ok) {
			const errordata = await res.json();
			console.log(errordata);
			showAlert(errordata.errormessage);
			return;
		}

		showAlert("Password changed Successfully !. Please login again with new Password").then(() => { window.location.href = "/login" });

	} catch (err) {
		console.error("Error updating profile:", err);
	}


}

async function loadToken() {
	const token = sessionStorage.getItem('api_token');
	if (!token) return;


	const parts = token.split('.'); // JWT has 3 parts
	if (parts.length === 3) {
		const masked = '*****************.*****************.' + `${parts[2]}`;

		// populate into UI
		const tokenEl = document.querySelector('.token-preview .visible-chars');
		if (tokenEl) tokenEl.textContent = masked;
	}
}

async function regenerateToken() {

	try {
		const res = await fetch("/api/settingpage/generatetoken", {
			method: "GET",
			headers: headers
		});

		if (!res.ok) {
			const errordata = await res.json();
			console.log(errordata);
			showAlert(errordata.errormessage);
			return;
		}

		showAlert("token generated successfully.");
		sessionStorage.setItem("api_token", token);
		

	} catch (err) {
		console.error("Error generating token:", err);
	}
}

async function attachClickHandlers() {

	//attaching logout
	const logOutBtn = document.getElementById("logoutButton");
	logOutBtn.addEventListener('click', () => window.location.href = "/welcome");


	//attaching save profile click
	const saveProfileBtn = document.getElementById("saveProfileBtn");
	saveProfileBtn.addEventListener('click', async () => {
		await updateProfileDetails();
		loadProfileDetails();

		const viewSection = document.getElementById("profileView");
		const editSection = document.getElementById("profileEdit");
		editSection.style.display = "none";
		viewSection.style.display = "block";
	});

	//attach delete account 
	async function openDeleteAccountModal() {
		const modal = document.getElementById('deleteAccountModal');
		const glassBox = modal.querySelector('.modal-glass-box');

		modal.classList.add('show');
		glassBox.classList.add('hidden');
		requestAnimationFrame(() => glassBox.classList.remove('hidden'));
	}

	function closeDeleteAccountModal() {
		const modal = document.getElementById('deleteAccountModal');
		const glassBox = modal.querySelector('.modal-glass-box');

		glassBox.classList.add('hidden');
		setTimeout(() => modal.classList.remove('show'), 300);
	}

	const deleteBtn = document.getElementById("delete-btn");
	deleteBtn.addEventListener('click', async () => {
		await openDeleteAccountModal().then(async () => {
			const confirmBtn = document.getElementById("confirmDltBtn");
			const cancelBtn = document.getElementById("cancelDltBtn");

			if (confirmBtn && cancelBtn) {
				confirmBtn.addEventListener('click', async () => {
					try {
						await deleteUserAccount();
						closeDeleteAccountModal();
						window.location.href = "/welcome";


					} catch (error) {
						console.error("Error deleting user Account:", error);
					} finally {
						closeDeleteAccountModal();
					}
				});

				cancelBtn.addEventListener('click', () => {
					closeDeleteAccountModal();
				});
			}
		});
	});

	//attach update password click

	const updatePwdBtn = document.getElementById("update-pwd-btn");
	updatePwdBtn.addEventListener('click', async () => {
		updateUserPassword();
	});

	//attach generate token click

	const tokenRegBtn = document.getElementById("regenerate-btn");
	if (tokenRegBtn) {
		tokenRegBtn.addEventListener('click', async () => {
			regenerateToken().then(() => loadToken());
		});
	}

}

document.addEventListener("DOMContentLoaded", () => {

	loadProfileDetails();
	loadToken();
	// Tab switching
	document.querySelectorAll(".tab-link").forEach(tab => {
		tab.addEventListener("click", () => {
			document.querySelectorAll(".tab-link").forEach(t => t.classList.remove("active"));
			tab.classList.add("active");

			document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));
			document.getElementById(tab.dataset.tab).classList.add("active");
		});
	});

	// Profile edit toggle
	const editBtn = document.getElementById("editProfileBtn");
	const cancelBtn = document.getElementById("cancelEditBtn");
	const viewSection = document.getElementById("profileView");
	const editSection = document.getElementById("profileEdit");

	editBtn?.addEventListener("click", () => {
		viewSection.style.display = "none";
		editSection.style.display = "block";
	});

	cancelBtn?.addEventListener("click", () => {
		editSection.style.display = "none";
		viewSection.style.display = "block";
	});

	attachClickHandlers();
});
