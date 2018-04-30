import java.util.ArrayList;

public class LocalVariableTypeInference {
    
    public void aMethod() {
        var list = new ArrayList<String>();  // infers ArrayList<String>
        var stream = list.stream();          // infers Stream<String>
    }
}