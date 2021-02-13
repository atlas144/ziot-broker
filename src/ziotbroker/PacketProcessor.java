package ziotbroker;

import ziotbroker.packet.Packet;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Service which processes incoming packets from clients.
 *
 * @author Jakub Svarc
 */
public class PacketProcessor {
    
    /**
     * Map storing subscribtions of the clients.
     */
    private final HashMap<Subscribtion, ArrayList<OutputClientService>> subscribtions;
    /**
     * Queue containing messages extracted from PUBLISH packets.
     */
    private final ArrayBlockingQueue<PublishMessage> incomingMessageQueue;
    
    /**
     * Builds packet processor.
     * 
     * @param subscribtions map storing subscribtions of the clients
     */
    public PacketProcessor(HashMap<Subscribtion, ArrayList<OutputClientService>> subscribtions) {
        this.subscribtions = subscribtions;
        this.incomingMessageQueue = new ArrayBlockingQueue<>(10000);
    }
    
    /**
     * Processes incoming <b>CONNECT</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>CONNECT</b> packet to be processed
     */
    public void processConnect(Packet packet) {
        // TODO Will be implemented in the future
    }
    
    /**
     * Processes incoming <b>PUBLISH</b> packet and sends it to all clients with
     * subscription to its topic.
     * Only QoS 0 is supported now. Other will be implemented in the future.
     * 
     * @param packet incoming packet to be processed
     * @throws MQTTException if packet data are corrupted (e.g. QoS is set to 4)
     */
    public void processPublish(Packet packet) throws MQTTException {
        boolean[] flags = packet.getFlags();
        //TODO Retain flag resolving will be implemented in the future

        if (!flags[1] && !flags[2]) {
            try {
                if (flags[0]) {
                    throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed flags field");
                }

                byte[] variableHeaders = packet.getVariableHeaders();
                String topic = new String(variableHeaders, 2, variableHeaders.length - 2, Charset.forName("UTF-8"));

                incomingMessageQueue.put(new PublishMessage(topic, packet.getPayload()));
                
                Packet responsePacket = PacketBuilder.buildPublishPacket(flags, topic, packet.getPayload());
                ArrayList<OutputClientService> matchingSubscriptions = subscribtions.get(new Subscribtion(topic, (byte) 0));

                if (matchingSubscriptions != null) {
                    matchingSubscriptions.stream().map(matchingSubscription -> matchingSubscription).forEach(matchingOutputClient -> {
                        matchingOutputClient.send(responsePacket);
                    });
                }
            } catch (InterruptedException ex) {}
        } else if (!flags[1] && flags[2]) {
            // TODO Will be implemented along with QoS 1 and 2
        } else if (flags[1] && !flags[2]) {
            // TODO Will be implemented along with QoS 1 and 2
        }
    }
    
    /**
     * Processes incoming <b>PUBACK</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>PUBACK</b> packet to be processed
     */
    public void processPuback(Packet packet) {
        // TODO Will be implemented along with QoS 1 and 2
    }
    
    /**
     * Processes incoming <b>PUBREC</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>PUBREC</b> packet to be processed
     */
    public void processPubrec(Packet packet) {
        // TODO Will be implemented along with QoS 1 and 2
    }
    
    /**
     * Processes incoming <b>PUBREL</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>PUBREL</b> packet to be processed
     */
    public void processPubrel(Packet packet) {
        // TODO Will be implemented along with QoS 1 and 2
    }
    
    /**
     * Processes incoming <b>PUBCOMP</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>PUBCOMP</b> packet to be processed
     */
    public void processPubcomp(Packet packet) {
        // TODO Will be implemented along with QoS 1 and 2
    }
    
    /**
     * Processes incoming <b>SUBSCRIBE</b> packet and makes corresponding subscribtions.
     * 
     * @param packet
     * @param outputClient
     * @throws MQTTException 
     */
    public void processSubscribe(Packet packet, OutputClientService outputClient) throws MQTTException {
        /**
         * Binary representation of payload (which contains list of 
         * QoS + topic pairs).
         */
        byte[] binaryPayload = packet.getPayload().getBytes(Charset.forName("UTF-8"));
        /**
         * Array containing return codes for results of given subscribtions.
         */
        ArrayList<Byte> subscribtionReturnCodes = new ArrayList<>();
        /**
         * Array containing return codes for results of given subscribtions 
         * (transformed "subscribtionReturnCodes").
         */
        byte[] subscribtionReturnCodesArray;
        /**
         * Support variable which indicates number of bytes of payload read.
         */
        int bytesRead = 0;
        /**
         * Support variable - length of currently processed topic.
         */
        int topicLength;
        /**
         * Support variable - currently processed topic.
         */
        String topic;
        /**
         * Support variable - QoS for currently processed topic.
         */
        byte qualityOfService;
        /**
         * Support variable - new subscribtion to be stored.
         */
        Subscribtion newSubscribtion;

        do {
            topicLength = binaryPayload[bytesRead] * 256 + binaryPayload[bytesRead + 1];
            topic = new String(binaryPayload, bytesRead + 2, topicLength, Charset.forName("UTF-8"));
            qualityOfService = binaryPayload[bytesRead + 2 + topicLength];

            if (qualityOfService < 0 || qualityOfService > 2) {
                throw new MQTTException(MQTTExceptionLevel.ERROR, "Malformed QoS field");
            }

            newSubscribtion = new Subscribtion(topic, qualityOfService);

            if (subscribtions.containsKey(newSubscribtion)) {
                subscribtions.get(newSubscribtion).add(outputClient);
            } else {
                ArrayList<OutputClientService> newSubscribers = new ArrayList<>();
                newSubscribers.add(outputClient);
                subscribtions.put(newSubscribtion, newSubscribers);
            }

            subscribtionReturnCodes.add(qualityOfService);

            bytesRead += 2 + topicLength + 1;
        } while (bytesRead < binaryPayload.length);

        subscribtionReturnCodesArray = new byte[subscribtionReturnCodes.size()];

        for (int i = 0; i < subscribtionReturnCodes.size(); i++) {
            subscribtionReturnCodesArray[i] = subscribtionReturnCodes.get(i);
        }

        outputClient.send(PacketBuilder.buildSubackPacket(packet.getVariableHeaders(), subscribtionReturnCodesArray));
    }
    
    /**
     * Processes incoming <b>UNSUBSCRIBE</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>UNSUBSCRIBE</b> packet to be processed
     */
    public void processUnsubscribe(Packet packet) {
        // TODO Will be implemented in the future
    }
    
    /**
     * Processes incoming <b>PINGREQ</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>PINGREQ</b> packet to be processed
     */
    public void processPingreq(Packet packet) {
        // TODO Will be implemented in the future
    }
    
    /**
     * Processes incoming <b>DISCONNECT</b> packet. - <b>NOT IMPLEMENTED YET!!!</b>
     * 
     * @param packet incoming <b>DISCONNECT</b> packet to be processed
     */
    public void processDisconnect(Packet packet) {
        // TODO Will be implemented in the future
    }
    
    /**
     * Returns incoming messages from clients. It blocks until message
     * is available.
     * 
     * @return incoming message
     */
    public PublishMessage getPublishedMessage() {
        try {
            return incomingMessageQueue.take();
        } catch (InterruptedException ex) {
            return null;
        }
    }
    
}
