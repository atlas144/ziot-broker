package ziotbroker;

import ziotbroker.packet.Packet;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Interface of MQTT broker module. It controls the operation of the module and 
 * provides methods which allows interactions with it.
 *
 * @author Jakub Svarc
 */
public class MQTTBroker extends Thread {

    /**
     * Port on which the broker runs.
     */
    private final short PORT;
    /**
     * Map storing subscribtions of the clients.
     */
    private final HashMap<Subscribtion, ArrayList<OutputClientService>> subscribtions;
    /**
     * Service which processes incoming packets.
     */
    private final PacketProcessor packetProcessor;
    /**
     * Executor which executes input client services.
     */
    private final ExecutorService inputClients;
    /**
     * Executor which executes input client services.
     */
    private final ExecutorService outputClients;
    /**
     * Connection to which can clients connect.
     */
    private ServerSocket serverSocket;
    
    /**
     * Builds and initializes MQTTBroker.
     * 
     * @param port port on which the broker runs
     */
    public MQTTBroker(short port) {
        this.PORT = port;
        this.subscribtions = new HashMap<>();
        this.inputClients = Executors.newCachedThreadPool();
        this.outputClients = Executors.newCachedThreadPool();
        this.packetProcessor = new PacketProcessor(subscribtions);
    }
    
    /**
     * Continuously awaits clients to connect and builds input and output 
     * services for it.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);

            Socket clientSocket;
            InputClientService inputClient;
            OutputClientService outputClient;

            while ((clientSocket = serverSocket.accept()) != null) {
                outputClient = new OutputClientService(clientSocket);
                inputClient = new InputClientService(clientSocket, packetProcessor, outputClient);
                
                inputClients.submit(inputClient);
                outputClients.submit(outputClient);
                System.out.printf("%s - Client connected\n", this.getClass().getName());
            }
        } catch(IOException ex) {
            System.err.printf("%s - %s\n", this.getClass().getName(), ex.getMessage());
        }
    }
    
    /**
     * Publishes message with corresponding topic.
     * 
     * @param topic topic, to which the message is sent
     * @param payload the content of the message
     * @param qos quality of service of the message (0, 1 or 2) - <b>ONLY LEVEL
     * 0 IMPLEMENTED NOW</b>
     * @param retain indicates that the message should be saved on the broker 
     * - <b>NOT IMPLEMENTED YET!!! MUST BE SET TO FALSE</b>
     * @throws MQTTException thrown if given parameters are invalid
     */
    public void publish(String topic, String payload, byte qos, boolean retain) throws MQTTException {
        boolean[] flags = {false, false, false, retain};
        
        switch (qos) {
            case 0: {
                break;
            }
            case 1: {
                flags[2] = true;
                break;
            }
            case 2: {
                flags[1] = true;
                break;
            }
            default:
                throw new MQTTException(MQTTExceptionLevel.WARNING, "Malformed QoS value");
        }
        
        Packet packet = PacketBuilder.buildPublishPacket(flags, topic, payload);
        ArrayList<OutputClientService> matchingSubscriptions = subscribtions.get(new Subscribtion(topic, (byte) 0));

        if (matchingSubscriptions != null) {
            matchingSubscriptions.stream().map(matchingSubscription -> matchingSubscription).forEach(matchingOutputClient -> {
                matchingOutputClient.send(packet);
            });
        }
    }
    
    /**
     * Returns incoming messages from clients. It blocks until message
     * is available.
     * 
     * @return incoming message
     */
    public PublishMessage getMessage() {
        return packetProcessor.getPublishedMessage();
    }
    
}
