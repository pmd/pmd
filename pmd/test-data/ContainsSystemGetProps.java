public class ContainsSystemGetProps {
    public ContainsSystemGetProps() {
        System.getProperty("Rule violation");
        System.getProperties();
        System.setProperty("Another rule violation", "foo");
    }
}
