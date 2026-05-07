public class MapUser {
    // Should trigger violation - IKey has indirect abstract implementor with equals
    private Map<IKey, String> keyMap;
}

