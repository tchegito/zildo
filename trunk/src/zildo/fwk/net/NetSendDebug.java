package zildo.fwk.net;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.net.packet.AskPacket;
import zildo.fwk.net.packet.GetPacket;

public class NetSendDebug extends NetSend {

    String filename;
    EasyBuffering logBuffer;
    DateFormat df=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
    
    public NetSendDebug(InetAddress p_address, int p_port) {
	super(p_address, p_port);
	
	
	// Create the file name
	boolean server=this.getClass().equals(NetServer.class);
	filename=p_address == null ? "localhost" : p_address.getHostName()+"_";
	filename+=server ? "server" : "client";
	filename+=".log";
	
	logBuffer=new EasyBuffering(400000);
    }

    /**
     * Receive all packets
     * 
     * @return List<Packet>
     */
    public PacketSet receiveAll() {
	PacketSet set=super.receiveAll();
	
	for (Packet p : set) {
	    logPacket(true, p, p.getSource());
	}
	return set;
    }

    /**
     * Send the packet to target. If target is null, we broadcast.
     * 
     * @param p_packet
     * @param p_object
     */
    public void sendPacket(Packet p_packet, TransferObject p_target) {
	super.sendPacket(p_packet, p_target);

	logPacket(false, p_packet, p_target);
    }
    
    private void logPacket(boolean p_in, Packet p_packet, TransferObject p_target) {
	String targetStr=p_target == null ? "broadcast" : p_target.address.toString();
	String sens=p_in ? "<<" : ">>";
	int size=p_packet.getSize();
	String complement="";
	switch (p_packet.getType()) {
	case GET_RESOURCE:
	    complement+="+"+((GetPacket)p_packet).resourceType;
	    break;
	case ASK_RESOURCE:
	    complement+="+"+((AskPacket)p_packet).resourceType;
	    break;
	} 
	logBuffer.put(" "+df.format(new Date()) + " " + p_packet.getType()+complement+" "+sens+" "+targetStr+" size="+size+"\n");
    }

    @Override
    public void close() {
	super.close();

	// Save log file
	new EasyWritingFile(logBuffer).saveFile(filename);
    }
}
