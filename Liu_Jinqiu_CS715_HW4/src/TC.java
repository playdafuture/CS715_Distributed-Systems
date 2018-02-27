
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

public class TC extends Thread {
    InputStream isA;
    InputStream isB;
    OutputStream toA;
    ObjectOutputStream oos;
    
    public TC(InputStream a, InputStream b, OutputStream os) {
        isA = a;
        isB = b;
        toA = os;
    }
    
    public void run() {
        try {
            System.out.println("TC starting execution");
            oos = new ObjectOutputStream(toA);
            int number = -1;            
            while (number != 0) {
                // get primitive data from TB
                number = isB.read();
                System.out.println("TC received from TB: " + number);
                
                // get primitive data from TA
                number = isA.read();
                System.out.println("TC received from TA: " + number);
                
                // send object to TA
                Message m = new Message(number, 3);
                oos.writeObject(m);
            }     
            System.out.println("TC finished execution");
        } catch (Exception e) {
            System.out.println("TC terminated. Reason: " + e.getMessage());
        }
    }
}
