public class ClassLiteralExample {
    public class TargetClass {}

    public void example() {
        consume(TargetClass.class);
        consume(ClassLiteralExample.TargetClass.class);
        consume(Type.class);
    }

    private void consume(Type referencedType) {}
}
