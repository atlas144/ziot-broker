package ziotbroker;

import ziotbroker.packet.Packet;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
        
/**
 * Service which sends packets from broker to the client.
 * 
 * @author Jakub Svarc
 */
public class OutputClientService implements Runnable {
    
    /**
     * Connection to the client.
     */
    private final Socket clientSocket;
    /**
     * Queue containing packets to be sent to the client.
     */
    private final ArrayBlockingQueue<Packet> packetQueue;
    
    /**
     * Builds OutputClientService.
     * 
     * @param clientSocket connection to the client
     */
    public OutputClientService(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.packetQueue = new ArrayBlockingQueue<>(1000);
    }

    /**
     * Continuously pulls packets from PacketQueue, encodes them and sends them
     * to the client.
     */
    @Override
    public void run() {
        Packet packet;
        
        try (
            OutputStream out = clientSocket.getOutputStream();
        ) {            
            while ((packet = packetQueue.take()) != null) {
                byte[] binaryPacket = PacketParser.parse(packet);
                
                out.write(binaryPacket);
                System.out.printf("%s - Sent %s packet\n", this.getClass().getName(), packet.getType());
            }
        } catch (IOException | InterruptedException ex) {
            System.err.printf("%s - %s\n", this.getClass().getName(), ex.getMessage());
        } catch (MQTTException ex) {
            System.err.printf("%s - %s\n", this.getClass().getName(), ex.getMessage());
            
            if (ex.getLevel() == MQTTExceptionLevel.CRITICAL) {
                System.exit(1);
            }
        }
    }

    /**
     * Sends given packet to the client.
     * 
     * @param packet packet to be sent to the client
     */
    public void send(Packet packet) {
        try {
            packetQueue.put(packet);
        } catch (InterruptedException ex) {}
    }

}
