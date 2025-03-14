/**
 * With java9, private methods are allowed in interface.
 */
public class Java9Interface {

    public interface Tool {
        void use();

        default String getName() {
            return determineName();
        }

        default String getStaticName() {
            return determineNameStatic();
        }

        private String determineName() {
            return "unknown:" + this.getClass().getSimpleName();
        }

        private static String determineNameStatic() {
            return "unknown:" + Tool.class.getSimpleName();
        }
    }

    public static class SampleTool implements Tool {

        public void use() {
            if (true) { // force a PMD violation: java-basic/UnconditionalIfStatement
                System.out.println("Instance: " + getName());
                System.out.println("  Static: " + getStaticName());
            }
        }
    }

    public static void main(String... args) {
        Tool tool = new SampleTool();
        tool.use();
    }
}
