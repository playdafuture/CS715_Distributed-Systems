
import java.io.OutputStream;
import java.io.ObjectOutputStream;

public class TB extends Thread {
    ObjectOutputStream oos;
    OutputStream toA;
    OutputStream os;
    
    public TB(OutputStream osA, OutputStream osC) {
        toA = osA;
        os = osC;
    }
    
    public void run() {
        try {
            System.out.println("TB starting execution");
            oos = new ObjectOutputStream(toA);
            for (byte i = 3; i >= 0; i--) {
                //Thread.sleep(3000); 
                /**
                 * Un-comment Thread.sleep for controlled message sending.
                 * Since TB does not receive message from anyone, without this,
                 * TB will just send all messages at once.
                 * I included this to make the output sequence make more sense.
                 */
                Message m = new Message(i, 2);
                System.out.println("TB sends to TA: " + m);
                oos.writeObject(m);
                System.out.println("TB sends to TC: " + i);
                os.write(i);
            }
            System.out.println("TB finished execution");
        } catch (Exception e) {
            System.out.println("TB terminated. Reason: " + e.getMessage());
        }
    }
}
