import java.io.*;
public class EmptyCatchBlock {
    public EmptyCatchBlock() {
	try {
		FileReader fr = new FileReader("/dev/null");
		// howdy
	} catch (Exception e) {
	}
	try {
		FileReader fr = new FileReader("/dev/null");
	} catch (Exception e) {
		e.printStackTrace();
		// this shouldn't show up on the report
	}
    }
}
