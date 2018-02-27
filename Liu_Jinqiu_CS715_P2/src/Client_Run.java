
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * The runnable class on any client computer 
 * to create a thread that connects to the server host.
 * @author Jinqiu Liu
 */
public class Client_Run {
    public static String hostIP;
    public static int hostPort;
    public static Socket connection;
    public static Scanner consoleInput = new Scanner(System.in);
    
    public static void main(String args[]) {
        getHostInfo();
        testHostConnection();
        System.out.println("Host is good!");
        createThreads();
    }
    
    public static void getHostInfo() {
        System.out.println("Enter the host IP Address (e.g. 192.168.0.1)");
        hostIP = consoleInput.next();
        System.out.println("Enter the host port (e.g. 5678)");
        hostPort = consoleInput.nextInt();
    }
    
    public static void testHostConnection() {
        while (true) {
            try {
                connection = new Socket(hostIP, hostPort);
                connection.getOutputStream().write(0);
                break; // only way out is a successful connection
            } catch (IOException ex) {
                System.out.println("Test connection failed! "
                        + "(R)etry, (N)ew Connection, (A)bort?");
                String input;
                input = consoleInput.next();
                if (input.equalsIgnoreCase("N")) {
                    getHostInfo();
                } else if (input.equalsIgnoreCase("A")) {
                    System.exit(1);
                }
                // Default case is Retry
            }
        }
    }
    
    public static void createThreads() {
        while (true) {
            System.out.println("Which Thread to create? (S)tudent, (T)eacher, (A)bort or (C)ontinue?");
        
            String input = consoleInput.next();
            if (input.equalsIgnoreCase("S")) {
                System.out.println("How many instances?");
                int instances = consoleInput.nextInt();
                for (int i = 0; i < instances; i++) {
                    new Client_Student(hostIP, hostPort).start();
                }
            } else if (input.equalsIgnoreCase("T")) {
                new Client_Teacher(hostIP, hostPort).start();
            } else if (input.equalsIgnoreCase("A")) {
                System.exit(2);
            } else if (input.equalsIgnoreCase("C")) {
                break;
            } else {
                System.out.println("Invalid choice, try again.");
            }
        }        
    }
}
