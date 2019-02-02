import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Scanner connectionIn;
    private PrintStream connectionOut;




    public ClientHandler(Socket s){
        connectionIn = null;
        try {
            socket = s;
            connectionIn = new Scanner(socket.getInputStream());
            connectionOut = new PrintStream(socket.getOutputStream());

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public void run() {
        //System.out.println(Thread.currentThread().getName() + ": Started ClientHandler");

       // Driver.createAccount(Thread.currentThread().getName(), (Thread.currentThread().getName()));
        while(!Thread.currentThread().isInterrupted()){

                handleMessage();

         }

        //Driver.deleteAccount(Thread.currentThread().getName(),Thread.currentThread().getName());


        try {
            socket.close();
            connectionIn.close();
            connectionOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        Driver.removeConnection(Thread.currentThread());
        //System.out.println(Thread.currentThread().getName() + " ID: " + Thread.currentThread().getId() + " ; Ended ClientHandler");


    }

    private void handleMessage(){
        String message = connectionIn.nextLine();

        switch(message){
            case "QUIT":
                handleQUIT();
                break;

            case "CREATEACCOUNT":
                handleCREATEACCOUNT();
                break;

            case "DELETEACCOUNT":
                handleDELETEACCOUNT();
                break;

            case "LOGIN":
                handleLOGIN();
                break;


            case "LOGOUT":
                handleLOGOUT();
                break;

            case "TRYTOHOST":
                handleTRYTOHOST();
                break;
        }
    }

    private void handleQUIT(){
        if(!Thread.currentThread().isInterrupted()){
            Thread.currentThread().interrupt();
        }
    }

    private void handleCREATEACCOUNT(){
        connectionOut.println("OKAY");
        String accountName = connectionIn.nextLine();
        connectionOut.println("OKAY");
        String accountPassHash = connectionIn.nextLine();

        if(Driver.createAccount(accountName,accountPassHash)){
            connectionOut.println("SUCCESS");
        }else{
            connectionOut.println("FAILURE");
        }
    }

    //below method could be used to poll account names
    private void handleDELETEACCOUNT(){
        connectionOut.println("OKAY");
        String accountName = connectionIn.nextLine();
        connectionOut.println("OKAY");
        String accountPassHash = connectionIn.nextLine();

        connectionOut.println(Driver.deleteAccount(accountName,accountPassHash));



        /*
        switch(Driver.deleteAccount(accountName,accountPassHash)){
            case 0:
                connectionOut.println("0");
                break;

            case 1:
                connectionOut.println("1");
                break;

            case 2:
                connectionOut.println("2");
                break;
        }*/
    }

    private void handleLOGIN(){
        connectionOut.println("OKAY");
        String accountName = connectionIn.nextLine();
        connectionOut.println("OKAY");
        String accountPassHash = connectionIn.nextLine();

        connectionOut.println(Driver.logInAccount(accountName,accountPassHash));
    }

    private void handleLOGOUT(){
        connectionOut.println("OKAY");
        String accountName = connectionIn.nextLine();
        connectionOut.println("OKAY");
        String accountPassHash = connectionIn.nextLine();

        connectionOut.println(Driver.logOutAccount(accountName,accountPassHash));
    }

    private void handleTRYTOHOST(){
        connectionOut.println("OKAY");
        String serverName = connectionIn.nextLine();
        connectionOut.println("OKAY");
        String hostName = connectionIn.nextLine();
        connectionOut.println("OKAY");
        String pass = connectionIn.nextLine();

        if(Driver.addGameServer(socket.getInetAddress().toString().substring(1),serverName,hostName,pass,Thread.currentThread().getId()) == 0){
            talkToGameServer();
        }

    }

    private void talkToGameServer(){
        boolean mySentinel = true;
        while(!Thread.currentThread().isInterrupted() && mySentinel){

        }
    }

}
