public class NullAssignment2 {
	public void foo() {
		Object x;
		x = new Object();
		for (int y = 0; y < 10; y++) {
			System.err.println(y);	
		}
		x = null; // This is bad
	}
}