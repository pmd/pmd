import java.util.ArrayList;
import java.util.List;

public class LocalVariableTypeInferenceForLoopEnhanced2 {
    public void listMethod() {
       List<String> test = new ArrayList<>();

       for (var s : test) {
            System.out.println(s);
        }
    }

    public void arrayMethod() {
        int values[] = {2, 4, 6};

        for (var s : values) {
             System.out.println(s);
         }
     }
}
