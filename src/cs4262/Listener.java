/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

import client.ClientFrame;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuwantha
 */
public class Listener implements Runnable {

    ClientFrame clientFrame;

    public Listener(ClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }

    @Override
    public void run() {
        try {
            listen(this.clientFrame); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listen(ClientFrame clientFrame) throws IOException, ClassNotFoundException {
        // Port number to bind server to.
        int portNum = clientFrame.getPort();

        // Socket for server to listen at.
        DatagramSocket datagramSocket = clientFrame.getDatagramSocket();
//        System.out.println("Now listening to port: " + portNum);
        byte[] buffer;
        DatagramPacket packet;
        // Simply making Server run continously.
        while (true) {
            buffer = new byte[125536];
            packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());
//            System.out.println("Message Recieved : " + message);
            //print  the details of incoming data - client ip : client port - client message
//            System.err.println(packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + message);

            /* here we need to process the message and call recuired function according to the
            message type
             */
            String[] messagePart = message.split(" ");
            String[] sentNode;
            switch (messagePart[1]) {
                case "REGOK":
                    //handle  response from bootstrap
                    //System.out.println(message);
                    clientFrame.handleRegisterResponse(message);
                    break;
                case "UNROK": // handle unregister response
                    clientFrame.handleLeaveOk(message);
                    break;
                case "JOINOK": // join response message
                    break;
                case "LEAVEOK": // leave response message
                    clientFrame.handleLeaveOk(message);
                    break;
                case "LEAVE": // leave response message
                    clientFrame.handleLeave(message);
                    break;
                case "SER":
                    System.out.println(message);
                    this.clientFrame.searchFiles(message);
                    break;
                case "SEROK": // search response message
                    System.out.println(message);
                    this.clientFrame.handleSearchFilesResponse(message);
                    break;
                case "HEARTBEATOK": //haddle hearbeat ok
                    //System.out.println(message);
                    clientFrame.handleHeartBeatResponse(message);
                    break;
                case "HEARTBEAT":
                    //System.out.println(message);
                    clientFrame.sendHeartBeatReply(message);
                    break;
                //this.client.
                case "FBM": //multicast message to find a node from a bucket
//                    System.out.println(message);
                    sentNode = messagePart[3].split("\\:");
                    this.clientFrame.findNodeFromBucketReply(Integer.parseInt(messagePart[2]), new Node(sentNode[0], Integer.valueOf(sentNode[1])));
                    break;
                case "FBMOK": //reply to FBM
                    this.clientFrame.receiveReplyFindNodeFromBucket(message);
                    break;
                case "FNL": // unicast message to find myNodeList from node
//                  System.out.println(message);
                    sentNode = messagePart[2].split(":");
                    this.clientFrame.findMyNodeListFromNodeReply(new Node(sentNode[0], Integer.valueOf(sentNode[1])));
                    break;
                case "FNLOK": //reply to FNL
                    this.clientFrame.receiveReplyfindMyNodeListFromNode(message);
                    break;
                case "CWN": 
                    this.clientFrame.HandleConnectWithNodes(message);
                    break;
            }
        }
    }

}
