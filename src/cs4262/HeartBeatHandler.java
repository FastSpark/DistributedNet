/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuwantha
 */
public class HeartBeatHandler implements Runnable{
    private DatagramSocket ds;
    private Client client;
    public HeartBeatHandler(Client client){
        this.client=client;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                sendHeartBeat();
                Thread.sleep(100);
                client.updateRountingTable();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(HeartBeatHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }

    //send Heartbeat to other nodes
    public void sendHeartBeat() throws IOException{
        String message="HEARTBEAT "+client.getIp()+" "+client.getPort();
        message = String.format("%04d", message.length() + 5) + " " + message;
        client.multicast(message, client.getMyNodeList());
        Set<Integer> keySet = client.getBucketTable().keySet();
        for (int key : keySet) {
            client.unicast(message, client.getBucketTable().get(key));
        }
    }
}
