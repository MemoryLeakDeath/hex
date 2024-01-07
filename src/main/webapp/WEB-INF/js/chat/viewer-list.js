
$('body').on('htmx:afterSettle', showViewerList);

function showViewerList(evt) {
	var targetId = $(evt.detail.target).attr('id');
	if(targetId === "viewer-list") {
		$('#viewer-list').toggleClass('d-none');
	}
}