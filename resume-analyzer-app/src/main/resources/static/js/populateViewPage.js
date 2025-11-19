const { setupItemToggle, setupOpenFromRecent, setupSearch } = require("./viewpage");

export function populateViewPage(data, type) {
	// 1. Update header stats
	document.getElementById('totalCount').textContent = data.totalCount;
	document.getElementById('lastUploadedDate').textContent =
		data.lastUploadedDate || '—';

	// 2. Populate recent 5 uploads
	const recentContainer = document.getElementById('recentUploads');
	recentContainer.innerHTML = '';

	data.recent5Uploads.forEach(item => {
		const div = document.createElement('div');
		div.className = type === 'jd' ? 'jd-paper' : 'resume-paper';
		div.setAttribute(
			type === 'jd' ? 'data-jd-id' : 'data-resume-id',
			type === 'jd' ? item.jdId : item.resumeId
		);

		div.innerHTML = `
			<h4>${type === 'jd' ? item.title : item.fileName}</h4>
			<span>${item.companyName}</span>
			<span>${new Date(item.createdAt).toLocaleDateString()}</span>
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
			<div class="${type}-item-header">
				<h4>${type === 'jd' ? item.title : item.fileName}</h4>
				<span>${{ item, : .companyName } - { new: Date(item.createdAt).toLocaleDateString() }}</span>
			</div>
			<div class="${type}-item-content">
				<pre>${type === 'jd' ? item.jdText : 'Resume details TBD...'}</pre>
			</div>
		`;
		allContainer.appendChild(itemDiv);
	});

	// 4. Reattach behavior handlers
	if (type === 'jd') {
		setupItemToggle('.jd-item', '.jd-item-header');
		setupOpenFromRecent('.jd-paper', 'data-jd-id', '.jd-item');
		setupSearch('searchJD', 'filterCompany', 'filterTitle', '.jd-item');
	} else {
		setupItemToggle('.resume-item', '.resume-item-header');
		setupOpenFromRecent('.resume-paper', 'data-resume-id', '.resume-item');
		setupSearch('searchResume', 'filterCompany', 'filterTitle', '.resume-item');
	}
}
