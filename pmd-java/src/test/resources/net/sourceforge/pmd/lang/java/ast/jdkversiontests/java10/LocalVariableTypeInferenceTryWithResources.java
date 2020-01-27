import java.io.FileInputStream;

public class LocalVariableTypeInferenceTryWithResources {

    public void aMethod() throws Exception {
        String filename = "file.txt";
        try (var in = new FileInputStream(filename)) {

        }
    }
}
