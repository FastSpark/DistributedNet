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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuwantha
 */
public class Client {

    private int k;
    private int myBucketId;
    private String status;
    private String ip;
    private int port;
    private String userName;
    private Map<Integer, Node> bucketTable;
    private Map<String, ArrayList<String>> fileDictionary;
    private ArrayList<Node> myNodeList;
    private Timestamp timestamp;
    public Client(int k, int myBucketId, String ip, int port, String username, Map<String, ArrayList<String>> fileDictionary) {
        this.k = k; // get from main
        this.myBucketId = myBucketId;
        this.status = "0";
        this.ip = ip;
        this.port = port;
        this.userName = username;
        this.bucketTable = new HashMap<>();
        this.fileDictionary = fileDictionary;
        this.myNodeList = new ArrayList<>();
        this.timestamp=new Timestamp(System.currentTimeMillis());
    }

    Client() {
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<Integer, Node> getBucketTable() {
        return bucketTable;
    }

    public void setBucketTable(Map<Integer, Node> bucketTable) {
        this.bucketTable = bucketTable;
    }

    public Map<String, ArrayList<String>> getFileDictionary() {
        return fileDictionary;
    }

    public void setFileDictionary(Map<String, ArrayList<String>> fileDictionary) {
        this.fileDictionary = fileDictionary;
    }

    public ArrayList<Node> getMyNodeList() {
        return myNodeList;
    }

    public void setMyNodeList(ArrayList<Node> myNodeList) {
        this.myNodeList = myNodeList;
    }

    public void sendMessage(String msg) {
        try {
            System.out.println("Sending message: " + msg);

            DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(this.ip), 55555);
            DatagramSocket ds = new DatagramSocket(13548);
            ds.send(dp);            
            
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initialize() {
        // Register With Bootstrap Server
        String msg = " REG " + this.ip + " " + this.port + " " + this.userName;
        msg = "00" + Integer.toString(msg.length()) + msg;

        sendMessage(msg);
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
                System.out.println("You are the first node, registered successfully with BS!");
                // change up the "status" to ready (1) ????
                break;
            case "1":
                storeNode(arr[3], arr[4]);
                // change up the "status" to ready (1) ????
                break;
            case "2":
                storeNode(arr[3], arr[4]);
                storeNode(arr[5], arr[6]);
                // change up the "status" to ready (1) ????
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
                // store FIRST 2 nodes' details
                storeNode(arr[3], arr[4]);
                storeNode(arr[5], arr[6]);

                // complete bucketTable
                for (int i = 0; i < k; i++) {
                    if (!bucketTable.containsKey(i)) {
                        // findNodeFromBucket(i);   handle exceptions
                    }
                }

                // complete myNodeList    
                if (myNodeList.isEmpty()) {
                    // findNodeFromBucket(myBucketId);
                    // send message to that returned node to get it's myNodeList and then store
                } else {
                    // send message to that node to get it's myNodeList and then store
                }

                // change up the "status" to ready (1)
                break;
        }

    }

    private void storeNode(String ip, String port) {
        Node newNode = new Node(ip, Integer.parseInt(port));
        int bucketId = (ip + ":" + port).hashCode() % k;
        if (bucketId == this.myBucketId) {
            myNodeList.add(newNode);
        } else {
            bucketTable.put(bucketId, newNode);
        }
    }

    private void connectWithInitialNodes() {
        
    }

    public void displayFiles() {

    }

    public void displayRoutingTable() {

    }
    
    public void searchFiles(){
        //length SER IP port file_name hops
        
    }

    
    public void findNodeFromBucket(int bucketId) throws UnknownHostException, IOException {
        //FBM: Find Bucket Member 0011 FBM 01
        String message = "FBM " + bucketId;
        message= String.format("%04d", message.length() + 5) + " " + message;
        multicast(message, myNodeList);
    }

    public void findNodeFromBucketReply(int bucketId, Node fromNode) throws UnknownHostException, IOException {
        //FBMOK: Find Bucket Member OK
        Node nodeFromBucket = null;
        String message=null;
        if (bucketTable.get(bucketId) != null) {
            nodeFromBucket = bucketTable.get(bucketId);
            message = "FBMOK " +bucketId +""+ nodeFromBucket.getIp() + " " + nodeFromBucket.getPort();
        }else{
            message = "FBMOK "+bucketId+" null null";
        }
        message= String.format("%04d", message.length() + 5) + " " + message;
        unicast(message, fromNode);
    }
    
    public void receiveReplyFindNodeFromBucket(String message) throws UnknownHostException, IOException {
        String[] split_msg = message.split(" ");        
        Node bucket_node= new Node(split_msg[3], Integer.valueOf(split_msg[4]));
        this.bucketTable.put(Integer.valueOf(split_msg[2]), bucket_node);
    }

    public void multicast(String message, ArrayList<Node> nodesList) throws SocketException, UnknownHostException, IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        for (Node node : nodesList) {
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(node.getIp());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 80);
            datagramSocket.send(packet);
        }
    }

    public void unicast(String message, Node node) throws SocketException, UnknownHostException, IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        InetAddress receiverAddress = InetAddress.getByName(node.getIp());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 80);
        datagramSocket.send(packet);
    }

    //gracefull leave
    public void leave(int bucketId) throws IOException {
        String message = "LEAVING BUCKET " + bucketId;
        multicast(message, myNodeList);
    }
    public void updateRountingTable() throws IOException{
        ArrayList<Node> temNeighboursList = new ArrayList<Node>();
        for (Node neighbour : myNodeList) {
            if(timestamp.getTime()-neighbour.getTimeStamp()<5000){
                temNeighboursList.add(neighbour);
            }
        }
        this.myNodeList=temNeighboursList;
        
        for(int key:bucketTable.keySet()){
            Node neighbour = bucketTable.get(key);
            if(timestamp.getTime()-neighbour.getTimeStamp()>5000){
                bucketTable.remove(key);
                this.findNodeFromBucket(key);
            }
        }
        
    }
    
    public void handleHeartBeatResponse(String message){
        //length HEARTBEATOK IP_address port_no
        boolean is_Change=false;
        ArrayList<Node> temNeighboursList = new ArrayList<Node>();
        String[] splitMessage = message.split(" ");
        String ip = splitMessage[2];
        int port= Integer.parseInt(splitMessage[3]);
        for (Node node : myNodeList) {
            if(node.getIp().equals(ip)&& node.getPort()==port){
                node.setTimeStamp(timestamp.getTime());
                is_Change=true;
            }
            temNeighboursList.add(node);
        }
        this.myNodeList=temNeighboursList;
        
        if(!is_Change){
            for(int key:bucketTable.keySet()){
                Node neighbour = bucketTable.get(key);
                if(neighbour.getIp().equals(ip)&& neighbour.getPort()==port){
                    neighbour.setTimeStamp(timestamp.getTime());
                    bucketTable.replace(key, neighbour);                    
                }
            }
        }
        
    }

    
}
