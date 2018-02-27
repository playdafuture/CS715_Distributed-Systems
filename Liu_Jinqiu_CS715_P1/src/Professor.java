/**
 * Professor Class.
 * There is only one professor.
 * @author Jinqiu Liu
 */
public class Professor extends Thread {
    Classroom cr;
    /**
     * System time when program started.
     */
    long startTime;
    int examsAdministrated;
    
    public Professor(Classroom cr, long startTime) {
        this.cr = cr;
        this.startTime = startTime;
        examsAdministrated = 0;
        setName("Professor");
    }
    
    @Override
    public void run() {
        sleep(500, 1000);
        msg("Awake");
        while (examsAdministrated < 4) {
            msg("Upcomming: Exam " + examsAdministrated);
            cr.letInStudents(examsAdministrated);
            msg("Let in the students");
            
            cr.giveExam(examsAdministrated);
            
            msg("Grading exams");
            cr.gradeExams();
            msg("Finished grading exams");
            
            examsAdministrated++;
        }
        msg("My day is finally over.");
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
