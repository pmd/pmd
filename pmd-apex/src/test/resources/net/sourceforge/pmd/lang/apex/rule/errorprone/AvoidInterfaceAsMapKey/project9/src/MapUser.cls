public class MapUser {
    // Should trigger violation - IBaseKey has a deep abstract implementor with equals
    // Hierarchy: IBaseKey <- ConcreteBase <- MidKey <- DeepAbstractKey (abstract with equals)
    private Map<IBaseKey, String> keyMap;
}

