/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 *
 * @author Sony
 */
public class GestioneChat implements Runnable {
   private static final Set<String> users = new HashSet<>();
    private static final Set<PrintWriter> writers = new HashSet<>();
    private static Set<GestioneChat> usersList = new HashSet<>();
       private Socket socket;
        private String name;
        private Scanner in;
        private PrintWriter out;

       
        public GestioneChat(Socket socket) {
            this.socket = socket;
        }

        public void printUsers()
        {
            if(users != null && !users.isEmpty())
            {
                out.println("\n ------------ ");
                out.println(" LIST " + users.size());
                for (String username : users) 
                    out.println(" - "+ username);
            }   
            else
                out.println("Non c'è ancora nessun utente");
        }
      
        public void printPrivateMsg(String private_msg)
        {
            if (private_msg.contains("[") && private_msg.contains("]"))
                {
                  String[] parts = private_msg.split("]"); //dividere i nomi e il messaggio
                  parts[0] = parts[0].substring(1);        // tagliare il simbolo [
                  String[] private_users = parts[0].split(" "); // nomi utente divisi
                  String message = parts[1];              //salva il messaggio privato da inviare
                        for(String pr_user : private_users)
                            {
                            if(users.contains(pr_user))
                            {
                               for(GestioneChat user : usersList)
                               {
                                 if(user.name.equals(pr_user))
                                 {  
                                     user.out.println(" Messaggio privato ");
                                     user.out.println(user.name + ": " + message);                                
                                 }
                                }
                            }
                            else out.println("utente "+ pr_user + " non esiste");
                        }                            
                }
            else 
                out.println("Si prega di utilizzare [ ] per indicare gli utenti");   
        }
        
        
        
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("\n REGOLE/COMANDI DI FUNZIONAMENTO DELLA CHAT ");
                    out.println(" ------------------------------------------------------------------ \n");
                    out.println(" 1) Inviare messaggi pubblici con la seguente sintassi MSG 'testo' " +"\n"+" 2)Per inviare messaggi privati scrivere PRV [user1 user2] 'testo' " + "\n" + " 2)Per vivere la chat è sufficiente digitare BYE" + "\n 3)Per sapere chi è oline nella chat basta digitare WHO \n");
                    out.println(" ------------------------------------------------------------------ \n");
                    out.println(" Inserisci il nickname con la seguente sintassi IAM username ");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (users) {                        
                        if ( name != null  && name.startsWith("IAM")) 
                        {
                            name = name.substring(4);
                            if(users.contains(name))
                               out.println("Questo nickname è già stato preso");
                            else if(name.contains(" ")) 
                            {                                                                
                                out.println("Scrivi un nome utente senza spazio");
                            }                                
                            else
                            {  
                                users.add(name);
                                break;
                            }
                        }                       
                        else if(name != null && name.equals("  Rinserisci il nickname con la seguente sintassi IAM username "))
                        {
                            printUsers();
                        }
                    }
                }

                out.println(" BENVENUTO NELLA CHAT PUBBLICA --> utente: " + name);
                for (PrintWriter writer : writers)
                {
                    //questa scritta si vede nella finestra dell'utenti che sono gia presenti alla chat, colui che si conette per sepere chi è conesso basta scrivere who
                    writer.println(" Entrato l'utente: " + name );
                }
                // questo scritta si vedra nella output del server che indica chi è etratto/conesso
                System.out.println(name + " joined the chat");
                writers.add(out);

                while (true) 
                {
                    String input = in.nextLine();
                    //con bye permettrea di uscire dall chat
                    if (input.equals("BYE")) 
                    {
                        out.println("GOOD BYE" );
                        //System.exit(0);
                            return;
                    }
                    // INVII IL MESSAGIO A TUTTI
                    else if(input.startsWith("MSG"))
                    {
                        for (PrintWriter writer : writers) 
                        {
                            //if(writer != this.out)
                                writer.println(" < "+ name + " > : " + input.substring(4));
                        }
                    }
                     else if(input.startsWith("PRV"))
                     {
                         String private_msg = input.substring(4);
                        printPrivateMsg(private_msg);                
                                 }
                     //Lista dei utenti connesi in questa chat
                     else if(input.equals("WHO"))
                     {
                       printUsers();
                     }
                    else    
                    {
                          out.println(" ***MESSAGGIO SCONOSCIUTO*** " );       
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                    usersList.remove(this);
                }
                if (name != null) 
                {
                    users.remove(name);
                    for (PrintWriter writer : writers) 
                    {
                        writer.println(" Utente: " + name + " è uscito ");
                    }
                    //questo scritta si vedra nella output del server che indica chi è uscito/disconesso
                   System.out.println(name + " has left the chat");
                 
                }
                try 
                { 
                    socket.close(); 
                } 
                catch (IOException e)
                {
                    System.out.println(e);
                }
            }
        }
} 

    

