import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionListener implements Runnable{


    public ConnectionListener( ){

    }

    public void run() {



        ServerSocket serverSocket = null;
        Socket socket = null;


        try {
            serverSocket = new ServerSocket(1234);

        }catch(IOException e){
            e.printStackTrace();
        }

         while(!Thread.currentThread().isInterrupted()){

        try{
            socket = serverSocket.accept();

        }catch(IOException e){
            e.printStackTrace();
        }
        if(!Thread.currentThread().isInterrupted()) {
            ClientHandler chandler = new ClientHandler(socket);
            Thread thread = new Thread(chandler);
            thread.start();
            Driver.addConnection(thread);
        }

         }
        try {
            serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
