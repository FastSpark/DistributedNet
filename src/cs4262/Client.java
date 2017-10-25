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
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ArrayList<String> myFileList;
    private ArrayList<Node> myNodeList;
    private Timestamp timestamp;
    private DatagramSocket ds;
    Scanner scanner = new Scanner(System.in);

    public Client(int k, int myBucketId, String ip, int port, String username, Map<String, ArrayList<String>> fileDictionary, ArrayList<String> myFileList, DatagramSocket datagramSocket) throws SocketException {
        this.k = k; // get from main
        this.myBucketId = myBucketId;
        this.status = "0";
        this.ip = ip;
        this.port = port;
        this.userName = username;
        this.bucketTable = new HashMap<>();
        this.fileDictionary = fileDictionary;
        this.myFileList = myFileList;
        this.myNodeList = new ArrayList<>();
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.ds = datagramSocket;
    }

    public DatagramSocket getDatagramSocket() {
        return ds;
    }

    public void setDatagramSocket(DatagramSocket ds) {
        this.ds = ds;
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
    public void handleRegisterResponse(String msg) throws IOException {
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
//                        findNodeFromBucket(i);   //handle exceptions
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
        //while (true) {
        displayFiles();
        System.out.println("");
            System.out.print("Input Next Command : ");

            msg = scanner.nextLine();
            
            switch (msg.split(" ")[0]) {
                case "DISPLAY_FILES":
                    displayFiles();
                    break;
                case "DISPLAY_TABLE":
                    displayRoutingTable();
                    break;
                case "SEARCH_FILES":
                    initializeSearch(msg);
                    break;
                default:
                    break;
                  
            }
       // }

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
        System.out.println("Files in ");
        this.myFileList.forEach((file) -> {
            System.out.println(file);
        });
    }

    public void displayRoutingTable() {
        if (myNodeList.isEmpty() && bucketTable.isEmpty()) {
            System.out.println("Tables are empty");
        } else {
            System.out.println("Nodes list in the Bucket:");
            for (Node node : myNodeList) {
                System.out.println("\t" + node.getIp() + ":" + node.getPort());
            }

            System.out.println("Nodes list from other Buckets:");
            Iterator entries = bucketTable.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Integer key = (Integer) entry.getKey();
                Node node = (Node) entry.getValue();
                System.out.println("Bucket " + key + " : " + node.getIp() + ":" + node.getPort());
            }
        }
    }

    public void initializeSearch(String msg) throws IOException{
        //SEARCH_FILES file_name
        String file_name= msg.split(" ")[1];
        String result_string="";
        
        //length SEROK no_files IP port hops filename1 filename2 ... ...
        ArrayList<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile(".*\\\\b"+file_name+"\\\\b.*");
        Set<String> keys = fileDictionary.keySet();
        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {
            String candidate = iterator.next();
            Matcher m = p.matcher(candidate);
            if (m.matches()) {
                results.add(candidate);
                result_string.concat(candidate+" ");
            }
        }
        System.out.println(result_string); 
        
        /////////
        String net_message="SER "+this.getIp()+" "+this.getPort()+" "+msg.split(" ")[1]+" 1";
        net_message = String.format("%04d", net_message.length() + 5) + " " + net_message;
        searchFiles(net_message);
    }
    
    public void searchFiles(String message) throws UnknownHostException, IOException {
        //length SER IP port file_name hops
        String[] split = message.split(" ");
        String file_name= split[4];
        String result_string="";
        
        int hop_count=0;
        if(split.length==6) 
            hop_count=Integer.valueOf(split[5]);
        
        //length SEROK no_files IP port hops filename1 filename2 ... ...
        ArrayList<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile("[a-zA-Z]*["+file_name+"][a-zA-Z]*");
        Set<String> keys = fileDictionary.keySet();
        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {
            String candidate = iterator.next();
            Matcher m = p.matcher(candidate);
            if (m.matches()) {
                results.add(candidate);
                result_string.concat(candidate+" ");
            }
        }
        
        String ret_message= "SEROK "+results.size()+" "+this.getIp()+" "+this.getPort()+" "+(hop_count++)+" "+result_string;
        ret_message = String.format("%04d", ret_message.length() + 5) + " " + ret_message;
        unicast(ret_message, new Node(split[2], Integer.parseInt(split[3])));
    }   

    public void findNodeFromBucket(int bucketId) throws UnknownHostException, IOException {
        //FBM: Find Bucket Member 0011 FBM 01
        String message = "FBM " + bucketId;
        message = String.format("%04d", message.length() + 5) + " " + message;
        multicast(message, myNodeList);
    }

    public void findNodeFromBucketReply(int bucketId, Node fromNode) throws UnknownHostException, IOException {
        //FBMOK: Find Bucket Member OK
        Node nodeFromBucket = null;
        String message = null;
        if (bucketTable.get(bucketId) != null) {
            nodeFromBucket = bucketTable.get(bucketId);
            message = "FBMOK " + bucketId + "" + nodeFromBucket.getIp() + " " + nodeFromBucket.getPort();
        } else {
            message = "FBMOK " + bucketId + " null null";
        }
        message = String.format("%04d", message.length() + 5) + " " + message;
        unicast(message, fromNode);
    }

    public void receiveReplyFindNodeFromBucket(String message) throws UnknownHostException, IOException {
        String[] split_msg = message.split(" ");
        Node bucket_node = new Node(split_msg[3], Integer.valueOf(split_msg[4]));
        this.bucketTable.put(Integer.valueOf(split_msg[2]), bucket_node);
    }

    public void multicast(String message, ArrayList<Node> nodesList) throws SocketException, UnknownHostException, IOException {
        for (Node node : nodesList) {
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(node.getIp());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, this.port);
            ds.send(packet);
        }
    }

    public void unicast(String message, Node node) throws SocketException, UnknownHostException, IOException {
        byte[] buffer = message.getBytes();
        InetAddress receiverAddress = InetAddress.getByName(node.getIp());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, this.port);
        ds.send(packet);
    }

    //gracefull leave
    public void leave(int bucketId) throws IOException {
        String message = "LEAVING BUCKET " + bucketId;
        multicast(message, myNodeList);
    }

    public void updateRountingTable() throws IOException {
        ArrayList<Node> temNodeList = new ArrayList<Node>();
        for (Node node : myNodeList) {
            if (timestamp.getTime() - node.getTimeStamp() < 5000) {
                temNodeList.add(node);
            }
        }
        this.myNodeList = temNodeList;

        for (int key : bucketTable.keySet()) {

            Node neighbour = bucketTable.get(key);
            if (timestamp.getTime() - neighbour.getTimeStamp() > 5000) {
                bucketTable.remove(key);
                this.findNodeFromBucket(key);
            }
        }

    }

    public void handleHeartBeatResponse(String message) {
        //length HEARTBEATOK IP_address port_no
        boolean is_Change = false;
        ArrayList<Node> temNodeList = new ArrayList<Node>();
        String[] splitMessage = message.split(" ");
        String ip = splitMessage[2];
        int port = Integer.parseInt(splitMessage[3]);
        for (Node node : myNodeList) {
            if (node.getIp().equals(ip) && node.getPort() == port) {
                node.setTimeStamp(timestamp.getTime());
                is_Change = true;
            }
            temNodeList.add(node);
        }
        this.myNodeList = temNodeList;

        if (!is_Change) {
            for (int key : bucketTable.keySet()) {
                Node node = bucketTable.get(key);
                if (node.getIp().equals(ip) && node.getPort() == port) {
                    node.setTimeStamp(timestamp.getTime());
                    bucketTable.replace(key, node);
                }
            }
        }
    }
}
