
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Student Class.
 * There are many students.
 * @author Jinqiu Liu
 */
public class Server_Student extends Thread {
    Server_Classroom cr;
    /**
     * System time when program started.
     */
    long startTime;
    /**
     * Assigned by main, unique.
     */
    int studentID;
    /**
     * Tracked by self.
     */
    int examsTaken;
    /**
     * Received from Professor, tracked by self.
     */
    int[] scores = new int[3];
    
    Socket connection;
    DataInputStream input;
    DataOutputStream output;
    
    public Server_Student(Server_Classroom cr, long startTime, int id, Socket connection) {
        this.cr = cr;
        this.startTime = startTime;
        studentID = id;
        examsTaken = 0;
        setName("Student " + studentID);
        this.connection = connection;        
        try {
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());
            output.writeInt(id);
        } catch (IOException ex) {
            Logger.getLogger(Server_Student.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(getName() + " created");
    }
    
    @Override
    public void run() {
        sleep(1000, 3000);
        msg("Arrived School");
        waitProceed();
        while (examsTaken < 3 && cr.timePassed() < 42000) { 
            //more tests to take and school is open 
            //(See the full explanation for time in main.java class header)
            msg("Waiting to enter Classroom");
            waitProceed();
            cr.enterRoom(studentID);
            sleep(100,300); //walks into the room
            
            msg("Waiting for exam to start");
            waitProceed();
            cr.waitExam(studentID);
            
            msg("Exam over. Check notes");
            waitProceed();
            sleep(500,700);
            
            msg("Returning the exam for Professor to grade");
            waitProceed();
            scores[examsTaken] = cr.returnExam(studentID);
            
            msg("Leaving Classroom. " + (++examsTaken) + " exam(s) finished");
            waitProceed();
            cr.leaveRoom(studentID);
            
        }
        if (examsTaken == 3) {
            msg("All exams are finished. Going home");
            waitProceed();
        } else {
            msg("School Closed. Only taken " + examsTaken + " exams");
            waitProceed();
        }
        msg("My scores are " + scores[0] + ", " + scores[1] + ", " + scores[2]);
        waitProceed();
        msg("END");
        waitProceed();
    }
    
    /**
     * Sleeps the thread for a random amount of time.
     * 100ms runtime = 1 minute story time.
     * See the full explanation for time in main.java class header.
     * @param min Minimum time to sleep (in ms).
     * @param max Maximum time to sleep (in ms).
     */
    public void sleep(int min, int max) {
        double random = Math.random() * (max - min);
        try {
            Thread.sleep((long) random + min);
        } catch (InterruptedException ex) {
            msg("Sleep interrupted");
            msg(ex.getMessage());
        }
    }
    
    public void msg(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException ex) {
            Logger.getLogger(Server_Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void waitProceed() {
        String cmd;
        try {
            cmd = input.readUTF();
            if (!cmd.equalsIgnoreCase("Proceed")) {
                System.out.println("Unexpected Command, exiting...");
                System.exit(5);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server_Student.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
