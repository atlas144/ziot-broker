package ziotbroker;

import ziotbroker.packet.PacketType;
import ziotbroker.packet.Packet;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Supporting class which serves to parse incoming bytes to packets 
 * and outcoming packet objects to bytes.
 *
 * @author Jakub Svarc
 */
public class PacketParser {
    
    /**
     * Parses control header byte of the packet to get its type.
     * 
     * @param controlHeader control header byte of the packet
     * @return type of the packet
     * @throws MQTTException thrown if control header byte value is malformed
     */
    private static PacketType getPacketType(byte controlHeader) throws MQTTException {
        byte packetTypeByte = (byte) ((controlHeader >>> 4) & 15);
        
        switch (packetTypeByte) {
            case 1: return PacketType.CONNECT;
            case 2: return PacketType.CONNACK;
            case 3: return PacketType.PUBLISH;
            case 4: return PacketType.PUBACK;
            case 5: return PacketType.PUBREC;
            case 6: return PacketType.PUBREL;
            case 7: return PacketType.PUBCOMP;
            case 8: return PacketType.SUBSCRIBE;
            case 9: return PacketType.SUBACK;
            case 10: return PacketType.UNSUBSCRIBE;
            case 11: return PacketType.UNSUBACK;
            case 12: return PacketType.PINGREQ;
            case 13: return PacketType.PINGRESP;
            case 14: return PacketType.DISCONNECT;
            default: throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed packet type field");
        }
    }
    
    /**
     * Builds control header byte from type of the packet. WARNING - flags 
     * fields of the control header byte needs to be set separately.
     * 
     * @param packetType type of the packet
     * @return control header byte (without flags)
     * @throws MQTTException thrown if packet type is unknown
     */
    private static byte getBinaryPacketType(PacketType packetType) throws MQTTException {        
        switch (packetType) {
            case CONNECT: return (byte) 1 << 4;
            case CONNACK: return (byte) 2 << 4;
            case PUBLISH: return (byte) 3 << 4;
            case PUBACK: return (byte) 4 << 4;
            case PUBREC: return (byte) 5 << 4;
            case PUBREL: return (byte) 6 << 4;
            case PUBCOMP: return (byte) 7 << 4;
            case SUBSCRIBE: return (byte) (8 << 4);
            case SUBACK: return (byte) (9 << 4);
            case UNSUBSCRIBE: return (byte) (10 << 4);
            case UNSUBACK: return (byte) (11 << 4);
            case PINGREQ: return (byte) (12 << 4);
            case PINGRESP: return (byte) (13 << 4);
            case DISCONNECT: return (byte) (14 << 4);
            default: throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed packet type field");
        }
    }
    
    /**
     * Parses control header byte of the packet to get flags array.
     * 
     * @param controlHeader control header byte of the packet
     * @return array of flags represented by boolean
     */
    private static boolean[] getFlags(byte controlHeader) {
        byte multiplier = 1;
        boolean[] flags = new boolean[4];
        
        for (byte i = (byte) (flags.length - 1); i >= 0; i--) {
            flags[i] = (controlHeader & multiplier) != 0;
            multiplier *= 2;
        }
        
        return flags;
    }
    
    /**
     * Builds control header byte from flags array. WARNING - packet type
     * field of the control header byte needs to be set separately.
     * 
     * @param flags array of flags represented by boolean
     * @return control header byte (without packet type)
     */
    private static byte getBinaryFlags(boolean[] flags) {
        byte binaryFlags = 0;
        byte multiplier = 1;
        
        for (byte i = (byte) (flags.length - 1); i >= 0; i--) {
            binaryFlags += flags[i] ? multiplier : 0;
            multiplier *= 2;
        }
        
        return binaryFlags;
    }

    /**
     * Returns remaining length of the packet (length of variable headers + length of payload).
     * 
     * @param packetLengthRemainingHeader packet remaining legth bytes
     * @return length of variable headers + length of payload
     */
    public static int getPacketRemainingLength(ArrayList<Integer> packetLengthRemainingHeader) {
        int packetRemainingLength = 0, multiplier = 1;
            
        for (int i = 0; i < packetLengthRemainingHeader.size(); i++) {
            packetRemainingLength += (int) (packetLengthRemainingHeader.get(i) % 128) * multiplier;
            multiplier *= 128;
        }
        
        return packetRemainingLength;
    }
    
