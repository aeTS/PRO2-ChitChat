import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.BadLocationException;

public class ReceiveRobot extends TimerTask {
	private ChatFrame chat;
	
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 3000, 2000);
	}
	
	public ReceiveRobot(ChatFrame chat) {
		this.chat = chat;
	}
	
	@Override
	public void run() {
		try {
			chat.sprejmiSporocila();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		}

}
