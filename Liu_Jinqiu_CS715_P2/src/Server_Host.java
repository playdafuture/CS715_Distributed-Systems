
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The runnable class on server computer.
 * All clients will try to connect to here first.
 * @author Jinqiu Liu
 */
public class Server_Host {
    public static ServerSocket serverSocket;
    public static long startTime = System.currentTimeMillis() + 60000; // 1 minute extra time for connections
    public static final int teacherMax = 1;
    public static final int studentMax = 16;
    public static final int capacity = 12;
    public static final int numStudents = studentMax;
    public static final int numSeats = 3;
    public static int teacherCount = 0;
    public static int studentCount = 0;
    public static Server_Classroom cr;
    
    public static void main(String args[]) {
        setupServer();
        while (teacherCount < teacherMax || studentCount < studentMax) {
            acceptConnection();
        }
        System.out.println("All connections established. Server host closing.");
    }
    
    public static void setupServer() {
        cr = new Server_Classroom(capacity, numSeats, startTime);
        int port = ((int) ( 5000.0 * Math.random() )) + 5000;
        try {
            serverSocket = new ServerSocket(port, 20);
            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println("Please use the following Server info:"
                    + "\n\tIP Address = " + serverAddress
                    + "\n\tPort = " + port);
        } catch (IOException ex) {
            System.out.println("ERROR, unable to establish a server");
        }
    }
    
    public static void acceptConnection() {
        try {
            Socket incomingConnection = serverSocket.accept();
            int connectionType = incomingConnection.getInputStream().read();
            //read one int value to determine income thread type
            if (connectionType == 1) {
                // teacher connection
                System.out.println("Teacher connection established");
                if (teacherCount < teacherMax) {                    
                    new Server_Teacher(cr, startTime, incomingConnection).start();
                    teacherCount++;
                } else {
                    System.out.println("Too many teachers connected already!");
                    DataOutputStream dos = new DataOutputStream(incomingConnection.getOutputStream());
                    dos.writeInt(-1);
                }
            } else if (connectionType == 2) {
                // student connection
                System.out.println("Student connection established");
                if (studentCount < studentMax) {
                    new Server_Student(cr, startTime, studentCount, incomingConnection).start();
                    studentCount++;
                } else {
                    System.out.println("Too many students connected already!");
                    DataOutputStream dos = new DataOutputStream(incomingConnection.getOutputStream());
                    dos.writeInt(-1);
                }
            } else {
                // Anything else, most likely the test connection
                System.out.println("Test connection established");
            }
        } catch (IOException ex) {
            System.out.println("ERROR, unable to establish a connection to client");
        }
    }
}
