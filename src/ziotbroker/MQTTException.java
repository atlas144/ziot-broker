package ziotbroker;

/**
 * Exception for MQTT protocol violation.
 * 
 * @author Jakub Svarc
 */
public class MQTTException extends Exception {
    
    /**
     * Level of exception relevance.
     */
    private final MQTTExceptionLevel level;

    /**
     * Builds MQTTMessage.
     * 
     * @param level level of exception relevance
     * @param message error message
     */
    public MQTTException(MQTTExceptionLevel level, String message) {
        super(message);
        this.level = level;
    }
    
    /**
     * Returns level of exception relevance.
     * 
     * @return level of exception relevance
     */
    public MQTTExceptionLevel getLevel() {
        return level;
    }
    
}
