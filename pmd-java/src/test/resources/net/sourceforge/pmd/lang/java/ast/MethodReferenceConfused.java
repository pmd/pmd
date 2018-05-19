public class MethodReferenceConfused {
    public void wrongVariableAccessor() {
        Object someVarNameSameAsMethodReference = null;
        Object someObject = null;
        String result = AccessController.doPrivileged((PrivilegedAction<String>)
                ((SomeCast) someObject)::someVarNameSameAsMethodReference);
    }
}
