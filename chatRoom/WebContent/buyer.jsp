
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>chat</title>
<script src="js/websocket.js"></script>
</head>
<head>
<link
	href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
	rel="stylesheet" id="bootstrap-css">
<script
	src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script
	src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css"
	type="text/css" rel="stylesheet">
<script src="js/websocket.js"></script>
<link rel="stylesheet" type="text/css" href="css/chat.css" />
</head>
<body>
<img style="margin:50px;border-radius: 10px" width="500" height="500" src="https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg">
	<% String from = (String)request.getAttribute("username");
			   String to =(String)request.getAttribute("to"); 
			%>
	<div class="center">
		<div class="contacts">
			<h2>Contacts</h2>
			<div class="contact" onclick="toggleChatBox()">
				<div class="pic rogers" alt="Avatar"></div>
				<div id="to" class="name">
					<% out.print(to); %>
				</div>
				<div id="recentMessage" class="message"></div>
			</div>
		</div>
		<div class="chat">
			<div class="contact bar">
				<div class="pic stark" alt="Avatar"></div>
				<div class="name">
					<% out.print(to); %>
				</div>
				<div id="status" style="color: red;" class="seen">offline</div>
			</div>
			<div class="messages" id="chat"></div>
			<div class="input">
				<input id="messageText" placeholder="Type your message here!"
					type="text" /><input type="button" value="send"
					onclick="sendMessage('<%=to%>','<%=from%>')">
			</div>
		</div>
	</div>
	<script type="text/javascript">
		var websocket = new WebSocket("ws://localhost:8080/chatRoom/chatApp");
		websocket.onmessage = function processMessage(message) {
			var jsonData = JSON.parse(message.data);
			var to = document.getElementById("to").innerHTML;
			if (jsonData.message != null) {
				action = extractAction(jsonData.message);
				if (action == "CHAT") {

					from = extractFrom(jsonData.message);
					to = extractTo(jsonData.message);
					extractedMessage = extractMessage(jsonData.message);

					var tag = document.createElement("div");
					tag.classList.add("message");
					var text = document.createTextNode(extractedMessage);
					tag.appendChild(text);
					var element = document.getElementById("chat");
					element.appendChild(tag);
					document.getElementById("recentMessage").innerHTML=extractedMessage;
				} else if (jsonData.message.includes('JOINED')) {
					username = jsonData.message.split('~')[1];
					if (username.trim() == to.trim()) {
						var x = document.getElementById("status");
						x.innerHTML = " online"
						x.style.color = "green";
					}

				} else if (jsonData.message.includes('CLOSED')) {
					username = jsonData.message.split('~')[1];
					if (username.trim() == to.trim()) {
						var x = document.getElementById("status");
						x.innerHTML = " offline"
						x.style.color = "red";
					}
				}

			}

		}
		function extractFrom(message) {
			words = message.split('|')[1];
			from = words.split('~')[1];
			return from;
		}
		function extractTo(message) {
			words = message.split('|')[2];
			to = words.split('~')[1];
			return to;
		}
		function extractMessage(message) {
			words = message.split('|')[3];
			message = words.split('~')[1];
			return message;
		}
		function extractAction(message) {
			words = message.split('|')[0];
			action = words.split('~')[1];
			return action;
		}
	</script>
</body>
</html>