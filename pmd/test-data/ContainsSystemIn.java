import java.io.*;

public class ContainsSystemIn {
    public ContainsSystemIn() {
        try {
            System.in.read();
            System.err.println();
        } catch (IOException ioe) {}
    }
}
