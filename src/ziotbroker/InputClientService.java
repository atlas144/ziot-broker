package ziotbroker;

import ziotbroker.packet.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
        
/**
 * Service accepting packets from corespondig client and pushing them 
 * to PacketProcessor.
 * 
 * @author Jakub Svarc
 */
public class InputClientService implements Runnable {
    
    /**
     * Connection to the client.
     */
    private final Socket clientSocket;
    /**
     * Service which processes given packets.
     */
    private final PacketProcessor packetProcessor;
    /**
     * Output service of corresponding client.
     */
    private final OutputClientService outputClient;
    
    /**
     * Builds InputClientService.
     * 
     * @param clientSocket connection to the client
     * @param packetProcessor service which processes given packets
     * @param outputClient output service of corresponding client
     */
    public InputClientService(Socket clientSocket, PacketProcessor packetProcessor, OutputClientService outputClient) {
        this.clientSocket = clientSocket;
        this.packetProcessor = packetProcessor;
        this.outputClient = outputClient;
    }

    /**
     * Continuously listens on the connection, awaits incoming packets and gives
     * them to further processing.
     */
    @Override
    public void run() {
        try (
            InputStream in = clientSocket.getInputStream();
        ) {
            /**
             * Control header byte (first in the packet).
             */
            byte controlHeader;
            /**
             * Bytes containing encoded value of the length of the rest 
             * of the packet.
             */
            ArrayList<Integer> packetLengthHeader;
            /**
             * Decoded length of the rest of the packet.
             */
            int remainingLength;
            /**
             * Bytes containing encoded packet type specific headers 
             * and payload.
             */
            byte[] variableHeader;
            /**
             * Object representation of the packet, built from received data.
             */
            Packet packet;
            
            while ((controlHeader = (byte) in.read()) != -1) {
                packetLengthHeader = new ArrayList<>();
                
                do {
                    packetLengthHeader.add(in.read());
                } while (packetLengthHeader.get(packetLengthHeader.size() - 1) > 127);
                
                remainingLength = PacketParser.getPacketRemainingLength(packetLengthHeader);
                variableHeader = new byte[remainingLength];
                in.read(variableHeader);
                
                packet = PacketParser.parse(controlHeader, remainingLength, variableHeader);
                System.out.printf("%s - Accepted %s packet\n", this.getClass().getName(), packet.getType());
                
                switch(packet.getType()) {
                    case CONNECT: {
                        packetProcessor.processConnect(packet);
                        break;
                    } case PUBLISH: {
                        packetProcessor.processPublish(packet);
                        break;
                    } case PUBACK: {
                        packetProcessor.processPuback(packet);
                        break;
                    } case PUBREC: {
                        packetProcessor.processPubrec(packet);
                        break;
                    } case PUBREL: {
                        packetProcessor.processPubrel(packet);
                        break;
                    } case PUBCOMP: {
                        packetProcessor.processPubcomp(packet);
                        break;
                    } case SUBSCRIBE: {
                        packetProcessor.processSubscribe(packet, outputClient);
                        break;
                    } case UNSUBSCRIBE: {
                        packetProcessor.processUnsubscribe(packet);
                        break;
                    } case PINGREQ: {
                        packetProcessor.processPingreq(packet);
                        break;
                    } case DISCONNECT: {
                        packetProcessor.processDisconnect(packet);
                        break;
                    } default:
                        throw new MQTTException(MQTTExceptionLevel.ERROR, "Unexpected packet type!");
                }
            }
        } catch (IOException ex) {
            System.err.printf("%s - %s\n", this.getClass().getName(), ex.getMessage());
        } catch (MQTTException ex) {
            System.err.printf("%s - %s\n", this.getClass().getName(), ex.getMessage());
            
            if (ex.getLevel() == MQTTExceptionLevel.CRITICAL) {
                System.exit(1);
            }
        }
    }

}
