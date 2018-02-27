/**
 * Main class.
 * Creates and activates all other threads.
 * 
 * ***** Explanations *****
 * Time:
 * Every 100 millisecond of runtime is 1 minute of story time.
 * Story starts are 0 time (consider it 12:00 noon), 
 * and the first exam is scheduled to be at 1:00.
 * So the first entry to classroom is at 0:45 which is 4500 runtime.
 * Similarly, the scheduled exam time and entry time for consequent exams 
 * would be +2:00 for each exam passed.
 * Then the exams are scheduled for 1:00, 3:00, 5:00 and 7:00 (7*60*100 = 72000).
 * 
 * Enter Sequence
 * It seems like the notifyAll() method notifies the waiting treads on a stack.
 * As a result, the first 4 waiting students will never get to take an exam 
 * until all others are done.
 * Since it is in the requirements to use notifyAll to signal the students
 * (to come in the classroom) instead of a queue, I implemented a random variable.
 * This will ensure that students will "randomly" enter the classroom,
 * even though the sequence of notify is pre-determined.
 * With this policy in place, it becomes very unlikely 
 * that a student will be stuck with only 1 exam.
 * However, it is still quite common that some students only takes 2 exams,
 * while others are done with all 3 on the 3rd given exam.
 * Therefore, to prevent a deadlock, the 4th (final) exam will start 
 * regardless of the number of students in the classroom.
 * 
 * @author Jinqiu Liu
 */
public class main {
    /**
     * System time when program started.
     */
    public static long startTime = System.currentTimeMillis();
    
    static int capacity = 12;
    static int numStudents = 16;
    static int numSeats = 3;
    
    public static void main(String[] args) {
        if (args.length == 3) { //possible valid arguments
            msg("Custom arguments found!");
            numStudents = Integer.parseInt(args[0]);
            capacity    = Integer.parseInt(args[1]);
            numSeats    = Integer.parseInt(args[2]);
        } //end of parsing arguments
        
        Classroom classroom = new Classroom(capacity, numSeats, startTime);
        Professor p = new Professor(classroom, startTime);
        p.start();
        for (int i = 0; i < numStudents; i++) {
            Student s = new Student(classroom, startTime, i);
            s.start();
        }
        msg("All Threads Created");
        
    } //end of main method
    
    public static void msg(String message) {
        System.out.println("["+(System.currentTimeMillis()-startTime)+"]\tMAIN:\t\t" + message);
    }
}
