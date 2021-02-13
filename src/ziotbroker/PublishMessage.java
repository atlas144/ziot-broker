package ziotbroker;

/**
 * Message gathered from publish packet. Contains payload and topic to which 
 * the packet was published.
 *
 * @author Jakub Svarc
 */
public class PublishMessage {
    
    /**
     * Topic to which the packet was published.
     */
    private final String topic;
    /**
     * The message itself.
     */
    private final String payload;

    /**
     * Builds MQTTMesage.
     * 
     * @param topic topic to which the packet was published
     * @param payload the message itself
     */
    public PublishMessage(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }
    
    /**
     * Returns topic to which the packet was published.
     * 
     * @return topic to which the packet was published
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns message content.
     * 
     * @return message content
     */
    public String getPayload() {
        return payload;
    }
    
}
