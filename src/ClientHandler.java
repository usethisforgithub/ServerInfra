import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket s){
        socket = s;
    }


    public void run() {
        System.out.println(Thread.currentThread().getName() + ": Started ClientHandler");

        while(!Thread.currentThread().isInterrupted()){

         }




        try {
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        Driver.connections.remove(Thread.currentThread());
        System.out.println(Thread.currentThread().getName() + ": Ended ClientHandler");
    }

}