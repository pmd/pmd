import java.util.Arrays;
import java.util.List;

public class LocalVariableTypeInferenceForLoopEnhanced {
    private List<String> data = Arrays.asList("a", "b", "c");

    public void aMethod() {
        for (var s : data) {
            System.out.println(s);
        }
    }
}