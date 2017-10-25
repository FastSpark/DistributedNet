/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

import java.net.DatagramSocket;

/**
 *
 * @author nuwantha
 */
public class HeartBeatHandler implements Runnable{
    private DatagramSocket ds;
    private Client client;
    public HeartBeatHandler(Client client, DatagramSocket ds){
        this.client=client;
        this.ds=ds;
    }
    
    @Override
    public void run() {
        while (true) {
           
        }
    }
    
}
