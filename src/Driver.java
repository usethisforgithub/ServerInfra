import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.cli.*;

public class Driver {

    static ArrayList<Thread> connections = new ArrayList<Thread>();



    public static void main(String[] args){
       






        System.out.println("Server: Welcome to the server.");

        boolean sentinel = true;

        Scanner keyboard = new Scanner(System.in);


        Thread currentCL = null;


        while(sentinel){
            System.out.println(">");
            String input = keyboard.nextLine();
            switch(input){

                case "killAll":

                    while(connections.size() != 0){
                        connections.get(0).interrupt();
                    }

                    break;


                case "killCon":
                    System.out.println("Thread to kill?");
                    input = keyboard.nextLine();
                    boolean matched = false;
                    for(int i = 0; i < connections.size(); i++){
                        if(connections.get(i).getName().equals(input)){
                            matched = true;
                            connections.get(i).interrupt();
                            break;
                        }
                    }
                    if(matched){
                        System.out.println("Server: Deleted thread '"+ input +"'");
                    }else{
                        System.out.println("Server: No thread with the given name existed");
                    }


                    break;

                case "listConnections":
                    System.out.println("Server: " + connections.size() + " connections to the server:");
                    for(int i = 0; i < connections.size(); i++){
                        System.out.println(connections.get(i).getName());
                    }
                    break;


                case "open":


                    if(currentCL == null || !currentCL.isAlive()){
                        currentCL = new Thread(new ConnectionListener());
                        currentCL.start();
                    } else{

                            System.out.println("Server: Already open");

                    }
                    break;

                case "quit":
                    sentinel = false;

                case "close":
                    if(currentCL != null && currentCL.isAlive()){
                        if(!currentCL.isInterrupted()){
                            currentCL.interrupt();

                            //connects to the socket to close the trailing connection
                            Socket socket = null;

                            try{
                                socket = new Socket("127.0.0.1", 1234);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("Server: Already in the process of closing. Should close soon");
                        }
                    }else{
                        System.out.println("Server: Already closed");
                    }
                    break;
            }

        }





    }

}
