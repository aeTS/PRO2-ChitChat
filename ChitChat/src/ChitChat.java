public class ChitChat {

	public static void main(String[] args) {
		ChatFrame chatFrame = new ChatFrame();
		UsersRobot robot = new UsersRobot(chatFrame);
		robot.activate();
		chatFrame.pack();
		chatFrame.setVisible(true);
	}

}
