public class LocalVariableTypeInference_varAsAnnotationName {
    public static @interface var { }
    public static void main(String... args) {
        var var = 1;

        System.out.println("var = " + var);
    }
}
