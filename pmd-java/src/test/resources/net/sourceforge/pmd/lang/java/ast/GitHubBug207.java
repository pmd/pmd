public class GitHubBug207 {
    private static HttpMessageWriter<Resource> resourceHttpMessageWriter(BodyInserter.Context context) {
        return context.map(BodyInserters::<Resource>cast);
    }
}