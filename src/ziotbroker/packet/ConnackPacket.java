package ziotbroker.packet;

import java.nio.charset.Charset;
import ziotbroker.MQTTException;
import ziotbroker.MQTTExceptionLevel;

/**
 * Object representation of CONNACK packet.
 *
 * @author Jakub Svarc
 */
public class ConnackPacket extends Packet {
    
    /**
     * Charset used for encoding strings in packet (for MQTT 3.1.1 it is UTF-8).
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");
    
    /**
     * Indicates if session with corresponding ID already exists on the server
     * and if should be stored.
     */
    private final boolean sessionPresent;
    /**
     * Code indicating if the connection was established succesfully.
     */
    private final byte connectReturnCode;
    
    /**
     * Basic constructor used to build CONNACK packet object with specific 
     * connect return code.
     * 
     * @param sessionPresent indicates if session with corresponding ID already
     * exists on the server and if should be stored
     * @param connectReturnCode code indicating if the connection was 
     * established succesfully
     */
    private ConnackPacket(boolean sessionPresent, byte connectReturnCode) {       
        super(PacketType.CONNACK);
        this.sessionPresent = sessionPresent;
        this.connectReturnCode = connectReturnCode;
    }
    
    /**
     * Basic constructor used to build CONNACK packet object with connect return
     * code 0.
     * 
     * @param sessionPresent indicates if session with corresponding ID already
     * exists on the server and if should be stored
     */
    private ConnackPacket(boolean sessionPresent) {
        this(sessionPresent, (byte) 0);
    }
    
    /**
     * Builds CONNACK packet object from binary representation of the packet.
     * 
     * @param controlHeader first byte of the packet (contains packet type and
     * flags)
     * @param remainingHeaders binary representation of variable headers
     * @throws MQTTException thrown if packet data are corrupted
     */
    public ConnackPacket(byte controlHeader, byte[] remainingHeaders) throws MQTTException {       
        super(PacketType.CONNECT);
        
        if (controlHeader != 0b00100000) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Packet controll header mallformed!");
        }
        
        if (remainingHeaders.length != 2) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Variable headers length is " + remainingHeaders.length + ", but must be 2");
        }
        
        if (remainingHeaders[0] > 0b00000001) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Connect acknowledge flag is malformed, value must be 0 or 1, bur is " + remainingHeaders.length);
        }
        
        sessionPresent = remainingHeaders[0] == 1;
        connectReturnCode = remainingHeaders[1];
    }

    @Override
    public byte[] buildBinaryPacket() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns value of the session present flag.
     * 
     * @return value of the session present flag
     */
    public boolean getSessionPresent() {
        return sessionPresent;
    }

    /**
     * Returns value of the connect return code.
     * 
     * @return value of the connect return code
     */
    public short getConnectReturnCode() {
        return connectReturnCode;
    }
    
}
