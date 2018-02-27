
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 *
 * @author Jinqiu Liu
 */
public class HW4Demo {
    static private PipedInputStream  pisBA;
    static private PipedOutputStream posBA;
    
    static private PipedInputStream  pisBC;
    static private PipedOutputStream posBC;
    
    static private PipedInputStream  pisAC;
    static private PipedOutputStream posAC;
    
    static private PipedInputStream  pisCA;
    static private PipedOutputStream posCA;
            
    public static void main(String[] args) {
        try {
            System.out.println("Pipe setup");  
            posBA = new PipedOutputStream();
            pisBA = new PipedInputStream(posBA);
            posBC = new PipedOutputStream();
            pisBC = new PipedInputStream(posBC);
            posAC = new PipedOutputStream();
            pisAC = new PipedInputStream(posAC);
            posCA = new PipedOutputStream();
            pisCA = new PipedInputStream(posCA);
            
            System.out.println("Object creation");
            TA ta = new TA(pisBA, pisCA, posAC);
            TB tb = new TB(posBA, posBC);
            TC tc = new TC(pisBC, pisAC, posCA);
            
            System.out.println("Thread execution");
            ta.start();
            tb.start();                        
            tc.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
