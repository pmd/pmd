public class Unused13 {

	public void foo() {
		final String x = "hi";
		new Runnable() {
			public void run() {
				x.toString();
			}
		};
	}
}
