import java.util.Vector;

/**
 * Monitor Class. 
 * Synchronizes Students, Professor and Timer.
 * @author Jinqiu Liu
 */
public class Server_Classroom {
    /**
     * Table object is a part of the classroom.
     * Students will sit at the tables in the classroom during exams.
     */
    private static class Table {
        /**
         * Number of Tables in the classroom.
         */
        int numTables;
        /**
         * Number of Seats per Table.
         */
        int seatsPerTable;
        /**
         * Number of students that actually are seated.
         */
        int seatedStudents;
        /**
         * Table is simulated by 2-dimensional array, 
         * first number being the table number 
         * and second number being the seat number.
         * For example, table[1][2] represents 2nd table, 3rd seat.
         */
        int[][] table;
        /**
         * Lock object for each table. 
         * Professor will call notify on those objects.
         */
        Object[] tableLock;
        /**
         * Constructor.
         * @param tables Number of tables.
         * @param seats Seats per table.
         */
        public Table (int tables, int seats) {
            numTables = tables;
            seatsPerTable = seats;
            table = new int[tables][seats];
            for (int t = 0; t < numTables; t++) {
                for (int s = 0; s < numSeats; s++) {
                    table[t][s] = -1; //-1 is empty. 0 would mean student 0.
                }
            }
            tableLock = new Object[tables];
            for (int i = 0; i < tables; i++) {
                tableLock[i] = new Object();
            }
            seatedStudents = 0;
        }
        
        /**
         * Assign a seat for student.
         * Student will get the table object he/she is sitting on to wait,
         * or will get null if all tables are actually full.
         * @param id Student's ID.
         * @return Table object if successfully seated 
         *         or null if no available seats.
         */
        public synchronized Object sitStudent(int id) {
            if (seatedStudents >= roomCapacity) {
                return null;
            }
            table[seatedStudents/numSeats][seatedStudents%numSeats] = id;
            seatedStudents++;            
            return tableLock[seatedStudents/numTables];
        }
        
        /**
         * Student "stands up" after the exam. 
         * The student should be submitting the exam for the professor to grade.
         * Student will check how many more students are seated, 
         * if none is left, will notify professor to start grading.
         * @param id Student's ID.
         * @return Number of students still seated.
         */
        public synchronized int leave(int id) {
            for (int t = 0; t < numTables; t++) {
                for (int s = 0; s < numSeats; s++) {
                    if (table[t][s] == id) { //finds the seat location
                        table[t][s] = -1; //this seat is now empty
                        seatedStudents--;
                        return seatedStudents;
                    }
                }
            }
            return -1; //student does not exist in table
        }
        
        /**
         * DEBUG method: Forces the tables to clear.
         */
        public void reset() {
            seatedStudents = 0;
            for (int t = 0; t < numTables; t++) {
                for (int s = 0; s < numSeats; s++) {
                    table[t][s] = -1;
                }
            }
        }
        
        /**
         * Print out how students are seated.
         */
        public synchronized void print() {
            for (int t = 0; t < numTables; t++) {
                System.out.print("\t\t\t");
                for (int s = 0; s < numSeats; s++) {
                    System.out.print(table[t][s] + "\t");
                }
                System.out.println();
            }
        }
        
        /**
         * Professor's call when starting exams.
         */
        private void notifyAllTables() {
            for (int i = 0; i < numTables; i++) { //for each table
                synchronized (tableLock[i]) {
                    tableLock[i].notifyAll(); //release table lock (start exam)
                }
            }
        }        
    }
    
    /**
     * Exam object.
     * Student will "hand it in" after the test is over and WAIT on it.
     */
    private class Exam {
        /**
         * Score of the exam.
         * Only professor will change this (students won't cheat).
         */
        int score = 0;
    }
    // "Physical Attributes"
    /**
     * Max number of students allowed in the room.
     */
    static int roomCapacity;
    /**
     * Current occupancy.
     */
    static int numStudents;
    /**
     * Seats per table.
     */
    static int numSeats;
    /**
     * Derived attribute, total/seatsPerTable = numTable.
     */
    static int numTables;
    /**
     * Table object for organizing seats.
     */    
    static Table table;
    /**
     * Boolean (local) variable that indicates 
     * if students are allowed to come in the classroom or not.
     */
    static boolean isOpen;
    
