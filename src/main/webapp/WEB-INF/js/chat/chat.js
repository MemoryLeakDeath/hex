var chatWidget;

$(document).ready(function() {
	var channelName = $('.chatheader').data('channelname');
	chatWidget = new ChatClient(channelName, receiveMessage);
	
	$('#sendchatmessagebutton').on('click', sendMessage);	
});

function sendMessage() {
	var chatMessage = $('#chatmessageinput').val();
	chatWidget.send(chatMessage);	
}

function receiveMessage(messageObject) {
	var message = messageObject.message;
	var channelName = messageObject.channelName;
	var displayName = messageObject.details.senderDisplayName;
	var htmlMessage = `<div class="message" data-channelname="${channelName}">
	                   <span class="displayname"><strong>${displayName}:</strong></span><span>&nbsp;&nbsp;${message}</span>
	                   </div>`;
	$('.chatbox').append(htmlMessage);
}

