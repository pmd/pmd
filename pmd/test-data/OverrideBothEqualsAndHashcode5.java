public class OverrideBothEqualsAndHashcode5 {

	public class Foo {
		public boolean equals(Object o) {
			return true;
		}
	}

	public boolean equals(Object o) {
		return true;
	}
	public int hashCode() {
		return 0;
	}

}
