public class GitHubBug208 {
	public void testMethod() {
	    @Lazy
	    @Configuration
	    class LocalClass {
            @Bean Object foo() {
                return null;
            }
        }
    }
}