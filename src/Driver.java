import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

    private static ArrayList<Thread> connections = new ArrayList<Thread>();//might want to rework the methods using this list to use an inUse flag
    private static BufferedWriter serverLog = null;
    private static ArrayList<Account> accountList = new ArrayList<Account>();
    private static boolean accountListInUse; //initialized in loadAccounts
    private static boolean accountsSaveFileInUse; //initialized in at beginning of main
    private static boolean connectionsListInUse;

    private static boolean sentinel;

    private static BufferedReader accountsSaveFileIn;
    private static BufferedWriter accountsSaveFileOut;


    private static Thread currentCL;




    public static void main(String[] args){

        try{
            serverLog = new BufferedWriter(new FileWriter("serverLog.txt",true));//the true will append the new data



        }catch(IOException e){
            System.err.println("IOException: " + e.getMessage());
        }
            accountsSaveFileInUse = false;//initial
            connectionsListInUse = false;

        loadAccounts();



        System.out.println("Server: Welcome to the server.");

        sentinel = true;

        Scanner keyboard = new Scanner(System.in);


        currentCL = null;


        while(sentinel){//parse input
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
                    killConnection(input);
                    break;

                case "listConnections":
                    listConnections();
                    break;


                case "open":

                    openConnectionListener();

                    break;

                case "quit":
                    quitServer();
                    break;

                case "close":
                    closeConnectionListener();
                    break;
            }

        }
        try {
            serverLog.close();

        }catch(IOException e){e.printStackTrace();}



    }

    public static void log(String message){//implement flag waiting
        try {
            serverLog.write(message);
            serverLog.newLine();
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
        while(connectionsListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        connectionsListInUse = true;

        while(connections.size() != 0){
            connections.get(0).interrupt();
        }
        connectionsListInUse = false;
    }

    private static void loadAccounts(){
        while(accountsSaveFileInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        accountsSaveFileInUse = true;
        try {
            accountsSaveFileIn = new BufferedReader(new FileReader("AccountsSaveFile.txt"));

            String line;
            String[] accountAttributes = new String[2];
            while ((line = accountsSaveFileIn.readLine()) != null) {
                //use String file here
                accountAttributes[0] = line;//username
                line = accountsSaveFileIn.readLine();
                accountAttributes[1] = line;//passwordHash
                accountList.add(new Account(accountAttributes[0],accountAttributes[1]));
            }
            accountsSaveFileIn.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        accountListInUse = false;
        accountsSaveFileInUse = false;
    }

    public static boolean createAccount(String name, String passwordHash){

        while(accountListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        accountListInUse = true;
        boolean duplicateName = false;
        try{
            for(int i = 0; i < accountList.size(); i++){
                if(accountList.get(i).getName().equals(name)){
                    duplicateName = true;
                }
            }
        }catch(NullPointerException e){
            e.printStackTrace();//I'm not yet convinced that the static variable wont be accessed during this call and create problems
            //perhaps i could make a static in use flag
            //edit: that is what I did lol
        }
        if(!duplicateName){
            accountList.add(new Account(name,passwordHash));
            while(accountsSaveFileInUse){
                try {
                    Thread.sleep(10);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            accountsSaveFileInUse = true;

            try{
                accountsSaveFileOut = new BufferedWriter(new FileWriter("AccountsSaveFile.txt",false));
                for(int i = 0; i < accountList.size(); i++){
                    accountsSaveFileOut.write(accountList.get(i).getName());
                    accountsSaveFileOut.newLine();
                    accountsSaveFileOut.write(accountList.get(i).getPasswordHash());
                    accountsSaveFileOut.newLine();


                }
                accountsSaveFileOut.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            accountsSaveFileInUse = false;

        }
        accountListInUse = false;
        return !duplicateName;

    }


    public static boolean deleteAccount(String name, String passwordHash){

        while(accountListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        accountListInUse = true;
        boolean foundIt = false;
        try{
            for(int i = 0; i < accountList.size(); i++){
                if(accountList.get(i).getName().equals(name)){
                    accountList.remove(i);

                    foundIt = true;
                    break;
                }
            }
        }catch(NullPointerException e){
            e.printStackTrace();//I'm not yet convinced that the static variable wont be accessed during this call and create problems
            //perhaps i could make a static in use flag
            //edit: that is what I did lol
        }

        if(foundIt){
            while(accountsSaveFileInUse){
                try {
                    Thread.sleep(10);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            accountsSaveFileInUse = true;

            try{
                accountsSaveFileOut = new BufferedWriter(new FileWriter("AccountsSaveFile.txt",false));
                for(int i = 0; i < accountList.size(); i++){
                    accountsSaveFileOut.write(accountList.get(i).getName());
                    accountsSaveFileOut.newLine();
                    accountsSaveFileOut.write(accountList.get(i).getPasswordHash());
                    accountsSaveFileOut.newLine();


                }
                accountsSaveFileOut.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            accountsSaveFileInUse = false;


        }

        accountListInUse = false;
        return foundIt;

    }

    public static void killConnection(String name){

        while(connectionsListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        connectionsListInUse = true;


        boolean matched = false;
        for(int i = 0; i < connections.size(); i++){
            if(connections.get(i).getName().equals(name)){
                matched = true;
                connections.get(i).interrupt();
                break;
            }
        }
        if(matched){
            System.out.println("Server: Deleted thread '"+ name +"'");
        }else{
            System.out.println("Server: No thread with the given name existed");
        }
        connectionsListInUse = false;
    }

    public static void listConnections(){
        while(connectionsListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        connectionsListInUse = true;

        System.out.println("Server: " + connections.size() + " connections to the server:");
        for(int i = 0; i < connections.size(); i++){
            System.out.println(connections.get(i).getName());
        }


        connectionsListInUse = false;
    }

    private static void openConnectionListener(){
        if(currentCL == null || !currentCL.isAlive()){
            currentCL = new Thread(new ConnectionListener());
            currentCL.start();
        } else{

            System.out.println("Server: Already open");

        }
    }

    private static void closeConnectionListener(){
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
                //System.out.println("Server: Already in the process of closing. Should close soon");
            }
        }else{
            // System.out.println("Server: Already closed");
        }
    }

    private static void quitServer(){
        sentinel = false;
        killAll();
        while(connections.size()!=0){
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        closeConnectionListener();
    }


}
