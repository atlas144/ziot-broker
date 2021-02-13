package ziotbroker.packet;

import java.nio.charset.Charset;
import java.util.Arrays;
import ziotbroker.MQTTException;
import ziotbroker.MQTTExceptionLevel;
import ziotbroker.QoS;

/**
 *
 * @author Jakub Svarc
 */
public class ConnectPacket extends Packet {
    
    /**
     * Charset used for encoding strings in packet (for MQTT 3.1.1 it is UTF-8).
     */
    private static final Charset CHARSET = Charset.forName("UTF-8");
    
    /**
     * ID representing established session.
     */
    private final String clientID;
    /**
     * Number of seconds to keep session if no packet was sent.
     */
    private final short keepAlive;
    /**
     * Indicates if new session should be established when new CONNECT packet 
     * arrives.
     */
    private final boolean cleanSession;
    /**
     * Indicates if a username is present in payload.
     */
    private final boolean userNameFlag;
    /**
     * Username of connected client.
     */
    private final String userName;
    /**
     * Indicates if a password is present in payload.
     */
    private final boolean passwordFlag;
    /**
     * Password of connected client.
     */
    private final String password;
    /**
     * Indicates if will should be set.
     */
    private final boolean willFlag;
    /**
     * Topic to which the will message should be sent.
     */
    private final String willTopic;
    /**
     * Content of will message.
     */
    private final String willMessage;
    /**
     * Quality of service of will message.
     */
    private final QoS willQoS;
    /**
     * Indicates if will message should be retained.
     */
    private final boolean willRetain;
    
    /**
     * Basic constructor used to build CONNECT packet object with all 
     * propperties.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     * @param userNameFlag indicates if an user name is present in payload
     * @param userName user name of connected client
     * @param passwordFlag indicates if a password is present in payload
     * @param password password of connected client
     * @param willFlag indicates if will should be set
     * @param willTopic topic to which the will message should be sent
     * @param willMessage content of will message
     * @param willQoS quality of service of will message
     * @param willRetain indicates if will message should be retained
     */
    private ConnectPacket(String clientID, short keepAlive, boolean cleanSession, boolean userNameFlag, String userName, boolean passwordFlag, String password, boolean willFlag, String willTopic, String willMessage, QoS willQoS, boolean willRetain) {       
        super(PacketType.CONNECT);
        this.clientID = clientID;
        this.keepAlive = keepAlive;
        this.cleanSession = cleanSession;
        this.userNameFlag = userNameFlag;
        this.userName = userName;
        this.passwordFlag = passwordFlag;
        this.password = password;
        this.willFlag = willFlag;
        this.willTopic = willTopic;
        this.willMessage = willMessage;
        this.willQoS = willQoS;
        this.willRetain = willRetain;
    }
    
    /**
     * Builds CONNECT packet object from given properties. Packet contains all 
     * optional fields.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * (0 if session should persist forever)
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     * @param userName user name of connected client
     * @param password password of connected client
     * @param willTopic topic to which the will message should be sent
     * @param willMessage content of will message
     * @param willQoS quality of service of will message
     * @param willRetain indicates if will message should be retained
     */
    public ConnectPacket(String clientID, short keepAlive, boolean cleanSession, String userName, String password, String willTopic, String willMessage, QoS willQoS, boolean willRetain) {
        this(clientID, keepAlive, cleanSession, true, userName, true, password, true, willTopic, willMessage, willQoS, willRetain);
    }
    
    /**
     * Builds CONNECT packet object from given properties. Packet also contains 
     * <i>username</i> and <i>will</i> fields.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * (0 if session should persist forever)
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     * @param userName user name of connected client
     * @param willTopic topic to which the will message should be sent
     * @param willMessage content of will message
     * @param willQoS quality of service of will message
     * @param willRetain indicates if will message should be retained
     */
    public ConnectPacket(String clientID, short keepAlive, boolean cleanSession, String userName, String willTopic, String willMessage, QoS willQoS, boolean willRetain) {
        this(clientID, keepAlive, cleanSession, true, userName, false, null, true, willTopic, willMessage, willQoS, willRetain);
    }
    
