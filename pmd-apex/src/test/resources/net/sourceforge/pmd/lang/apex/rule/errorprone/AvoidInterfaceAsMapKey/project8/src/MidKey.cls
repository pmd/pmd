// Abstract class that EXTENDS concrete (indirectly implements IKey)
// and defines equals - this should still trigger the bug
public abstract class MidKey extends BaseKey {
    public MidKey(String key) {
        super(key);
    }
    public Boolean equals(Object other) {
        return this === other;
    }
    public Integer hashCode() {
        return System.identityHashCode(this);
    }
}

