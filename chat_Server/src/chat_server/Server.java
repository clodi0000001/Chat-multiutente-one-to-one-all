/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_server;

/**
 *
 * @author Sony
 */
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
private static Set<GestioneChat> usersList = new HashSet<>();
    public static void main(String[] args) throws Exception {
        System.out.println("Il server di chat è in funzione...");
        ExecutorService pool = Executors.newFixedThreadPool(100);

        try (ServerSocket listener = new ServerSocket(5678)) {
            while (true) {
                GestioneChat newUser = new GestioneChat(listener.accept());
                pool.execute(newUser);
                usersList.add(newUser);
            }
        }
    }
}

 
