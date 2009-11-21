package zildo.fwk.net.packet;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.Packet;

/**
 * Meaning of this packet:
 * 
 * The client need one resource. So he send this packet to the server to ask for the desired one.
 * @author tchegito
 *
 */
public class AskPacket extends Packet {

    public enum ResourceType {
    	MAP, MAP_PART, ENTITY, KEYBOARD, SOUND, DIALOG, CLIENTINFO; // Note: Keyboard, Dialog, Map part and Sound are never asked, only sent.		
		public static ResourceType fromString(String p_string) {
			for (ResourceType rType : ResourceType.values()) {
				if (rType.toString().equals(p_string)) {
					return rType;
				}
			}
			throw new RuntimeException(p_string+" is not a valid resource type");
		}

	}
	
	public ResourceType resourceType;
    public boolean entire; // TRUE=client want entire entities list (just for ENTITY resource type)

    /**
     * Empty constructor (called by {@link Packet#receive(java.nio.ByteBuffer)})
     */
    public AskPacket() {
        super();
    }

    public AskPacket(ResourceType p_type, boolean p_entire) {
        super();
        resourceType = p_type;
        entire = p_entire;
    }

    @Override
    protected void buildPacket() {
        b.put(resourceType.toString());
        b.put(entire);
    }

    @Override
    protected void deserialize(EasyBuffering p_buffer) {
        String resType = p_buffer.readString();
        resourceType = ResourceType.fromString(resType);
        entire = p_buffer.readBoolean();
    }
}