public class ContainsSystemGetProps {
    public void foo() {
        System.getProperty("this.is.not.allowed");
        System.getProperties();
        System.setProperty("set.a.system.property", "value");
    }
}
