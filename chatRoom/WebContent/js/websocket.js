/**
 * 
 */

// "RESPONSE~CHAT|FROM~" + from + "|TO~" + to + "|MSG~" + actualMessage; //
// formatting msg
function sendMessage(to, from) {
	if (!to) {
		to = document.getElementById("to").innerHTML;
	}
	action = "ACTION~CHAT|FROM~" + from + "|TO~" + to + "|MSG~";
	message = document.getElementById("messageText").value;
	messageToSend = action + message;
	websocket.send(messageToSend);

	var tag = document.createElement("div");
	tag.classList.add("message");
	tag.classList.add("messageSender");
	var text = document.createTextNode(message);
	tag.appendChild(text);
	var element = document.getElementById("chat");
	element.appendChild(tag);
	document.getElementById("messageText").value = "";
}

function sendMessageForSeller(from) {
	to = document.getElementById("to").innerHTML;
	if (to != null && to.trim() != "") {
		action = "ACTION~CHAT|FROM~" + from + "|TO~" + to + "|MSG~";
		message = document.getElementById("messageText").value;
		messageToSend = action + message;
		websocket.send(messageToSend);

		var tag = document.createElement("div");
		tag.classList.add("message");
		tag.classList.add("messageSender");
		var text = document.createTextNode(message);
		tag.appendChild(text);
		var element = document.getElementById("chat");
		element.appendChild(tag);
	}
	document.getElementById("messageText").value = "";
}
function toggleChatBox() {
	var nodes = document.getElementById("chat").getElementsByTagName('*');
	for (var i = 0; i < nodes.length; i++) {
		nodes[i].hidden = false;
	}
}
