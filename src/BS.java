
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Malith
 */
public class BS extends javax.swing.JFrame {

    /**
     * Creates new form BS
     */
    public BS() {
        initComponents();

    }

    //simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Boostrap Server DS sys");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Bootstrap Server");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(126, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addGap(28, 28, 28))
        );

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Stop ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Thread bs = new Thread(new Runnable() {
            public void run() {
                DatagramSocket sock = null;
                String s;
                List<Neighbour> nodes = new ArrayList<Neighbour>();

                try {
                    sock = new DatagramSocket(55555);

                    echo("Bootstrap Server created at 55555. Waiting for incoming data...");

                    while (true) {
                        byte[] buffer = new byte[65536];
                        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                        sock.receive(incoming);

                        byte[] data = incoming.getData();
                        s = new String(data, 0, incoming.getLength());

                        //echo the details of incoming data - client ip : client port - client message
                        echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

                        StringTokenizer st = new StringTokenizer(s, " ");

                        String length = st.nextToken();
                        String command = st.nextToken();

                        if (command.equals("REG")) {
                            String reply = "REGOK ";

                            String ip = st.nextToken();
                            int port = Integer.parseInt(st.nextToken());
                            String username = st.nextToken();
                            if (nodes.size() == 0) {
                                reply += "0";
                                nodes.add(new Neighbour(ip, port, username));
                            } else {
                                boolean isOkay = true;
                                for (int i = 0; i < nodes.size(); i++) {
                                    if (nodes.get(i).getPort() == port) {
                                        if (nodes.get(i).getUsername().equals(username)) {
                                            reply += "9998";
                                        } else {
                                            reply += "9997";
                                        }
                                        isOkay = false;
                                    }
                                }
                                if (isOkay) {
                                    if (nodes.size() == 1) {
                                        reply += "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
                                    } else if (nodes.size() == 2) {
                                        reply += "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " " + nodes.get(1).getIp() + " " + nodes.get(1).getPort();
                                    } else {
                                        Random r = new Random();
                                        int Low = 0;
                                        int High = nodes.size();
                                        int random_1 = r.nextInt(High - Low) + Low;
                                        int random_2 = r.nextInt(High - Low) + Low;
                                        while (random_1 == random_2) {
                                            random_2 = r.nextInt(High - Low) + Low;
                                        }
                                        reply += "2 " + nodes.get(random_1).getIp() + " " + nodes.get(random_1).getPort() + " " + nodes.get(random_2).getIp() + " " + nodes.get(random_2).getPort();
                                    }
                                    nodes.add(new Neighbour(ip, port, username));
                                }
                            }
                            reply = String.format("%04d", reply.length() + 5) + " " + reply;
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, incoming.getAddress(), incoming.getPort());
                            sock.send(dpReply);
                        } else if (command.equals("UNREG")) {
                            String ip = st.nextToken();
                            int port = Integer.parseInt(st.nextToken());
                            String username = st.nextToken();
                            for (int i = 0; i < nodes.size(); i++) {
                                if (nodes.get(i).getPort() == port) {
                                    nodes.remove(i);
                                    String reply = "0012 UNROK 0";
                                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, incoming.getAddress(), incoming.getPort());
                                    sock.send(dpReply);
                                }
                            }
                        } else if (command.equals("ECHO")) {
                            for (int i = 0; i < nodes.size(); i++) {
                                echo(nodes.get(i).getIp() + " " + nodes.get(i).getPort() + " " + nodes.get(i).getUsername());
                            }
                            String reply = "0012 ECHOK 0";
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, incoming.getAddress(), incoming.getPort());
                            sock.send(dpReply);
                        }

                    }
                } catch (IOException e) {
                    System.err.println("IOException " + e);
                }
            }
        });
        bs.start();


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        int res = JOptionPane.showConfirmDialog(this, "Stop the Server?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BS().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