    /**
     * Parses incoming packet bytes and builds packet object.
     * 
     * @param controlHeader control header byte
     * @param remainingLength byte array containing encoded length of variable 
     * headers + length of payload
     * @param remainingData binary encoded variable headers and payload
     * @return MQTT packet object
     * @throws MQTTException thrown if packet information are malformed
     */
    public static Packet parse(byte controlHeader, int remainingLength, byte[] remainingData) throws MQTTException {
        /**
         * Parsed type of the packet.
         */
        PacketType type = getPacketType(controlHeader);
        /**
         * Parsed packet flags.
         */
        boolean[] flags = getFlags(controlHeader);
        /**
         * Binary encoded variable headers.
         */
        byte[] variableHeaders = null;
        /**
         * Parsed packet payload.
         */
        String payload = "";
        
        switch (type) {
            case CONNECT: {
                // TODO Will be implemented in the future
                break;
            } case CONNACK: {
                // TODO Will be implemented in the future
                break;
            } case PUBLISH: {
                if ((flags[1] && flags[2]) || (flags[0] && !flags[1] && !flags[2])) {
                    throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed flags field");
                }
                // TODO Along with QoS 1 and 2, there must be a "Packet identifier" distinguisher implemented
                                
                int topicLength = remainingData[0] * 256 + remainingData[1];
                
                variableHeaders = Arrays.copyOfRange(remainingData, 0, topicLength + 2);
                payload = new String(Arrays.copyOfRange(remainingData, topicLength + 2, remainingLength), Charset.forName("UTF-8"));
                break;
            } case PUBACK: {
                // TODO Will be implemented along with QoS 1 and 2
            } case PUBREC: {
                // TODO Will be implemented along with QoS 1 and 2
            } case PUBREL: {
                // TODO Will be implemented along with QoS 1 and 2
            } case PUBCOMP: {
                // TODO Will be implemented along with QoS 1 and 2
            } case SUBSCRIBE: {
                if (flags[0] || flags[1] || !flags[2] || flags[3]) {
                    throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed flags field");
                }
                
                variableHeaders = Arrays.copyOfRange(remainingData, 0, 2);
                payload = new String(remainingData, 2, remainingData.length - 2, Charset.forName("UTF-8"));
                break;
            } case SUBACK: {
                if (flags[0] != false || flags[1] != false || flags[2] != false || flags[3] != false) {
                    throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed flags field");
                }
                
                variableHeaders = new byte[2];
                System.arraycopy(remainingData, 0, variableHeaders, 0, 2);
                payload = new String(remainingData, 2, remainingData.length - 2, Charset.forName("UTF-8"));
                break;
            } case UNSUBSCRIBE: {
                // TODO Will be implemented in the future
                break;
            } case UNSUBACK: {
                // TODO Will be implemented in the future
                break;
            } case PINGREQ: {
                // TODO Will be implemented in the future
                break;
            } case PINGRESP: {
                // TODO Will be implemented in the future
                break;
            } case DISCONNECT: {
                // TODO Will be implemented in the future
                break;
            } default: throw new MQTTException(MQTTExceptionLevel.ERROR, "Packet malformed");
        }
        
        return new Packet(type, flags, remainingLength, variableHeaders, payload);
    }
    
    /**
     * Builds binary representation of MQTT packet.
     * 
     * @param packet packet to be parsed
     * @return byte array containing encoded packet parameters
     * @throws MQTTException thrown if packet parameters are malformed
     */
    public static byte[] parse(Packet packet) throws MQTTException {
        /**
         * Binary encoded length of variable headers and payload.
         */
        byte[] binaryReaminingLength = new byte[4];
        /**
         * Nubmber of bytes used by remaining length field.
         */
        byte binaryReaminingLengthSize = 0;
        /**
         * Nonencoded packet remaining length.
         */
        int remainingLength = packet.getRemainingLength();
        /**
         * Supporting variable for encoding of remaining length.
         */
        int encodedByte;
        
        do {
            encodedByte = remainingLength % 128;
            remainingLength = remainingLength / 128;

            if (remainingLength > 0) {
                encodedByte = encodedByte | 128;
            }
            
            binaryReaminingLength[binaryReaminingLengthSize++] = (byte) encodedByte;
        } while (remainingLength > 0);
        
        /**
         * Binary representation of the packet.
         */
        byte[] binaryPacket = new byte[1 + binaryReaminingLengthSize + packet.getRemainingLength()];
        /**
         * Binary representation of the packet type.
         */
        byte binaryType = getBinaryPacketType(packet.getType());
        /**
         * Binary representation of packet flags.
         */
        byte binaryFlags = getBinaryFlags(packet.getFlags());
        /**
         * Binary encoded variable headers.
         */
        byte[] variableHeaders = packet.getVariableHeaders();
        /**
         * Binary encoded payload.
         */
        byte[] binaryPayload = packet.getPayload().getBytes(Charset.forName("UTF-8"));
        
        binaryPacket[0] = (byte) (binaryType | binaryFlags);
        System.arraycopy(binaryReaminingLength, 0, binaryPacket, 1, binaryReaminingLengthSize);
        System.arraycopy(variableHeaders, 0, binaryPacket, 1 + binaryReaminingLengthSize, variableHeaders.length);
        System.arraycopy(binaryPayload, 0, binaryPacket, 1 + binaryReaminingLengthSize + variableHeaders.length, binaryPayload.length);
        
        return binaryPacket;
    }
    
}
