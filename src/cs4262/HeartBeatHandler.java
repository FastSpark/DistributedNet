/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

import client.ClientFrame;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuwantha
 */
public class HeartBeatHandler implements Runnable {

    private DatagramSocket ds;
    private ClientFrame clientFrame;

    public HeartBeatHandler(ClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sendHeartBeat();
                Thread.sleep(100);
                this.clientFrame.updateRountingTable();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(HeartBeatHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //send Heartbeat to other nodes
    public void sendHeartBeat() throws IOException {
        String message = "HEARTBEAT " + this.clientFrame.getIp() + " " + this.clientFrame.getPort();
        message = String.format("%04d", message.length() + 5) + " " + message;
        ArrayList<Node> myNodeListWithoutMe = this.clientFrame.getMyNodeList();
        this.clientFrame.multicast(message, this.clientFrame.getMyNodeList());
        Set<Integer> keySet = this.clientFrame.getBucketTable().keySet();
        for (int key : keySet) {
            this.clientFrame.unicast(message, this.clientFrame.getBucketTable().get(key));
        }
    }
}
