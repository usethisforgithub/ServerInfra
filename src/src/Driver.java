import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

    private static ArrayList<Thread> connections = new ArrayList<Thread>();//might want to rework the methods using this list to use an inUse flag
    private static ArrayList<Account> accountList = new ArrayList<Account>();
    private static ArrayList<GameServerPA> gameServersList = new ArrayList<GameServerPA>();

    private static boolean sentinel;

    private static BufferedReader accountsSaveFileIn;
    private static BufferedWriter accountsSaveFileOut;
    private static BufferedWriter serverLog = null;

    private static Thread currentCL;

    private static boolean accountListInUse; //initialized in loadAccounts
    private static boolean accountsSaveFileInUse; //initialized in at beginning of main
    private static boolean connectionsListInUse;
    private static boolean serverLogInUse;
    private static boolean gameServersListInUse;




    public static void main(String[] args){

        try{
            serverLog = new BufferedWriter(new FileWriter("serverLog.txt",true));//the true will append the new data



        }catch(IOException e){
            System.err.println("IOException: " + e.getMessage());
        }
            accountsSaveFileInUse = false;//initial
            connectionsListInUse = false;
            serverLogInUse = false;
            gameServersListInUse = false;

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
                    killConnection(Integer.parseInt(input));
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

                case "allaccounts":
                    printAllAccounts();
                    break;

                case "printAllGameServers":
                    printAllGameServers();
                    break;
            }

        }
        try {
            serverLog.close();

        }catch(IOException e){e.printStackTrace();}



    }

    public static void log(String message){//implement flag waiting

        while(serverLogInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        serverLogInUse = true;


        try {
            serverLog.write(message);
            serverLog.newLine();
        }catch(IOException e){
            e.printStackTrace();
        }
        serverLogInUse = false;
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

        for(int i = 0; i < connections.size(); i++){
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

    }//called from clienthandler to create an account specified by a createAccount message dialogue


    public static int deleteAccount(String name, String passwordHash){

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
                    if(accountList.get(i).getPasswordHash().equals(passwordHash)){
                        accountList.remove(i);
                        foundIt = true;
                    }else{
                        return 2;//found account but wrong password
                    }

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
        if(foundIt){
            return 0;//successfully deleted
        }else{
            return 1;//account doesn't exist
        }

    }//called from clienthandler to delete an account specified by a deleteAccount message dialogue

    private static void killConnection(int id){

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
            if(connections.get(i).getId() == id){
                matched = true;
                connections.get(i).interrupt();
                break;
            }
        }
        if(matched){
            System.out.println("Server: Deleted thread "+ id);
        }else{
            System.out.println("Server: No thread with the given id existed");
        }
        connectionsListInUse = false;
    }//called by the main server terminal to kill a specific connection to the server

    private static void listConnections(){
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
    }//used to print all connections for the user at the server terminal

    private static void openConnectionListener(){
        if(currentCL == null || !currentCL.isAlive()){
            currentCL = new Thread(new ConnectionListener());
            currentCL.setDaemon(true);
            currentCL.start();
        } else{

            System.out.println("Server: Already open");

        }
    }//called by main server terminal to open connectionlistener

    private static void closeConnectionListener(){
        if(currentCL != null && currentCL.isAlive()){
            if(!currentCL.isInterrupted()){
                currentCL.interrupt();

                //connects to the socket to close the trailing connection
                Socket socket = null;

                try{
                    socket = new Socket("127.0.0.1", 1234);
                    PrintStream socketOut = new PrintStream(socket.getOutputStream());
                    socketOut.println("QUIT");
                }catch(IOException e){
                    e.printStackTrace();
                }
            }else{
                //System.out.println("Server: Already in the process of closing. Should close soon");
            }
        }else{
            // System.out.println("Server: Already closed");
        }
    }//called by main server terminal to close connection listener

    private static void quitServer(){
        sentinel = false;
        killAll();

        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        closeConnectionListener();
    }//called by main server terminal to close out the server

    public static int logInAccount(String name, String passHash){
        int returnVal = 3 ;//this val means no such name exists
        while(accountListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        accountListInUse = true;

        for(int i = 0; i < accountList.size(); i++){
            if(accountList.get(i).getName().equals(name)){
                returnVal = accountList.get(i).logIn(passHash);
            }
        }

        accountListInUse = false;

        return returnVal;
    }//called from clienthandler to change login status of an account

    public static int logOutAccount(String name, String passHash){
        int returnVal = 3 ;//fishy flag, received logOut for nonexistant account
        while(accountListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        accountListInUse = true;

        for(int i = 0; i < accountList.size(); i++){
            if(accountList.get(i).getName().equals(name)){
                returnVal = accountList.get(i).logOut(passHash);
            }
        }

        accountListInUse = false;

        return returnVal;
    }//called from clienthandler to change login status of an account

    private static void printAllAccounts(){
        while(accountListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        accountListInUse = true;

        System.out.println(accountList.size() + " accounts");

        for(int i = 0; i < accountList.size(); i++){
            System.out.println(accountList.get(i).getName() + " " + accountList.get(i).isLoggedIn());
        }


        accountListInUse = false;
    }//used to print all accounts for the user at the server terminal

    public static int addGameServer(String ip, String sname, String h, String passHash, long t){
        while(accountListInUse || gameServersListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        accountListInUse = true;
        gameServersListInUse = true;

        int returnVal = 3;//fishy flag, account trying to host does not exist

        for(int i = 0; i < accountList.size(); i++){
            if(accountList.get(i).getName().equals(h)){
                if(accountList.get(i).getPasswordHash().equals(passHash)){
                    if(accountList.get(i).isLoggedIn()){
                        gameServersList.add(new GameServerPA(ip,sname,h,t));
                        returnVal = 0;
                    }else{
                        returnVal = 1;//fishy flag, account was not logged in when trying to host
                    }
                }else{
                    returnVal = 2; //fishy flag, account trying to host gave wrong passHash
                }
            }
        }


        accountListInUse = false;
        gameServersListInUse = false;
        return returnVal;
    }//adds a gameServerPA to the list. Called from the client handler

    private static void printAllGameServers(){
        while(gameServersListInUse){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        gameServersListInUse = true;

        System.out.println(gameServersList.size() + " servers currently hosted:");
        for(int i = 0; i < gameServersList.size(); i++){
            System.out.println(gameServersList.get(i).getServerName() + " hosted by " + gameServersList.get(i).getHostAccountName());
        }

        gameServersListInUse = false;
    }//used to print all gameServers for the user at the server terminal




}
