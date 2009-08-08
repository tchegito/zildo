package zildo.monde;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.TransferObject;

public class WaitingDialog {

	public String sentence;
	public TransferObject client;
	
	public WaitingDialog(String p_sentence, TransferObject p_client) {
		sentence=p_sentence;
		client=p_client;
	}
	
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(sentence);
	}
	
	public static WaitingDialog deserialize(EasyBuffering p_buffer) {
		String s=p_buffer.readString();
		return new WaitingDialog(s, null);
	}
}
