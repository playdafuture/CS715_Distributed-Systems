
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;


public class TA extends Thread {    
    ObjectInputStream oisB;
    ObjectInputStream oisC;
    InputStream fromB;
    InputStream fromC;
    OutputStream osC;
    
    public TA(InputStream isB, InputStream isC, OutputStream os) {
        fromB = isB;
        fromC = isC;
        osC = os;
    }    
    
    public void run() {
        try {
            System.out.println("TA starting execution");
            oisB = new ObjectInputStream(fromB);            
            oisC = new ObjectInputStream(fromC);
            int number = -1;
            while (number != 0) {
                // get object from TB
                Message m = (Message) oisB.readObject();
                System.out.println("TA received from TB: " + m);
                number = m.number;
                
                // send primitive data to TC
                System.out.println("TA sends to TC: " + number);
                osC.write(number);
                
                // get object from TC
                m = (Message) oisC.readObject();
                number = m.number;
                System.out.println("TA received from TC: " + m);
            }
            System.out.println("TA finished execution");
        } catch (Exception e) {
            System.out.println("TA terminated. Reason: " + e.getMessage());
        }
    }
}
