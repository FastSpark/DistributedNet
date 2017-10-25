/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs4262;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nuwantha
 */
public class Cs4262 {

    private static final int k = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //need to haddle 
        //start new listener
        //call initializer

        try {
            System.out.println("Main Thread Started");

            //start of final variables         
            final String[] fileList = {
                "Adventures of Tintin",
                "Jack and Jill",
                "Glee",
                "The Vampire Diarie",
                "King Arthur",
                "Windows XP",
                "Harry Potter",
                "Kung Fu Panda",
                "Lady Gaga",
                "Twilight",
                "Windows 8",
                "Mission Impossible",
                "Turn Up The Music",
                "Super Mario",
                "American Pickers",
                "Microsoft Office 2010",
                "Happy Feet",
                "Modern Family",
                "American Idol",
                "Hacking for Dummies"
            };
            //end of final variables

            //start of the input
            String ip = Inet4Address.getLocalHost().getHostAddress();
//            String ip = "192.168.43.252";
//            int port = 80;

            Scanner scanner = new Scanner(System.in);
            System.out.println("IP Address : " + ip);
            System.out.print("Input Port : ");
            String input = scanner.nextLine();
            int port = Integer.parseInt(input);
            //end of the input

            //start of creating connection details
            String address = ip + ":" + port;
            int myBucketId = address.hashCode();
            myBucketId = myBucketId % k;
            //end of getting connection details

            //start of initializing files (3 to 5)
            Map<String, ArrayList<String>> fileDictionary = new HashMap<>();

            int randomFileCount = new Random().nextInt(3) + 3;
            System.out.println("Initializing node with " + randomFileCount + " files...");
            ArrayList<String> myFileList = new ArrayList<>();

            for (int i = 0; i < randomFileCount; i++) {
                int randomIndex = new Random().nextInt(fileList.length);
                String selectedFile = fileList[randomIndex];
                myFileList.add(selectedFile);

                ArrayList<String> nodesContainingFile = fileDictionary.get(selectedFile);
                if (nodesContainingFile == null) {
                    nodesContainingFile = new ArrayList<>();
                }
                nodesContainingFile.add(address);
                fileDictionary.put(selectedFile, nodesContainingFile);
            }
            //end of initializing files

            DatagramSocket datagramSocket = new DatagramSocket(port);

            Client client = new Client(k, myBucketId, ip, port, address, fileDictionary, myFileList, datagramSocket);
            client.initialize();

            Thread thread = new Thread(new Listener(client));
            thread.start();

       // } catch (UnknownHostException ex) {
         //   Logger.getLogger(Cs4262.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Cs4262.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cs4262.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
