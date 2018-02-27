
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Professor Class.
 * There is only one professor.
 * @author Jinqiu Liu
 */
public class Server_Teacher extends Thread {
    Server_Classroom cr;
    /**
     * System time when program started.
     */
    long startTime;
    int examsAdministrated;
    
    Socket connection;
    DataInputStream input;
    DataOutputStream output;
    
    public Server_Teacher(Server_Classroom cr, long startTime, Socket connection) {
        this.cr = cr;
        this.startTime = startTime;
        examsAdministrated = 0;
        setName("Teacher");
        this.connection = connection;
        try {
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());
            output.writeInt(0);
        } catch (IOException ex) {
            Logger.getLogger(Server_Student.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(getName() + " created");
    }
    
    @Override
    public void run() {
        sleep(500, 1000);
        msg("Awake");
        waitProceed();
        while (examsAdministrated < 4) {
            msg("Upcomming: Exam " + examsAdministrated);
            waitProceed();
            cr.letInStudents(examsAdministrated);
            msg("Let in the students");
            waitProceed();
            
            cr.giveExam(examsAdministrated);
            
            msg("Grading exams");
            waitProceed();
            cr.gradeExams();
            msg("Finished grading exams");
            waitProceed();
            
            examsAdministrated++;
        }
        msg("My day is finally over.");
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
