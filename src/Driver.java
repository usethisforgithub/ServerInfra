import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

    static ArrayList<Thread> connections = new ArrayList<Thread>();



    public static void main(String[] args){
        System.out.println("Welcome to the server.");

        boolean sentinel = true;

        Scanner keyboard = new Scanner(System.in);


        Thread currentCL = null;


        while(sentinel){
            System.out.print(">");
            String input = keyboard.nextLine();
            switch(input){
                case "quit":
                    sentinel = false;
                    break;



                case "open":


                    if(currentCL == null || !currentCL.isAlive()){
                        currentCL = new Thread(new ConnectionListener());
                        currentCL.start();
                    } else{

                            System.out.println("Already open");

                    }
                    break;


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
                            System.out.println("Already in the process of closing. Should close soon");
                        }
                    }else{
                        System.out.println("Already closed");
                    }
                    break;
            }

        }





    }

}
