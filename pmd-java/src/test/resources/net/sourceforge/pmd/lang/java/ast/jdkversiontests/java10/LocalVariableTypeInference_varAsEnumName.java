public class LocalVariableTypeInference_varAsEnumName {
    public enum var { A }
    public static void main(String... args) {
        var var = 1;

        System.out.println("var = " + var);
    }
}
