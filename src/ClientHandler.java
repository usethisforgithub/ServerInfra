import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket s){
        socket = s;
    }


    public void run() {
        //System.out.println(Thread.currentThread().getName() + ": Started ClientHandler");

        Driver.createAccount(Thread.currentThread().getName(), (Thread.currentThread().getName()));
        while(!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(1000);
            }catch(InterruptedException e){break;}
         }

        Driver.deleteAccount(Thread.currentThread().getName(),Thread.currentThread().getName());


        try {
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        Driver.removeConnection(Thread.currentThread());
        //System.out.println(Thread.currentThread().getName() + " ID: " + Thread.currentThread().getId() + " ; Ended ClientHandler");
    }

}
