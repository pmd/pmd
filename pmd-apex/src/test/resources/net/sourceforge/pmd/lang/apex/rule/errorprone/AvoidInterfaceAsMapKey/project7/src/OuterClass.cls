public class OuterClass {
    // Inner interface
    public interface IKey {
        String getKey();
    }

    // Inner abstract class implementing the interface
    public abstract class AbstractKey implements IKey {
        public Boolean equals(Object other) {
            return this === other;
        }
        public Integer hashCode() {
            return System.identityHashCode(this);
        }
    }

    // Inner concrete class
    public class ConcreteKey extends AbstractKey {
        private String key;
        public ConcreteKey(String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }

    // Map usage with inner interface as key - should trigger violation
    private Map<IKey, String> keyMap;

    public void doSomething(Map<IKey, Integer> paramMap) {
        Map<IKey, Boolean> localMap = new Map<IKey, Boolean>();
    }
}