    /**
     * Builds CONNECT packet object from given properties. Packet also contains 
     * <i>will</i> field.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * (0 if session should persist forever)
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     * @param willTopic topic to which the will message should be sent
     * @param willMessage content of will message
     * @param willQoS quality of service of will message
     * @param willRetain indicates if will message should be retained
     */
    public ConnectPacket(String clientID, short keepAlive, boolean cleanSession, String willTopic, String willMessage, QoS willQoS, boolean willRetain) {
        this(clientID, keepAlive, cleanSession, false, null, false, null, true, willTopic, willMessage, willQoS, willRetain);
    }
    
    /**
     * Builds CONNECT packet object from given properties. Packet also contains 
     * <i>username</i> and <i>password</i> fields.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * (0 if session should persist forever)
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     * @param userName user name of connected client
     * @param password password of connected client
     */
    public ConnectPacket(String clientID, short keepAlive, boolean cleanSession, String userName, String password) {
        this(clientID, keepAlive, cleanSession, true, userName, true, password, true, null, null, QoS.AT_MOST_ONCE, false);
    }
    
    /**
     * Builds CONNECT packet object from given properties. Packet also contains 
     * <i>username</i> field.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * (0 if session should persist forever)
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     * @param userName user name of connected client
     */
    public ConnectPacket(String clientID, short keepAlive, boolean cleanSession, String userName) {
        this(clientID, keepAlive, cleanSession, true, userName, false, null, true, null, null, QoS.AT_MOST_ONCE, false);
    }
    
    /**
     * Builds CONNECT packet object from given properties.
     * 
     * @param clientID unique identifier of established session
     * @param keepAlive number of seconds to keep session if no packet was sent
     * (0 if session should persist forever)
     * @param cleanSession indicates if new session should be established when
     * new CONNECT arrives
     */
    public ConnectPacket(String clientID, short keepAlive, boolean cleanSession) {
        this(clientID, keepAlive, cleanSession, false, null, false, null, true, null, null, QoS.AT_MOST_ONCE, false);
    }
    
    /**
     * Builds CONNECT packet object from binary representation of the packet.
     * 
     * @param controlHeader first byte of the packet (contains packet type and
     * flags)
     * @param remainingHeaders binary representation of variable headers and 
     * payload
     * @throws MQTTException thrown if packet data are corrupted
     */
    public ConnectPacket(byte controlHeader, byte[] remainingHeaders) throws MQTTException {       
        super(PacketType.CONNECT);
        
        if (controlHeader != 0b00010000) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Packet controll header mallformed!");
        }
        
