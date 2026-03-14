// Virtual concrete class that directly implements interface (virtual so it can be extended)
public virtual class BaseKey implements IKey {
    private String key;
    public BaseKey(String key) {
        this.key = key;
    }
    public virtual String getKey() {
        return key;
    }
}

