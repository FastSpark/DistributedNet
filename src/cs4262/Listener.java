/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

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

    @Override
    public void run() {
        try {
            listen(13546); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listen(int portNumber) throws IOException, ClassNotFoundException {
        // Port number to bind server to.
        int portNum = portNumber;

        // Socket for server to listen at.
        DatagramSocket datagramSocket = new DatagramSocket(portNum);
        System.out.println("Now listening to port: " + portNum);
        byte[] buffer;
        DatagramPacket packet;
        // Simply making Server run continously.
        while (true) {
            buffer = new byte[10];
            packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());
            //print  the details of incoming data - client ip : client port - client message
            System.err.println(packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + message);

            /* here we need to process the message and call recuired function according to the
            message type
             */
            String[] messagePart = message.split(" ");
            switch (messagePart[1]) {
                case "REGOK":  //handle  response from bootstrp
                    break;
                case "UNROK": // handle unregister response
                    break;
                case "JOINOK": // join response message
                    break;
                case "LEAVEOK": // leave response message
                    break;
                case "SEROK": // search response message
                    break;
                case "HEARTBEATOK": //haddle hearbeat ok    
                    break;

            }
        }
    }

}
