
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A teacher thread on the client side.
 * @author Jinqiu Liu
 */
public class Client_Teacher extends Thread {
    int id;
    Socket connection;
    DataInputStream input;
    DataOutputStream output;
    
    public Client_Teacher(String hostIP, int hostPort) {
        try {
            connection = new Socket(hostIP, hostPort);
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());
            connection.getOutputStream().write(1); //tell the host "I'm a teacher"
            id = input.readInt();
            setName("Teacher");
            System.out.println(getName() + " created");
        } catch (IOException ex) {
            msg("Error occured during constructor");
        }
    }
    
    @Override
    public void run() {
        if (id != -1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client_Teacher.class.getName()).log(Level.SEVERE, null, ex);
            }
            String status = "";
            while (!status.equalsIgnoreCase("END")) {
                try {
                    status = input.readUTF();
                    msg(status);
                    output.writeUTF("Proceed"); //always proceed
                } catch (IOException ex) {
                    Logger.getLogger(Client_Teacher.class.getName()).log(Level.SEVERE, null, ex);
                }            
            }
            msg("All tasks completed. \n\t\tThis Thread Terminated");
        } else {
            msg("This Thread should not exist. Terminating");
        }
        
    }
    
    private void msg(String message) {
        System.out.println(getName()+ ":\t" + message);
    }
}
