
import java.io.Serializable;
import java.net.*;

public class Message implements Serializable {

    public int number;
    public int id;
    
    public Message(int a, int b) {
        number = a;
        id = b;
    }

    public String toString() {
        return ("Message [number = " + number + ", id = " + id + "]");
  }
}