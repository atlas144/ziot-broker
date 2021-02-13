package ziotbroker;

import java.nio.charset.Charset;

/**
 * Subscribtion to topic + QoS pair.
 *
 * @author Jakub Svarc
 */
public class Subscribtion {
    
    /**
     * Topic of the subscribtion.
     */
    private final String topic;
    /**
     * QoS of the subscribtion.
     */
    private final byte qualityOfService;

    /**
     * Builds Subscribtion.
     * 
     * @param topic topic of the subscribtion
     * @param qualityOfService QoS of the subscribtion
     */
    public Subscribtion(String topic, byte qualityOfService) {
        this.topic = topic;
        this.qualityOfService = qualityOfService;
    }

    /**
     * Returns topic of the subscribtion.
     * 
     * @return topic of the subscribtion
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns QoS of the subscribtion.
     * 
     * @return QoS of the subscribtion
     */
    public byte getQualityOfService() {
        return qualityOfService;
    }
    
    /**
     * HashCode based on topic and QoS of subscribtion.
     * 
     * @return HashCode of the subscribtion
     */
    @Override
    public int hashCode() {
        byte[] binaryPreCode = topic.getBytes(Charset.forName("UTF-8"));
        int hashCode = 0;
        
        for (byte preCode : binaryPreCode) {
            hashCode += preCode + qualityOfService;
        }
        
        return hashCode;
    }
    
    /**
     * Matches subscribtions if their topic and QoS are the same.
     * 
     * @param o other Subscribtion to be compared with
     * @return <i>true</i> if both objects are same, else <i>false</i>
     */
    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        if (this.getClass() != o.getClass()) return false;
        
        Subscribtion otherSubscribtion = (Subscribtion) o;
        return otherSubscribtion.getTopic().equals(topic) && otherSubscribtion.getQualityOfService() == qualityOfService;
    }
}