    // Time consistency
    /**
     * System.currentTime when program started.
     */
    long startTime;
    
    // Queue
    /**
     * Queue for grading exam.
     */
    Vector gradingQueue = new Vector();
    
    // Locks
    /**
     * Lock for holding students coming in.
     */
    static Object doorLock = new Object();
    /**
     * Lock for everyone until the exam ends.
     */
    static Object examBell = new Object();
    /**
     * Lock for professor to wait until all exams are submitted.
     */
    static Object pleaseGrade = new Object();
    
    /**
     * Constructor.
     * @param capacity Max number of students.
     * @param ns Number of seats per table.
     * @param st System time where "main" started.
     */
    public Server_Classroom(int capacity, int ns, long st) {
        roomCapacity = capacity;
        numStudents = 0;
        numSeats = ns;
        numTables = capacity/numSeats;
        startTime = st;    
        table = new Table(numTables,numSeats);
        isOpen = false; //door is default closed
    }
    
    // Students' Service Methods ----------------------------------------------
    
    /**
     * Students must wait outside the room until the Professor opens the door 
     * (15 minutes before exam begins).
     * If the door is already open, the student can go in directly.
     */
    void enterRoom(int id) {
        while (true) {
            //msg("Student " + id + " trying to get in the room");
            double ran = Math.random() * 100;
            try {
                //Randomize Entry Sequence, see main.java header for full explanation
                Thread.sleep((long) ran);
            } catch (InterruptedException ex) {
                msg("!ERROR! Student interrupted while walking into the room");
            }
            if (isOpen && numStudents < roomCapacity) {
                // door is open, and there's more space -> walk in
                if (getSeat(id)) {
                    // and also got a seat
                    return;
                } else {
                    //goes back to if statement
                    continue;                    
                }
            } else {
                synchronized (doorLock) {
                    try {
                        //System.out.println(id + " waiting on doorlock");
                        doorLock.wait(); //waits to be notified
                        //System.out.println(id + " released on doorlock");
                    } catch (InterruptedException ex) {
                        //goes back to if statement
                        continue;                        
                    }
                }
            }
        }      
    }
    
    /**
     * After student walks in the room, find a seat.
     * @param id Student's ID.
     * @return TURE if a seat was assigned and FALSE otherwise.
     */
    boolean getSeat(int id) {
        numStudents++;
        Object myTable = table.sitStudent(id);
        if (myTable == null) {
            //all seats are full, get out of the room
            numStudents--;
            return false;
        }
        synchronized(myTable) {
            try {
                myTable.wait();
            } catch (InterruptedException ex) {
                msg("!ERROR! Interrupted when waiting on exam to start");
            }
        }
        return true;
    }
    
    /**
     * Wait for the exam to be over.
     * examBell is the object that both students and professor wait on.
     * @param id Student's ID
     */
    void waitExam(int id) {
        synchronized(examBell) {
            try {
                examBell.wait();
            } catch (InterruptedException ex) {
                msg("!ERROR! Sudent " + id + " interrupted while waiting for exam to end");
            }
        }        
    }
    
    /**
     * Submits the exam to the professor and then wait for it to be graded.
     * @param id Student's ID.
     */
    int returnExam(int id) {
        Exam myExam = new Exam();
        gradingQueue.add(myExam);
        int remainingStudents = table.leave(id);
        if (remainingStudents == 0) {
            synchronized (pleaseGrade) {
                pleaseGrade.notify(); //notify the professor there's something to grade
            }   
        }
        synchronized (myExam) {
            try {
                myExam.wait();
            } catch (InterruptedException ex) {
                msg("!ERROR! Sudent " + id + " interrupted while waiting for exam to be graded");
            }
        }
        return myExam.score;
    }
    
    /**
     * Student leaves the room.
     * Done with exam and received score.
     * @param id Student's ID.
     */
    synchronized void leaveRoom(int id) {
        numStudents--;
    }
    
    // Professor's Service Methods
    
