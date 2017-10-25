/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuwantha
 */
public class Node {

    private int myBucketId;
    private String status;
    private String ip;
    private String port;
    private String userName;
    private Map<Integer, Neighbour> bucketTable;
    private Map<String, ArrayList<String>> fileDictionary;
    private ArrayList<Neighbour> myNodeList;

    public Node(int myBucketId, String ip, String port, String username, Map<String, ArrayList<String>> fileDictionary) {
        this.myBucketId = myBucketId;
        this.status = "0";
        this.ip = ip;
        this.port = port;
        this.userName = username;
        this.bucketTable = new HashMap<>();
        this.fileDictionary = fileDictionary;
        this.myNodeList = new ArrayList<>();
    }

    Node() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getMyBucketId() {
        return myBucketId;
    }

    public void setMyBucketId(int myBucketId) {
        this.myBucketId = myBucketId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<Integer, Neighbour> getBucketTable() {
        return bucketTable;
    }

    public void setBucketTable(Map<Integer, Neighbour> bucketTable) {
        this.bucketTable = bucketTable;
    }

    public Map<String, ArrayList<String>> getFileDictionary() {
        return fileDictionary;
    }

    public void setFileDictionary(Map<String, ArrayList<String>> fileDictionary) {
        this.fileDictionary = fileDictionary;
    }

    public ArrayList<Neighbour> getMyNodeList() {
        return myNodeList;
    }

    public void setMyNodeList(ArrayList<Neighbour> myNodeList) {
        this.myNodeList = myNodeList;
    }

    public void initialize() {
        // Register With Bootstrap Server
        String msg = " REG " + this.ip + " " + this.port + " " + this.userName;
        msg = "00" + Integer.toString(msg.length()) + msg;

        try {
            multicast(msg, myNodeList);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // handles REGOK responses from BS
    // length REGOK no_nodes IP_1 port_1 IP_2 port_2
    public void handleRegisterResponse(String msg) {
        String[] arr = msg.split(" ");

        // validate msg
        if (!arr[1].equals("REGOK")) {
            return;
        }

        switch (arr[2]) {
            case "0":
                break;
            case "1":
                break;
            case "2":
                break;
            case "9999":
                System.out.println("failed, there is some error in the command");
                break;
            case "9998":
                System.out.println("failed, already registered! attempting unregister first");
                break;
            case "9997":
                System.out.println("failed, registered to another user, try a different IP and port");
                // TODO
                break;
            case "9996":
                System.out.println("failed, can't register. BS full.");
            default:
                // get the first 2 nodes' details
                break;
        }

    }

    private void connectWithInitialNodes() {

    }

    public void displayFiles() {

    }

    public void displayRoutingTable() {

    }

    public void findNodeFromBucket(int bucketId) throws UnknownHostException, IOException {
        String message = "FIND_BUCKET_MEMBER " + bucketId;
        multicast(message, myNodeList);
    }

    public void findNodeFromBucketReply(int bucketId, Neighbour fromNode) throws UnknownHostException, IOException {
        String nodeFromBucket = null;
        if (bucketTable.get(bucketId) != null) {
            //  nodeFromBucket = bucketTable.get(bucketId);
        }
        unicast("FOUND_BUCKET_MEMBER " + nodeFromBucket + " " + this.getIp() + " " + this.getPort(), fromNode);
    }

    public void multicast(String message, ArrayList<Neighbour> neighboursList) throws SocketException, UnknownHostException, IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        for (Neighbour neighbour : neighboursList) {
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(neighbour.getIp());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 80);
            datagramSocket.send(packet);
        }
    }

    public void unicast(String message, Neighbour neighbour) throws SocketException, UnknownHostException, IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        InetAddress receiverAddress = InetAddress.getByName(neighbour.getIp());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 80);
        datagramSocket.send(packet);
    }

    //gracefull leave
    public void leave(int bucketId) throws IOException {
        String message = "LEAVING BUCKET " + bucketId;
        multicast(message, myNodeList);
    }
}
