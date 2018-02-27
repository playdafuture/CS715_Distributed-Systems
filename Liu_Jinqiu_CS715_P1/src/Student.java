/**
 * Student Class.
 * There are many students.
 * @author Jinqiu Liu
 */
public class Student extends Thread {
    Classroom cr;
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
    
    public Student(Classroom cr, long startTime, int id) {
        this.cr = cr;
        this.startTime = startTime;
        studentID = id;
        examsTaken = 0;
        setName("Student " + studentID);
    }
    
    @Override
    public void run() {
        sleep(1000, 3000);
        msg("Arrived School");
        while (examsTaken < 3 && cr.timePassed() < 42000) { 
            //more tests to take and school is open 
            //(See the full explanation for time in main.java class header)
            msg("Waiting to enter Classroom");
            cr.enterRoom(studentID);
            sleep(100,300); //walks into the room
            
            msg("Waiting for exam to start");
            cr.waitExam(studentID);
            
            msg("Exam over. Check notes");
            sleep(500,700);
            
            msg("Returning the exam for Professor to grade");
            scores[examsTaken] = cr.returnExam(studentID);
            
            msg("Leaving Classroom. " + (++examsTaken) + " exam(s) finished");
            cr.leaveRoom(studentID);
            
        }
        if (examsTaken == 3) {
            msg("All exams are finished. Going home");
        } else {
            msg("School Closed. Only taken " + examsTaken + " exams");
        }
        msg("My scores are " + scores[0] + ", " + scores[1] + ", " + scores[2]);        
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
        System.out.println("["+(System.currentTimeMillis()-startTime)+"]\t"+ getName()+ ":\t" + message);
    }
}
