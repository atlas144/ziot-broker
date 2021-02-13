package ziotbroker;

/**
 * Qualities of service used in MQTT (0, 1, 2).
 *
 * @author Jakub Svarc
 */
public enum QoS {
    /**
     * QoS 0 - packets are delivered at most once (delivery isn't guaranted).
     */
    AT_MOST_ONCE,
    /**
     * QoS 1 - packets are delivered at least once (delivery is guaranted, but 
     * packet can be delivered more times).
     */
    AT_LEAST_ONCE,
    /**
     * QoS 2 - packets are delivered exactly once (delivery is guaranted exactly 
     * once).
     */
    EXACTLY_ONCE
}
