import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.cli.*;

public class Driver {

    private static ArrayList<Thread> connections = new ArrayList<Thread>();
    private static BufferedWriter bw = null;
    private static ArrayList<Account> accountList = new ArrayList<Account>();







    public static void main(String[] args){

        try{
            bw = new BufferedWriter(new FileWriter("StarterLog.txt",true));//the true will append the new data

        }catch(IOException e){
            System.err.println("IOException: " + e.getMessage());
        }


        loadAccounts();



        System.out.println("Server: Welcome to the server.");

        boolean sentinel = true;

        Scanner keyboard = new Scanner(System.in);


        Thread currentCL = null;


        while(sentinel){
            System.out.println(">");
            String input = keyboard.nextLine();
            switch(input){

                case "killAll":

                    killAll();

                    break;

                //I want to change this to kill by ID, or add kill by id eventually
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
                    killAll();
                    while(connections.size()!=0){
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }


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
        try {
            bw.close();
        }catch(IOException e){e.printStackTrace();}



    }

    public static void log(String message){
        try {
            bw.write(message);
            bw.newLine();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void removeConnection(Thread thread){
        connections.remove(thread);
    }

    public static void addConnection(Thread thread){
        connections.add(thread);
    }

    private static void killAll(){
        while(connections.size() != 0){
            connections.get(0).interrupt();
        }
    }

    private static void loadAccounts(){
        try {
            BufferedReader in = new BufferedReader(new FileReader("AccountsSaved.txt"));
            String line;
            String[] accountAttributes = new String[2];
            while ((line = in.readLine()) != null) {
                //use String file here
                accountAttributes[0] = line;//username
                line = in.readLine();
                accountAttributes[1] = line;//passwordHash
                accountList.add(new Account(accountAttributes[0],accountAttributes[1]));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
