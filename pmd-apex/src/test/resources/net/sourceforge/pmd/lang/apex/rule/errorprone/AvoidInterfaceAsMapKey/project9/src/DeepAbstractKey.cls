// Abstract class deep in hierarchy: IBaseKey <- ConcreteBase <- MidKey <- DeepAbstractKey
// The abstract class is 3 levels removed from the interface but should still trigger
public abstract class DeepAbstractKey extends MidKey {
    public Boolean equals(Object other) {
        return this === other;
    }
    public Integer hashCode() {
        return System.identityHashCode(this);
    }
}

