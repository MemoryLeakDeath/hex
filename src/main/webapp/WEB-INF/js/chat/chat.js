var chatWidget;

$(document).ready(function() {
	var channelName = $('.chatheader').data('channelname');
	chatWidget = new ChatClient(channelName, receiveMessage);
	
	$('#sendchatmessagebutton').on('click', sendMessage);	
});

$(window).on('beforeunload', disconnectChat);

function sendMessage() {
	var chatMessage = $('#chatmessageinput').val();
	chatWidget.send(chatMessage);	
}

function receiveMessage(messageObject) {
	var message = messageObject.message;
	var channelName = messageObject.channelName;
	var displayName = messageObject.details.senderDisplayName;
	var eventType = messageObject.details.eventType;
	var htmlMessage = '';
	if(eventType === "FOLLOW" || eventType === "SUBSCRIBE" || eventType === "GIFT") {
		htmlMessage = `<div class="event" data-channelname="${channelName}" data-eventtype="${eventType}">
	                      <p>${message}</p>
	                   </div>`;
	} else {
		htmlMessage = `<div class="message" data-channelname="${channelName}">
	                   <span class="displayname"><strong>${displayName}:</strong></span><span>&nbsp;&nbsp;${message}</span>
	                   </div>`;		
	}
	$('.chatbox').append(htmlMessage);
}

function disconnectChat() {
	chatWidget.disconnect();
}

