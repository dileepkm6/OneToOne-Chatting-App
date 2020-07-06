package chatRoom;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

//declaring server endpoint with query parameter
@ServerEndpoint(value = "/chatApp", configurator = ChatServerConfigurator.class)
public class OneToOneChat {
	// a HashMap to store the online users with their session id and username
	static HashMap<String, String> usersMap = new HashMap<String, String>();
	// a set to store the session parameters of all connected clients
	private static final Set<OneToOneChat> connections = new CopyOnWriteArraySet<>();
	private String username;
	private Session session;

	public OneToOneChat() {
		username = null;
	}

	@OnOpen
	public void start(EndpointConfig endpointConfig, Session session) {
		String userName = (String) endpointConfig.getUserProperties().get("username");
		this.session = session;
		connections.add(this); // adding this session to connections set
		this.username = userName.trim();
		addUserInMap(session.getId(), username); // adding username and session id in hashmap
		for (String key : usersMap.keySet()) {
			newJoinUpdateAll(usersMap.get(key)); // broadcasting new user joined notification to all connected clients
		}
	}

	@OnClose
	public void end(Session session) {
		connections.remove(this); // removing the session from connections set
		removeUserInMap(session.getId(), username); // removing the username and session id from hashmap
		closeUpdateAll(username); // broadcasting user closed notification to all connected clients
	}

	@OnMessage
	public void incoming(Session session, String message) {
		String action = extractAction(message); // extracting action from user's msg
		switch (action) {
		case "GET_USERS_LIST":
			String usersList = getOnlineUsersList(); // getting list of all online users
			broadcast(usersList, session.getId()); // broadcasting this list to connected client
			break;
		case "CHAT":
			String from = extractFrom(message).trim(); // extracting sender
			String to = extractTo(message).trim(); // extracting recipient
			String actualMessage = extractActualMessage(message).trim(); // extracting actual message
			sendMessageToUser(to, from, actualMessage); // sending this message to recipient
			break;
		default:
			break;
		}
	}

	private void sendMessageToUser(String to, String from, String actualMessage) {
		String toSessionId = getSessionIdOfUser(to); // getting sessionid of recipient
		String messageToSend = prepareMessage(to, from, actualMessage); // preparing proper format of msg
		broadcast(messageToSend, toSessionId); // sending the message to recipient
	}

	private void broadcast(String messageToSend, String toSessionId) {
		for (OneToOneChat client : connections) {
			try {
				synchronized (client) {
					// comparing the session id
					if (client.session.getId().equals(toSessionId) && this != client) {
						client.session.getBasicRemote().sendText(buildJsonData(messageToSend)); // send message to the
																								// user
					}
				}
			} catch (IOException e) {
				connections.remove(client);
				try {
					client.session.close();
				} catch (IOException e1) {
				}
				String message = String.format("* %s %s", client.username, "has been disconnected.");
				broadcast(message);
			}
		}
	}

	private static void broadcast(String msg) {
		for (OneToOneChat client : connections) {
			try {
				synchronized (client) {
					client.session.getBasicRemote().sendText(buildJsonData(msg)); // broadcasting to all connected
																					// clients
				}
			} catch (IOException e) {
				connections.remove(client);
				try {
					client.session.close();
				} catch (IOException e1) {
				}
				String message = String.format("* %s %s", client.username, "has been disconnected.");
				broadcast(message);
			}
		}
	}

	private String prepareMessage(String to, String from, String actualMessage) {
		String msg = "RESPONSE~CHAT|FROM~" + from + "|TO~" + to + "|MSG~" + actualMessage; // formatting msg
		return msg;
	}

	// retrieving the session id of user
	private String getSessionIdOfUser(String to) {
		if (usersMap.containsValue(to)) {
			for (String key : usersMap.keySet()) {
				if (usersMap.get(key).equals(to)) {
					return key;
				}
			}
		}
		return null;
	}

	private void newJoinUpdateAll(String user) {
		String message = String.format("%s%s", "JOINED~", user); // formatting msg
		broadcast(message); // broadcasting this message to all connected clients
	}

	private void addUserInMap(String id, String username) {
		usersMap.put(id, username); // adding session id and username to hashmap
	}

	private void removeUserInMap(String id, String user) {
		usersMap.remove(id); // removing session id and username from hashmap
	}

	private void closeUpdateAll(String user) {
		String message = String.format("%s%s", "CLOSED~", user); // formatting msg
		broadcast(message); // broadcasting this message to all connected clients
	}

	private String extractActualMessage(String message) {
		String[] firstSplit = message.split("\\|");
		String[] secondSplit = firstSplit[3].split("~");
		String match = "MSG";
		if (secondSplit[0].equals(match)) {
			return secondSplit[1];
		}
		return null;
	}

	private String extractTo(String message) {
		String[] firstSplit = message.split("\\|");
		String[] secondSplit = firstSplit[2].split("~");
		String match = "TO";
		if (secondSplit[0].equals(match)) {
			return secondSplit[1];
		}
		return null;
	}

	private String extractFrom(String message) {
		String[] firstSplit = message.split("\\|");
		String[] secondSplit = firstSplit[1].split("~");
		String match = "FROM";
		if (secondSplit[0].equals(match)) {
			return secondSplit[1];
		}
		return null;
	}
//	"ACTION~CHAT|FROM~" + from + "|TO~" + to + "|MSG~";
	private String extractAction(String message) {
		String[] firstSplit = message.split("\\|");
		String[] secondSplit = firstSplit[0].split("~");
		String match = "ACTION";
		if (secondSplit[0].equals(match)) {
			return secondSplit[1];
		}
		return null;
	}

	// getting list of users from hashmap
	private String getOnlineUsersList() {
		String usersList = new String();
		for (Entry<String, String> m : usersMap.entrySet()) {
			String iUser = m.getValue();
			if (usersList.toLowerCase().contains(iUser.toLowerCase())) {
				continue;
			} else {
				usersList = iUser + "|" + usersList;
			}
		}
		usersList = "ONLINEUSERS~" + usersList;
		return usersList;
	}

	private static String buildJsonData(String message) {
		JsonObject jsonObject = Json.createObjectBuilder().add("message", message).build();
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.write(jsonObject);
		}
		return stringWriter.toString();

	}
}
