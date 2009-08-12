package zildo.monde;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.TransferObject;

public class WaitingDialog {

	public String sentence;
	public int action;
	public boolean console;			// TRUE=message should be displayed in the console
	public TransferObject client;	// Only used for sending dialog to the right client. Unused by client side.
	
	public WaitingDialog(String p_sentence, int p_action, boolean p_console, TransferObject p_client) {
		sentence=p_sentence;
		action=p_action;
		console=p_console;
		client=p_client;
	}
	
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(sentence);
		p_buffer.put(action);
		p_buffer.put(console);
	}
	
	public static WaitingDialog deserialize(EasyBuffering p_buffer) {
		String s=p_buffer.readString();
		int act=p_buffer.readInt();
		boolean console=p_buffer.readBoolean();
		return new WaitingDialog(s, act, console, null);
	}
}
