import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.BadLocationException;

import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class UsersRobot extends TimerTask {
	private ChatFrame chat;
	
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 3000, 3000);
	}
	
	public UsersRobot(ChatFrame chat) {
		this.chat = chat;
	}
	
	@Override
	public void run() {
		try {String responseBody = Request.Get("http://chitchat.andrej.com/users")
	    		   .execute()
	    		   .returnContent()
	    		   .asString();
	       		ObjectMapper mapper = new ObjectMapper();
	       		mapper.setDateFormat(new ISO8601DateFormat());
	       		
	       		TypeReference<List<Uporabnik>> t = new TypeReference<List<Uporabnik>>() { };
	    		List<Uporabnik> sodelujoci = mapper.readValue(responseBody, t);
	    		chat.pocistiSodelujoce();
	    		
	    		for (Uporabnik oseba : sodelujoci) {
	    			chat.zapisiSodelujocega(oseba);
	    		}

	    		
	       } catch (IOException e) {
	           e.printStackTrace();
	       } catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}
}

