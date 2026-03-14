// Virtual concrete class that implements IBaseKey (virtual so it can be extended)
public virtual class ConcreteBase implements IBaseKey {
    public virtual String getKey() {
        return 'key';
    }
}

