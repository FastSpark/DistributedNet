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
import java.util.Dictionary;

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
    private Dictionary<Integer,String> bucketTable;
    private Dictionary<String,ArrayList<String>> fileDictionary;
    private ArrayList<Neighbour> myNodeList;

    public Node(int myBucketId, String status, String ip, String port, String userName, Dictionary<Integer, String> bucketTable, Dictionary<String, ArrayList<String>> fileDictionary, ArrayList<Neighbour> myNodeList) {
        this.myBucketId = myBucketId;
        this.status = status;
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.bucketTable = bucketTable;
        this.fileDictionary = fileDictionary;
        this.myNodeList = myNodeList;
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

    public Dictionary<Integer, String> getBucketTable() {
        return bucketTable;
    }

    public void setBucketTable(Dictionary<Integer, String> bucketTable) {
        this.bucketTable = bucketTable;
    }

    public Dictionary<String, ArrayList<String>> getFileDictionary() {
        return fileDictionary;
    }

    public void setFileDictionary(Dictionary<String, ArrayList<String>> fileDictionary) {
        this.fileDictionary = fileDictionary;
    }

    public ArrayList<Neighbour> getMyNodeList() {
        return myNodeList;
    }

    public void setMyNodeList(ArrayList<Neighbour> myNodeList) {
        this.myNodeList = myNodeList;
    }
    
    // 
    
    public void initialize(){
    //call bs
    //join bs
    //
        
    }
    
    public void fileSearch(String fileName){
    
    
    }
    
    public void findNodeFromBucket(int bucketId) throws UnknownHostException, IOException{
        String message= "FIND_BUCKET_MEMBER "+bucketId;
        multicast(message,myNodeList);
    }
    
    
    public void findNodeFromBucketReply(int bucketId,Neighbour fromNode) throws UnknownHostException, IOException{
        String nodeFromBucket=null;
        if(bucketTable.get(bucketId)!=null){
            nodeFromBucket= bucketTable.get(bucketId);
        }
        unicast("FOUND_BUCKET_MEMBER "+nodeFromBucket+" "+this.getIp()+" "+this.getPort(),fromNode);
    }
    
    public void multicast(String message,ArrayList<Neighbour> neighboursList) throws SocketException, UnknownHostException, IOException{
        DatagramSocket datagramSocket = new DatagramSocket();
        for (Neighbour neighbour : neighboursList) {
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(neighbour.getIp());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 80);
            datagramSocket.send(packet);
        }
    }
    
    public void unicast(String message,Neighbour neighbour) throws SocketException, UnknownHostException, IOException{
        DatagramSocket datagramSocket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        InetAddress receiverAddress = InetAddress.getByName(neighbour.getIp());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 80);
        datagramSocket.send(packet);
    }
    //gracefull leave
    public void leave(){
    
    
    }
    
    
}
