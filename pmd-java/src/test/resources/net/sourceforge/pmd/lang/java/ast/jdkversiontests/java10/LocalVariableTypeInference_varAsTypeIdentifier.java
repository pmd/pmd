public class LocalVariableTypeInference_varAsTypeIdentifier {
    public static class var { }
    public static void main(String... args) {
        var var = 1;

        System.out.println("var = " + var);
    }
}
