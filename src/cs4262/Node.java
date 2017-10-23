/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

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
    private ArrayList<String> myNodeList;

    public Node(int myBucketId, String status, String ip, String port, String userName, Dictionary<Integer, String> bucketTable, Dictionary<String, ArrayList<String>> fileDictionary, ArrayList<String> myNodeList) {
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

    public ArrayList<String> getMyNodeList() {
        return myNodeList;
    }

    public void setMyNodeList(ArrayList<String> myNodeList) {
        this.myNodeList = myNodeList;
    }
    
    
    
    public void initialize(){
    //call bs
    //join bs
    //
        
    }
    
    public void fileSearch(String fileName){
    
    
    }
    
    public void findNodeFromBucket(int bucketId){
    
    }
    
    public void heartbeat(){
    //send and listen
    
    }
    //gracefull leave
    public void leave(){
    
    
    }
    
    
    
   
    
    
}