        int cursor = 0;
        final int protocolNameLength = remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++];
        final String protocolName = new String(Arrays.copyOfRange(remainingHeaders, cursor, cursor += protocolNameLength), CHARSET);
        
        if (!protocolName.equals("MQTT")) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Protocol name header mallformed - " + protocolName);
        }
        
        final int protocolLevel = remainingHeaders[cursor++];
        
        if (protocolLevel != 4) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Unsuported protocol level - " + protocolLevel);
        }
        
        final byte connectFlags = remainingHeaders[cursor++];
        
        if ((connectFlags & 0b00000001) != 0b00000000) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Connect flags header mallformed!");
        }
        
        userNameFlag = (connectFlags & 0b10000000) == 0b10000000;
        passwordFlag = (connectFlags & 0b01000000) == 0b01000000;
        
        if (!userNameFlag && passwordFlag) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Password flag is 1 but username is 0!");
        }
        
        willRetain = (connectFlags & 0b00100000) == 0b00100000;
        willFlag = (connectFlags & 0b00000100) == 0b00000100;
        
        final byte binaryWillQoS = (byte) ((connectFlags & 0b00011000) / 8);
        
        switch (binaryWillQoS) {
            case 0: {
                willQoS = QoS.AT_MOST_ONCE;
                break;
            } case 1: {
                willQoS = QoS.AT_LEAST_ONCE;
                break;
            } case 2: {
                willQoS = QoS.EXACTLY_ONCE;
                break;
            } default: {
                throw new MQTTException(MQTTExceptionLevel.ERROR, "Connect flag \"Will QoS\" has unacceptable value - " + binaryWillQoS);
            }
        }
        
        if (!willFlag && (willRetain || willQoS != QoS.AT_LEAST_ONCE)) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Will flag is not present but other Will related flags are!");
        }
        
        cleanSession = (connectFlags & 0b00000010) == 0b00000010;
        keepAlive = (short) (remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++]);
        
        final short clientIDLength = (short) (remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++]);
        
        clientID = new String(Arrays.copyOfRange(remainingHeaders, cursor, cursor += clientIDLength), CHARSET);
        
        if (willFlag) {
            final short willTopicLength = (short) (remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++]);

            willTopic = new String(Arrays.copyOfRange(remainingHeaders, cursor, cursor += willTopicLength), CHARSET);

            final short willMessageLength = (short) (remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++]);

            willMessage = new String(Arrays.copyOfRange(remainingHeaders, cursor, cursor += willMessageLength), CHARSET);
        } else {
            willTopic = null;
            willMessage = null;
        }
        
        if (userNameFlag) {
            final short userNameLength = (short) (remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++]);

            userName = new String(Arrays.copyOfRange(remainingHeaders, cursor, cursor += userNameLength), CHARSET);
            if (passwordFlag) {
                final short passwordLength = (short) (remainingHeaders[cursor++] * 256 + remainingHeaders[cursor++]);

                password = new String(Arrays.copyOfRange(remainingHeaders, cursor, cursor += passwordLength), CHARSET);
            } else {
                password = null;
            }
        } else {
            userName = null;
            password = null;
        }
        
        if (cursor != remainingHeaders.length) {
            throw new MQTTException(MQTTExceptionLevel.ERROR, "Packet length does not match \"Remaining length\" value!");
        }
    }

    @Override
    public byte[] buildBinaryPacket() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns value of the client ID.
     * 
     * @return value of the client ID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * Returns value of the keep alive interval.
     * 
     * @return value of the keep alive interval
     */
    public short getKeepAlive() {
        return keepAlive;
    }

    /**
     * Returns value of the clean session flag.
     * 
     * @return value of the clean session flag
     */
    public boolean getCleanSession() {
        return cleanSession;
    }

    /**
     * Returns value of the username flag.
     * 
     * @return value of the username flag
     */
    public boolean getUserNameFlag() {
        return userNameFlag;
    }

    /**
     * Returns username of the client.
     * 
     * @return username of the client
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns value of the password flag.
     * 
     * @return value of the password flag
     */
    public boolean getPasswordFlag() {
        return passwordFlag;
    }

    /**
     * Returns password of the client.
     * 
     * @return password of the client
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Returns value of the will flag.
     * 
     * @return value of the will flag
     */
    public boolean getWillFlag() {
        return willFlag;
    }

    /**
     * Returns value of the will topic.
     * 
     * @return value of the will topic
     */
    public String getWillTopic() {
        return willTopic;
    }

    /**
     * Returns value of the will message.
     * 
     * @return value of the will message
     */
    public String getWillMessage() {
        return willMessage;
    }

    /**
     * Returns value of the will QoS.
     * 
     * @return value of the will QoS
     */
    public QoS getWillQoS() {
        return willQoS;
    }

    /**
     * Returns value of the will retain flag.
     * 
     * @return value of the will retain flag
     */
    public boolean getWillRetain() {
        return willRetain;
    }
    
}
