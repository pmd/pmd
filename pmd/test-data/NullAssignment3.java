public class NullAssignment3 {
	public void foo() {
		Object x;
		if (x == null) { // This is OK
			return;
		}
	}
}