    /**
     * Professor let the students in.
     * If time is not yet, create a timer and wait for it.
     * @param examNumber 0, 1, 2, or 3.
     */
    void letInStudents(int examNumber) {
        int time = 4500 + examNumber * 12000; 
        //see main.java header for full explanation on time
        Object bell = new Object();
        if (timePassed() < time) {
            Server_Timer t = new Server_Timer("Door timer", startTime, bell, false, time, false);
            t.start();
            synchronized (bell) {
                try {
                    bell.wait();
                } catch (InterruptedException ex) {
                    msg("!ERROR! Professor interrupted when waiting for door timer");
                }
            }
        }
        
        isOpen = true;
        synchronized(doorLock) {
            doorLock.notifyAll();
        }
    }
    /**
     * Professor gives the exam.
     * Generally there's two conditions to be met in order to start:
     * 1. Scheduled time is met. 2. Enough students seated.
     * If either condition is not met, professor will delay the exam 
     * and try to let more students in. (Except last exam, 
     * see main.java header for full explanation on entry sequence)
     * The test will always be 1 hour since it actually starts.
     * Everyone will wait on the same timer once the test starts.
     * @param examNumber 0, 1, 2, or 3.
     */
    void giveExam(int examNumber) {
        while (true) {
            if (timePassed() >= 6000 + 12000 * examNumber) {
                //Time is ready, check number of students
                //see main.java header for full explanation on time
                if (examNumber < 3 && table.seatedStudents >= roomCapacity - numSeats) {
                    break; //all tables except last one must be fully occupied
                } else if (examNumber == 3) {
                    //see main.java header for full explanation on entry sequence
                    break; //the last exam must start
                }
            }
            try {
                synchronized(doorLock) {
                    //time is not yet, or not enough students, try letting more in
                    doorLock.notifyAll(); 
                }
                Thread.sleep(300); 
                //Give the students 3 minutes to get seated (for those that just came in)
            } catch (InterruptedException ex) {
                msg("!ERROR! Professor interrupted while waiting for more students to come in");
            }
        }        
        
        isOpen = false;
        msg("Exam starts now. There are " + numStudents + " students in the room");
        msg("Classroom layout:");
        table.print();        
        
        table.notifyAllTables();
        Server_Timer t = new Server_Timer("Exam timer", startTime, examBell, true, 6000, true);
        t.start();
        synchronized(examBell) {
            try {
                examBell.wait();
            } catch (InterruptedException ex) {
                msg("!ERROR! Professor interrupted while waiting for exam to be over");
            }
        }
        msg("Exam " + examNumber + " is Over");
    }
    
    /**
     * Wait to be notified when all students submitted then grade all the exams.
     * The grades are assigned randomly, but minimum is 60 (to clearly distinguish a missed exam).
     */
    void gradeExams() {        
        synchronized (pleaseGrade) {
            try {                
                pleaseGrade.wait();
            } catch (InterruptedException ex) {
                msg("!ERROR! Professor interrupted while waiting to grade exams");
            }
        }
        //int examsGraded = 0;
        while (!gradingQueue.isEmpty()) {
            //System.out.print("Grading exam #");
            //System.out.println(++examsGraded);
            try {
                Thread.sleep(200); //simulate grading
            } catch (InterruptedException ex) {
                msg("!ERROR! Professor interrupted while grading exam");
            }
            synchronized (gradingQueue.elementAt(0)) {
                Exam e = (Exam) gradingQueue.elementAt(0);
                e.score = (int) (Math.random() * 40) + 60;
                gradingQueue.elementAt(0).notify();
            }            
            gradingQueue.remove(0);
        }               
    }

    // Monitor's Methods
    public int timePassed() {
        return (int) (System.currentTimeMillis() - startTime);
    }
    
    /**
     * DEBUG method. Forcefully reset the room.
     */
    public void clearRoom() {
        //numStudents = 0;
        //table.print();
        //msg("Clearing table");
        table.reset();
        //table.print();
    }
    
    public void msg(String message) {
        System.out.println("["+(System.currentTimeMillis()-startTime)+"]\tClassroom:\t" + message);
    }
}
