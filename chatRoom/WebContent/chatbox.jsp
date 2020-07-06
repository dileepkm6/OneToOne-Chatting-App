<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>chatbox</title>
</head>
<body>
<h3>Username</h3>
<textarea id="messageTextArea" rows="10" cols="45"></textarea>
<input type="text" id="messageText">
<input type="button" value="send" onclick="sendMessage()">
<p id="from">:<p id="status"></p></p>
<script type="text/javascript">
var websocket= new WebSocket("ws://localhost:8080/chatRoom/chatEndPoint");
websocket.onmessage = function processMessage(message)
{
	var jsonData = JSON.parse(message.data);
	if(jsonData.message!=null)
		{
			messageTextArea.value+=jsonData.message;
		}
}
funtion sendMessage()
{
	websocket.send(messageText.value);
	messageText.value="\";
	}
</script>
</body>
</html>