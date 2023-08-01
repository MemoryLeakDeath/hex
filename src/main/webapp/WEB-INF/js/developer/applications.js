$(document).ready(function() {
	$('#newapplicationbutton').on('click', handleNewApplication);
	$('.viewclientidbutton').on('click', handleViewClientIdToggle);
	$('.copyclientidbutton').on('click', handleCopyClientId);
	$('.viewclientsecretbutton').on('click', handleViewClientSecretToggle);
	$('.copyclientsecretbutton').on('click', handleCopyClientSecret);
	$('.regenerateclientsecretbutton').on('click', handleRegenerateClientSecret);
	$('.deleteclientbutton').on('click', handleDeleteClient);
	$('.editclientbutton').on('click', handleEditClient);
	$('.changeclientsecretbutton').on('click', handleChangeClientSecret);
});

function handleNewApplication() {
	$('#newapplicationform').submit();
}

function handleViewClientIdToggle() {
	var id = $(this).data('id');
	var clientIdSpan = $('#clientidtext-' + id);
	var buttonIcon = $(this).find('i');
	toggleBlurText(clientIdSpan, buttonIcon);
}

function handleCopyClientId() {
	var id = $(this).data('id');
	var errorMsg = $(this).data('errormsg');
	var successMsg = $(this).data('successmsg');
	var clientId = $('#clientidtext-' + id).val();
	copyToClipboard(clientId, successMsg, errorMsg);
}

function handleViewClientSecretToggle() {
	var id = $(this).data('id');
	var clientSecretSpan = $('#clientsecrettext-' + id);
	var buttonIcon = $(this).find('i');
	toggleBlurText(clientSecretSpan, buttonIcon);
}

function handleCopyClientSecret() {
	var id = $(this).data('id');
	var errorMsg = $(this).data('errormsg');
	var successMsg = $(this).data('successmsg');
	var clientSecret = $('#clientsecrettext-' + id).val();
	copyToClipboard(clientSecret, successMsg, errorMsg);
}

function handleRegenerateClientSecret() {
	var id = $(this).data('id');
	$('#regenerateclientsecretidinput').val(id);
	$('#regenerateclientsecretform').submit();
}

function handleDeleteClient() {
	var id = $(this).data('id');
	var confirmMsg = $(this).data('confirm');
	if(confirm(confirmMsg)) {
		$('#deleteapplicationid').val(id);
		$('#deleteapplicationform').submit();		
	}
}

function handleEditClient() {
	var id = $(this).data('id');
	$('#editapplicationid').val(id);
	$('#editapplicationform').submit();		
}

function handleChangeClientSecret() {
	var id = $(this).data('id');
	$('#changeclientsecretid').val(id);
	$('#changeclientsecretform').submit();
}
