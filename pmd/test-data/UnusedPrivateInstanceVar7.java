public class UnusedPrivateInstanceVar7 {
	private static final String FOO = "foo";
	public Runnable bar() {	
		return new Runnable() {
			public void run() {
				FOO.toString();
			}
		};
	}

}
