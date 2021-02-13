package ziotbroker.packet;

/**
 * Object representation of MQTT packet.
 *
 * @author Jakub Svarc
 */
public abstract class Packet {

    /**
     * Type of the packet.
     */
    private final PacketType type;
    /**
     * Flags of the packet represented by boolean array.
     */
    //private final boolean[] flags;
    /**
     * Length of variable headers and payload of the packet.
     */
    //private final int remainingLength;
    /**
     * Packet type specific headers.
     */
    //private final byte[] variableHeaders;
    /**
     * Content of the packet.
     */
    //private final String payload;
    
    /**
     * Builds MQTT packet.
     * 
     * @param type type of the packet
     */
    public Packet(PacketType type) {
        this.type = type;
    }
    
    /**
     * Builds binary representation of the packet.
     * 
     * @return binary representation of the packet
     */
    public abstract byte[] buildBinaryPacket();

    /**
     * Returns type of the packet.
     * 
     * @return type of the packet
     */
    public PacketType getType() {
        return type;
    }
    
}
