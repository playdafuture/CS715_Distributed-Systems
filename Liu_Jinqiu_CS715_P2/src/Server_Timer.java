/**
 * Timer Class.
 * Give the timer an Object, and it will notify it when it's time.
 * Timer can do countdown (relative) or alarm (absolute).
 * @author Jinqiu Liu
 */
public class Server_Timer extends Thread {
    /**
     * System time when program started.
     */
    long startTime;
    /**
     * The notification object.
     */
    Object bell;
    /**
     * TRUE if this is a countdown timer (for some milli-seconds), 
     * FALSE if this is an alarm timer (ring when run-time reaches some milli-seconds).
     */
    boolean countDown;
    /**
     * The countdown or target value for the timer.
     */
    long value;
    /**
     * TURE will notifyAll on the bell, FALSE will only notify one.
     */
    boolean notifyAll;
    
    /**
     * Construct a timer and run it later.
     * When the time is up, thread waiting on the bell will be notified.
     * @param description A description to name the timer.
     * @param startTime The absolute starting time of the entire program. 
     * This should be consistent among all threads.
     * @param bell The object to be notified later.
     * @param countDown TRUE if this is a countdown timer (for some milli-seconds), 
     * FALSE if this is an alarm timer (ring when run-time reaches some milli-seconds).
     * @param value The countdown or target value for the timer.
     * @param notifyAll TURE will notifyAll on the bell, FALSE will only notify one.
     */
    public Server_Timer(String description, long startTime, Object bell, boolean countDown, long value, boolean notifyAll) {
        this.startTime = startTime;
        this.bell = bell;
        this.countDown = countDown;
        this.value = value;
        this.notifyAll = notifyAll;
        setName(description);
    }
    
    @Override
    /**
     * Run the sequence and notify the bell (passed in by constructor) when it's supposed to.
     */
    public void run() {
        if (countDown) {
            msg("Countdown Timer Started");
            try {
                Thread.sleep(value);
            } catch (InterruptedException ex) {
                msg("Timer interrupted");
            }
            
        } else {
            msg("Alarm Timer Started");
            try {
                Thread.sleep(value - (System.currentTimeMillis() - startTime)); //target - time passed
            } catch (InterruptedException ex) {
                msg("Timer interrupted");
            }
        }
        msg("Time is up, notifying");
        synchronized (bell) {
            if (notifyAll) {
                bell.notifyAll();
            } else {
                bell.notify();
            }            
        }
    }
    
    public void msg(String message) {
        System.out.println("["+(System.currentTimeMillis()-startTime)+"]\t"+ getName()+ ":\t" + message);
    }
}
