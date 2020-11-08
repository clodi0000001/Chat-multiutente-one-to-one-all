/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_client;

/**
 *
 * @author Sony
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * @author Sambula
 */

public class Client {

    String host;
    private int port;  
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("CHAT");
    JTextArea messageArea = new JTextArea(30, 50);
    JTextField textField = new JTextField(50);
/**
 * 
 * @param host indirizzo del client
 * @param port porta del client
 */
  
    public Client(String host, int port) {
        this.host = host;
        this.port = port;

        textField.setEditable(true);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
     
    }
/**
 * @see finestra della chat
 * @throws IOException 
 */
    private void run() throws IOException {
        try {
            Socket socket = new Socket(host, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) 
            {
                String line = in.nextLine();
                messageArea.append(line + "\n");
               
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }
/**
 * <p> assegnazione del host e porta </p>
 * @param args
 * @throws Exception 
 */
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int portNumber = 5678;
     
    if (args.length < 2) 
    {
      System.out.println("Default host = " + host + " \n Default port = " + portNumber);
      Client client = new Client(host, portNumber);
      
      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      client.frame.setVisible(true);
      client.run();
    } 
    else 
    {
       host = args[0];
       portNumber = Integer.valueOf(args[1]);
       Client client = new Client(host, portNumber);
       
       client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       client.frame.setVisible(true);
       client.run();
    }
        
    }
}

