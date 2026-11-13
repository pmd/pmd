public class ClassRefExpressionTypeInfo {
    public class TargetClass {}

    public void example() {
        consume(TargetClass.class);
        consume(ClassRefExpressionTypeInfo.TargetClass.class);
        consume(Type.class);
    }

    private void consume(Type referencedType) {}
}
