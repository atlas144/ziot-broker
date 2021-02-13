package ziotbroker;

import ziotbroker.packet.Packet;
import ziotbroker.packet.PacketType;
import java.nio.charset.Charset;

/**
 * Supporting class which serves to build packets.
 *
 * @author Jakub Svarc
 */
public class PacketBuilder {
    
    /**
     * Builds <b>CONNACK</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>CONNACK</b> packet
     */
    public static Packet buildConnackPacket() {
        // TODO Will be implemented in the future
        return null;
    }
    
    /**
     * Builds <b>PUBLISH</b> packet for QoS 0. It means that there is no packet
     * ID field in variable headers.
     * 
     * @param flags flags part of control header
     * @param topic topic of the message
     * @param payload the massage itself
     * @return MQTT <b>PUBLISH</b> packet
     */
    public static Packet buildPublishPacket(boolean[] flags, String topic, String payload) {
        /**
         * Topic encoded as byte array.
         */
        byte[] binaryTopic = topic.getBytes(Charset.forName("UTF-8"));
        /**
         * Length of encoded topic.
         */
        short binaryTopicLength = (short) binaryTopic.length;
        /**
         * Length of encoded topic represented by byte array.
         */
        byte[] encodedBinaryTopicLength = new byte[2];
        /**
         * Variable headers packet field (payload not included).
         */
        byte[] variableHeaders = new byte[binaryTopicLength + 2];
        /**
         * Length of variable headers and payload.
         */
        int remainingLength = 2 + binaryTopicLength + payload.length();

        encodedBinaryTopicLength[0] = (byte)((binaryTopicLength >> 8) & 255);
        encodedBinaryTopicLength[1] = (byte)(binaryTopicLength & 255);

        System.arraycopy(encodedBinaryTopicLength, 0, variableHeaders, 0, encodedBinaryTopicLength.length);
        System.arraycopy(binaryTopic, 0, variableHeaders, encodedBinaryTopicLength.length, binaryTopic.length);

        return new Packet(PacketType.PUBLISH, flags, remainingLength, variableHeaders, payload);
    }
    
    /**
     * Builds <b>PUBLISH</b> packet for QoS 1 and 2 (Includes packet ID). -
     * <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param flags flags part of control header
     * @param topic topic of the message
     * @param packetID ID of the packet
     * @param payload the massage itself
     * @return MQTT <b>PUBLISH</b> packet
     */
    public static Packet buildPublishPacket(boolean[] flags, String topic, short packetID, String payload) {
        // TODO Will be implemented along with QoS 1 and 2
        return null;
    }
    
    /**
     * Builds <b>PUBACK</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>PUBACK</b> packet
     */
    public static Packet buildPubackPacket() {
        // TODO Will be implemented along with QoS 1 and 2
        return null;
    }
    
    /**
     * Builds <b>PUBREC</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>PUBREC</b> packet
     */
    public static Packet buildPubrecPacket() {
        // TODO Will be implemented along with QoS 1 and 2
        return null;
    }
    
    /**
     * Builds <b>PUBREL</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>PUBREL</b> packet
     */
    public static Packet buildPubrelPacket() {
        // TODO Will be implemented along with QoS 1 and 2
        return null;
    }
    
    /**
     * Builds <b>PUBCOMP</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>PUBCOMP</b> packet
     */
    public static Packet buildPubcompPacket() {
        // TODO Will be implemented along with QoS 1 and 2
        return null;
    }
        
    /**
     * Builds <b>SUBACK</b> packet as response for SUBSCRIBE packet with given
     * packet ID.
     * 
     * @param packetIdentifier ID of corresponding SUBSCRIBE packet
     * @param subscribtionReturnCodes bytes containing QoS of accepted 
     * subscriptions (if subscription failed, corresponding byte must contain 
     * value 128)
     * @return MQTT <b>SUBACK</b> packet
     */
    public static Packet buildSubackPacket(byte[] packetIdentifier, byte[] subscribtionReturnCodes) {
        boolean[] flags = {false, false, false, false};
        int remainingLength = packetIdentifier.length + subscribtionReturnCodes.length;
        
        return new Packet(PacketType.SUBACK, flags, remainingLength, packetIdentifier, new String(subscribtionReturnCodes, Charset.forName("UTF-8")));
    }
    
    /**
     * Builds <b>UNSUBACK</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>UNSUBACK</b> packet
     */
    public static Packet buildUnsubackPacket() {
        // TODO Will be implemented in the future
        return null;
    }
    
    /**
     * Builds <b>PINGRESP</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @return MQTT <b>PINGRESP</b> packet
     */
    public static Packet buildPingrespPacket() {
        // TODO Will be implemented in the future
        return null;
    }
    
}
