// this catches the case where the variable is used semantically before it's declared syntactically
public class UnusedPrivateInstanceVar4 {
public void bar() {
	foo[0] = 0;
}
private int[] foo;

}
