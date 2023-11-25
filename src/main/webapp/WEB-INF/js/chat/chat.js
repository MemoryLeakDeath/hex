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
	var htmlMessage = `<p class="message" data-channelname="${channelName}">${message}</p>`;
	$('.chatbox').append(htmlMessage);
}

