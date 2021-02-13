package ziotbroker;

/**
 * Level of exception relevance.
 *
 * @author Jakub Svarc
 */
public enum MQTTExceptionLevel {
    /**
     * Notify user/just log.
     */ 
    INFO,
    /**
     * Resolve immediately.
     */
    WARNING,
    /**
     * Close connection and shutdown client.
     */
    ERROR,
    /**
     * Shutdown broker.
     */
    CRITICAL
}
