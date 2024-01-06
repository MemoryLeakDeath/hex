class ChatClient {
	constructor(channelName, messageCallback) {
		this.channelName = channelName;
		this.messageCallback = messageCallback;
		this.#init();
	}
	
	#init() {
		this.wsClient = new StompJs.Client({brokerURL: 'wss://localhost:8443/hex-websocket'});
		this.#logErrors();
		this.connect();
	}
	
	connect() {		
		this.wsClient.onConnect = () => {
			this.subscription = this.wsClient.subscribe(`/topic/chat/${this.channelName}`, (message) => {
				this.messageCallback(JSON.parse(message.body));
			});
			console.log(`Subscribed to chat: ${this.channelName}`);
		};
		this.wsClient.activate();
	}
	
	disconnect() {
		this.subscription.unsubscribe();
		this.wsClient.deactivate();
		console.log(`Unsubscribed and disconnected from chat: ${this.channelName}`);
	}
	
	#logErrors() {
		this.wsClient.onWebSocketError = (error) => {
		    console.error('Error with websocket', error);
		};
		
		this.wsClient.onStompError = (frame) => {
		    console.error('Broker reported error: ' + frame.headers['message']);
		    console.error('Additional details: ' + frame.body);
		};		
	}
		
	send(message) {
		this.wsClient.publish({destination: `/hex/${this.channelName}/sendChatMessage`, body: JSON.stringify({message: message})});
	}
}
