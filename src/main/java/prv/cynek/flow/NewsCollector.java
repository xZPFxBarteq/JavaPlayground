package prv.cynek.flow;

import lombok.Getter;
import lombok.extern.java.Log;
import prv.cynek.flow.posts.Post;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.logging.Level;

@Log
public class NewsCollector implements Subscriber<Post> {

    @Getter
    private final Set<Post> collectedNews = new HashSet<>();
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Post post) {
        collectedNews.add(post);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.log(Level.SEVERE, "Scrolling has failed.");
    }

    @Override
    public void onComplete() {
        log.info("Done scrolling, no more news!");
    }
}
