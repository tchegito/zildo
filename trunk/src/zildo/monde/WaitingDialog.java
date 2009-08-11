package zildo.monde;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.TransferObject;

public class WaitingDialog {

	public String sentence;
	public int action;
	public TransferObject client;	// Only used for sending dialog to the right client. Unused by client side.
	
	public WaitingDialog(String p_sentence, int p_action, TransferObject p_client) {
		sentence=p_sentence;
		action=p_action;
		client=p_client;
	}
	
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(sentence);
		p_buffer.put(action);
	}
	
	public static WaitingDialog deserialize(EasyBuffering p_buffer) {
		String s=p_buffer.readString();
		int act=p_buffer.readInt();
		return new WaitingDialog(s, act, null);
	}
}
