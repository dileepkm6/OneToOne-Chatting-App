package chatRoom;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/chatEndPoint", configurator=ChatServerConfigurator.class)
public class ChatEndPoint {
	static Set<Session> chatSession = Collections.synchronizedSet(new HashSet<Session>());
	@OnOpen
    public void onOpen( EndpointConfig endpointConfig, Session session) {
		String userName=(String) endpointConfig.getUserProperties().get("username");
        if (Objects.isNull(userName)) {
            throw new RegistrationFailedException("User name is required");
        } else {
            session.getUserProperties().put(
"username", userName);
            chatSession.add(session);
        }
    }

    @OnError
    public void onError(final Session session, final Throwable throwable) {
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    	String userName=(String) session.getUserProperties().get("username");
    	if(userName!=null)
    	{
    		chatSession.stream().forEach(s ->
    		{
    			try
    			{
    				s.getBasicRemote().sendText(buildJsonData(userName,message));
    			}
    			catch(Exception e) {e.printStackTrace();}
    		});
    	}
    }

    @OnClose
    public void onClose( Session session) {
        chatSession.remove(session);
    }

    private static final class RegistrationFailedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public RegistrationFailedException(final String message) {
            super(message);
        }
    }
    
    private String buildJsonData(String username,String message)
    {
    	JsonObject jsonObject=Json.createObjectBuilder().add("message", username+": "+message).build();
    	StringWriter stringWriter= new StringWriter();
    	try(JsonWriter jsonWriter = Json.createWriter(stringWriter))
    	{
    		jsonWriter.write(jsonObject);
    	}
    	return stringWriter.toString();
    	
    }
}

