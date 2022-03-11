import java.security.AccessController;
import java.security.PrivilegedAction;

public class MethodReferenceConfused {

    public void wrongVariableAccessor() {
        Object method = null;
        Object someObject = null;
        String result = AccessController.doPrivileged(
            (PrivilegedAction<String>) ((I) someObject)::method);
    }

    interface I {

        String method();
    }
}
