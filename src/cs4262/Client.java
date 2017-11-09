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
import java.util.HashSet;
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

    private int k; //number of buckets
    private int myBucketId;
    private String status; //whether node is intializing or up
    private String ip;
    private int port;
    private String userName; //hash(ip:port)
    private Map<Integer, Node> bucketTable; //bucket and the node I know from that bucket
    private Map<String, ArrayList<String>> fileDictionary; //filename: nodelist
    private ArrayList<String> myFileList; //filenames with me
    private ArrayList<Node> myNodeList; //nodes in my bucket
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

        this.myNodeList.add(new Node(this.ip, this.port));
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
                this.displayRoutingTable();
                this.status = "1";
                break;
            case "1":
                storeNode(arr[3], arr[4]);
                this.displayRoutingTable();
                this.status = "1";
                break;
            case "2":
                storeNode(arr[3], arr[4]);
                storeNode(arr[5], arr[6]);

                // complete bucketTable (including my own bucket if it's empty)
                for (int i = 0; i < k; i++) {
                    if (!bucketTable.containsKey(i)) {
                        findNodeFromBucket(i);
                    }
                }

                // time out to complete receiving replies for findNodeFromBucket
                try {
                    Thread.sleep(8000);  // Tune this

                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (!bucketTable.containsKey(this.myBucketId)) { // if I'm only the node in my bucket no need to wait for myNodeList to populate
                    System.out.println("I'm the only node in my bucket");
                    this.displayRoutingTable();
                    this.status = "1";
                } else if (this.myNodeList.size() == 1) { // if heven't receive a node in same bucket and haven't called findMyNodeListFromNode inside storeNode method
                    // request myNodeList from bucketTable.get(this.myBucketId)
                    this.findMyNodeListFromNode(this.bucketTable.get(this.myBucketId));
                }
//                System.out.println("###################");
//                this.displayRoutingTable();
//                this.status = "1";

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
                break;
        }
    }

    private void storeNode(String ip, String port) throws IOException {
        Node newNode = new Node(ip, Integer.parseInt(port));
        int bucketId = Math.abs((ip + ":" + port).hashCode()) % k;
        bucketTable.put(bucketId, newNode);
        if (bucketId == this.myBucketId) {
            // request myNodeList from that node 
            this.findMyNodeListFromNode(newNode);
        }
    }

    // indicate that I'm new to net
    // send file list with this
    private void connectWithNodes() throws UnknownHostException, IOException {
        String fileList = "";
        for (int i = 0; i < this.myFileList.size(); i++) {
            fileList += myFileList.get(i);
        }
        String message = "CWN " + fileList + " " + this.ip + ":" + Integer.toString(this.port);
        message = String.format("%04d", message.length() + 5) + " " + message;

        multicast(message, myNodeList);
    }

    public void connectWithNodesResponse() {

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

    public void initializeSearch(String msg) throws IOException {
        //SEARCH_FILES file_name
        String file_name = msg.split(" ")[1];
        String result_string = "";

        //length SEROK no_files IP port hops filename1 filename2 ... ...
//        ArrayList<String> results = new ArrayList<String>();
//        Pattern p = Pattern.compile(".*\\\\b"+file_name+"\\\\b.*");
//        Set<String> keys = fileDictionary.keySet();
//        Iterator<String> iterator = keys.iterator();
//
//        while (iterator.hasNext()) {
//            String candidate = iterator.next();
//            Matcher m = p.matcher(candidate);
//            if (m.matches()) {
//                results.add(candidate);
//                result_string.concat(candidate+" ");
//            }
//        }
//        System.out.println(result_string); 
        String net_message = "SER " + this.getIp() + " " + this.getPort() + " " + msg.split(" ")[1] + " 1";
        net_message = String.format("%04d", net_message.length() + 5) + " " + net_message;
        searchFiles(net_message);
    }

    public void searchFiles(String message) throws UnknownHostException, IOException {
        //length SER IP port file_name hops
        String[] split = message.split(" ");
        String file_name = split[4];
        String result_string = "";
        String source_ip = split[2];
        int source_port = Integer.parseInt(split[3]);

        int hop_count = 0;
        if (split.length == 6) {
            hop_count = Integer.valueOf(split[5]);
        }

        //length SEROK tofind no_files IP port hops filename1 filename2 ... ...
        ArrayList<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile("[a-zA-Z\\s]*" + file_name + "[a-zA-Z\\s]*");

        Set<String> keys = new HashSet<>(myFileList);
        Iterator<String> iterator = keys.iterator();

        ArrayList<String> nodes = new ArrayList<>();
        ArrayList<Node> nodelist = new ArrayList<>();

        //search in my files list
        while (iterator.hasNext()) {
            String candidate = iterator.next();
            Matcher m = p.matcher(candidate);
            if (m.matches()) {
                results.add(candidate);
                System.out.println(candidate);
                result_string = result_string.concat(candidate + ",");
            }
        }
        if (results.size() > 0) {
            String ret_message = "SEROK " + file_name + " " + results.size() + " " + this.getIp() + " " + this.getPort() + " " + (hop_count++) + " " + result_string;
            ret_message = String.format("%04d", ret_message.length() + 5) + " " + ret_message;
            System.out.println(ret_message);
            unicast(ret_message, new Node(source_ip, source_port));
        } else {

            keys = fileDictionary.keySet();
            iterator = keys.iterator();

            boolean found = false;

            while (iterator.hasNext()) {
                String candidate = iterator.next();
                Matcher m = p.matcher(candidate);
                if (m.matches()) {
                    nodes = fileDictionary.get(candidate);
                    for (String node : nodes) {
                        nodelist.add(new Node(node.split(":")[0], Integer.parseInt(node.split(":")[1])));
                    }
                    multicast(message, nodelist);
                }
                found = true;
            }

            if (!found) {
                multicast(message, new ArrayList<Node>(bucketTable.values()));
            }
        }
    }

    public void findNodeFromBucket(int bucketId) throws UnknownHostException, IOException {
        //FBM: Find Bucket Member 0011 FBM 01
        String message = "FBM " + bucketId + " " + this.ip + ":" + Integer.toString(this.port);
        message = String.format("%04d", message.length() + 5) + " " + message;

        // request from available my nodes
        multicast(message, myNodeList);

        // request from nodes from other buckets
        for (int i = 0; i < k; i++) {
            if (this.bucketTable.containsKey(i) && i != this.myBucketId) {
                unicast(message, this.bucketTable.get(i));
            }
        }
    }

    public void findNodeFromBucketReply(int bucketId, Node fromNode) throws UnknownHostException, IOException {
        //FBMOK: Find Bucket Member OK
        Node nodeFromBucket = null;
        String message = null;
        if (bucketTable.get(bucketId) != null) {
            nodeFromBucket = bucketTable.get(bucketId);
            message = "FBMOK " + bucketId + " " + nodeFromBucket.getIp() + " " + nodeFromBucket.getPort();
        } else {
            message = "FBMOK " + bucketId + " null null";
        }
        message = String.format("%04d", message.length() + 5) + " " + message;
        unicast(message, fromNode);
    }

    public void receiveReplyFindNodeFromBucket(String message) throws UnknownHostException, IOException {

        String[] split_msg = message.split(" ");
        if ("null".equals(split_msg[3])) {
            return;
        }
        Node bucket_node = new Node(split_msg[3], Integer.valueOf(split_msg[4]));

        this.bucketTable.put(Integer.valueOf(split_msg[2]), bucket_node);

        // Node is still initializing and the returned node is a node from my bucket
        if (this.status.equals("0") && split_msg[2].equals(this.myBucketId)) {
            // request myNodeList from that node
            this.findMyNodeListFromNode(bucket_node);
        }
    }

    public void findMyNodeListFromNode(Node node) throws UnknownHostException, IOException {
        String fileList = " ";
        for (int i = 0; i < this.myFileList.size(); i++) {
            fileList += myFileList.get(i) + ":";
        }
        //FNL: Find Node List
        String message = "FNL" + " " + this.ip + ":" + Integer.toString(this.port) + fileList;
        message = String.format("%04d", message.length() + 5) + " " + message;
        unicast(message, node);
    }

    public void findMyNodeListFromNodeReply(Node fromNode) throws UnknownHostException, IOException {
        String message = "FNLOK ";
        for (int i = 0; i < this.myNodeList.size(); i++) {
            message += this.myNodeList.get(i).getIp() + ":" + Integer.toString(this.myNodeList.get(i).getPort()) + " ";
        }
        message = String.format("%04d", message.length() + 5) + " " + message;
        unicast(message, fromNode);

        // add that new node to myNodeList
        Boolean isAlreadyInMyNodeList = false;
        // ignore if it's already in myNodeList
        for (int j = 0; j < myNodeList.size(); j++) {
            if (myNodeList.get(j).getIp().equals(fromNode.getIp()) && myNodeList.get(j).getPort() == fromNode.getPort()) {
                isAlreadyInMyNodeList = true;
            }
        }
        if (!isAlreadyInMyNodeList) {
            this.myNodeList.add(fromNode);
        }

        // get file list of that new node and store in fileDictionary
    }

    public void receiveReplyfindMyNodeListFromNode(String message) throws UnknownHostException, IOException {
        String[] split_msg = message.split(" ");
        int numOfNodes = split_msg.length - 2;
        for (int i = 0; i < numOfNodes; i++) {
            String[] nodeDetails = split_msg[i + 2].split(":");

            Boolean isAlreadyInMyNodeList = false;
            // ignore if it's already in myNodeList
            for (int j = 0; j < myNodeList.size(); j++) {
                if (myNodeList.get(j).getIp().equals(nodeDetails[0]) && myNodeList.get(j).getPort() == Integer.valueOf(nodeDetails[1])) {
                    isAlreadyInMyNodeList = true;
                }
            }
            if (!isAlreadyInMyNodeList) {
                Node nodeInList = new Node(nodeDetails[0], Integer.valueOf(nodeDetails[1]));
                this.myNodeList.add(nodeInList);
            }
        }
        this.displayRoutingTable();
        this.status = "1";
    }

    public void multicast(String message, ArrayList<Node> nodesList) throws SocketException, UnknownHostException, IOException {
        for (Node node : nodesList) {
            if (node.getIp().equals(this.ip) && node.getPort() == this.port) { // ignore sending own
                continue;
            }
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(node.getIp());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, node.getPort());
            ds.send(packet);
        }
    }

    public void unicast(String message, Node node) throws SocketException, UnknownHostException, IOException {
        byte[] buffer = message.getBytes();
        InetAddress receiverAddress = InetAddress.getByName(node.getIp());
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, node.getPort());
        ds.send(packet);
    }

    //gracefull leave
    //send leave message to bootrap server
    public void leave() throws IOException {

        String message = "LEAVE " + this.getIp() + " " + this.getPort();
        message = String.format("%04d", message.length() + 5) + " " + message;
        sendMessage(message);

    }

    // handle leave ok from bootrap server
    public void handleLeaveOk(String message) throws UnknownHostException, IOException {

        int messageType = Integer.parseInt(message.split(" ")[2]);
        if (messageType == 0) {
            String sendMeessage = "LEAVE " + this.getIp() + " " + this.getPort();
            message = String.format("%04d", sendMeessage.length() + 5) + " " + sendMeessage;
            multicast(sendMeessage, myNodeList);
        } else if (messageType == 9999) {
            System.out.println("error while adding new node to routing table");
        }

    }

    public void handleLeave(String message) throws UnknownHostException, IOException {
        String[] splitMesseageList = message.split(" ");
        String ip = splitMesseageList[2];
        int port = Integer.parseInt(splitMesseageList[3]);
        // leave wena eka nodelist eken ain karan ona eke ekek nm.
        ArrayList<Node> tem = new ArrayList<>();
        for (Node node : myNodeList) {
            if (!node.getIp().equals(ip) && node.getPort() != port) {
                tem.add(node);
            } else {
                for (String file : fileDictionary.keySet()) {
                    ArrayList<String> temFileNodeList = new ArrayList<String>();
                    for (String username : fileDictionary.get(file)) {
                        String[] split = username.split(":");
                        String temIp = split[0];
                        int temPort = Integer.parseInt(split[1]);
                        if (temIp != ip && temPort != port) {
                            temFileNodeList.add(username);
                        }
                    }
                    fileDictionary.replace(file, temFileNodeList);
                }
            }
        }
        for (int key : bucketTable.keySet()) {
            Node neighbour = bucketTable.get(key);
            if (neighbour.getIp() == ip && neighbour.getPort() == port) {
                bucketTable.remove(key);
                this.findNodeFromBucket(key);
            }
        }

    }

    public void updateRountingTable() throws IOException {
        ArrayList<Node> temNodeList = new ArrayList<>();
        for (Node node : myNodeList) {
            if (new Timestamp(System.currentTimeMillis()).getTime() - node.getTimeStamp() < 5000) {
                temNodeList.add(node);
            } else {
                for (String file : fileDictionary.keySet()) {
                    ArrayList<String> temFileNodeList = new ArrayList<String>();
                    for (String username : fileDictionary.get(file)) {
                        String[] split = username.split(":");
                        String ip = split[0];
                        int port = Integer.parseInt(split[1]);
                        if (ip != node.getIp() && port != node.getPort()) {
                            temFileNodeList.add(username);
                        }

                    }
                    fileDictionary.replace(file, temFileNodeList);
                }

            }
        }

        System.out.println("myNodeList " + myNodeList.size());
        System.out.println("myTemList " + temNodeList.size());
        this.myNodeList = temNodeList;
        for (int key : bucketTable.keySet()) {
            Node neighbour = bucketTable.get(key);
            System.out.println("time now" + new Timestamp(System.currentTimeMillis()).getTime());
            System.out.println("neighour time :" + neighbour.getTimeStamp());
            System.out.println("time to response in bucket table " + (new Timestamp(System.currentTimeMillis()).getTime() - neighbour.getTimeStamp()));
            if (new Timestamp(System.currentTimeMillis()).getTime() - neighbour.getTimeStamp() > 5000) {
                System.out.println("time to response in bucket table " + (timestamp.getTime() - neighbour.getTimeStamp()));
                System.out.println("before remove" + bucketTable.keySet());
                bucketTable.remove(key);
                System.out.println("after remove" + bucketTable.keySet());
                this.findNodeFromBucket(key);
            }

        }
        // if my bucket table does not have connect ti some bucket we need to update that
        Set<Integer> keySet = bucketTable.keySet();
        for (int i = 0; i < this.k; i++) {
            if (!keySet.contains(i) && i != this.myBucketId) {
                this.findNodeFromBucket(i);
            }
        }

        displayRoutingTable();

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
                node.setTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
                is_Change = true;
            }
            temNodeList.add(node);
        }
        this.myNodeList = temNodeList;

        if (!is_Change) {
            for (int key : bucketTable.keySet()) {
                Node node = bucketTable.get(key);
                if (node.getIp().equals(ip) && node.getPort() == port) {
                    node.setTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
                    bucketTable.replace(key, node);
                }
            }
        }
    }

    public void sendHeartBeatReply(String message) throws IOException {
        String newMessage = "HEARTBEATOK " + this.getIp() + " " + this.getPort();
        newMessage = String.format("%04d", newMessage.length() + 5) + " " + newMessage;
        String[] splitMessage = message.split(" ");
        Node node = new Node(splitMessage[2], Integer.parseInt(splitMessage[3]));
        unicast(newMessage, node);
    }
}
