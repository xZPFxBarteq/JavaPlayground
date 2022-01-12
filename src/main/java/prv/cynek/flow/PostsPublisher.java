package prv.cynek.flow;

import prv.cynek.flow.posts.Post;

import java.util.concurrent.SubmissionPublisher;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class PostsPublisher extends SubmissionPublisher<Post> {

    public PostsPublisher() {
        super(newSingleThreadExecutor(), 5);
    }
}
