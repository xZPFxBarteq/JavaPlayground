package prv.cynek.flow;

import org.junit.jupiter.api.Test;
import prv.cynek.flow.posts.Post;
import prv.cynek.flow.posts.TextPost;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class NewsCollectorTest {

    private Set<Post> dailyMailPosts = Set.of(dailyMailPost("new covid strain!"),
                                              dailyMailPost("drugs are now free!"));

    private Set<Post> bbcPosts = Set.of(bbcPost("queen died!"),
                                        bbcPost("Poland got invaded!"),
                                        bbcPost("Daily Mail sucks!"));

    @Test
    void shouldCollectAllPosts() {
        //given
        PostsPublisher postsPublisher = new PostsPublisher();
        NewsCollector newsCollector = new NewsCollector();

        //when
        postsPublisher.subscribe(newsCollector);
        executePostPublishing(postsPublisher);

        //then
        await().atMost(3, SECONDS)
               .until(() -> collectorHasBothDailyMailAndBBCPosts(newsCollector));
    }

    private void executePostPublishing(PostsPublisher postsPublisher) {
        ScheduledExecutorService executor = newScheduledThreadPool(2);

        executor.schedule(() -> offerNewsPosts(postsPublisher,
                                               dailyMailPosts),
                          1, SECONDS);
        executor.schedule(() -> offerNewsPosts(postsPublisher,
                                               bbcPosts),
                          2, SECONDS);

        executor.shutdown();
    }

    private void offerNewsPosts(PostsPublisher postsPublisher,
                                Set<Post> posts) {
        posts.forEach(post -> postsPublisher.offer(post,
                                                   (subscriber, nonProcessedPost) -> {
                                                       subscriber.onError(null);
                                                       //one reattempt
                                                       return true;
                                                   }));
    }

    private boolean collectorHasBothDailyMailAndBBCPosts(NewsCollector collector) {
        return collector.getCollectedNews().containsAll(dailyMailPosts) &&
                collector.getCollectedNews().containsAll(bbcPosts);
    }

    private Post dailyMailPost(String title) {
        return new TextPost("Daily mail: " + title,
                            "Something happened!");
    }

    private Post bbcPost(String title) {
        return new TextPost("BBC: " + title,
                            "Something happened!");
    }

}