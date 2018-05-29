public class LocalVariableTypeInferenceForLoop {

    public void aMethod() {
        for (var i = 1; i < 10; i++) {
            System.out.println(i);
        }
    }
}