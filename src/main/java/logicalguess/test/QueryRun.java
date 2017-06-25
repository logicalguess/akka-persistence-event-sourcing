package logicalguess.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import logicalguess.domain.command.ItemAdd;
import logicalguess.domain.query.ItemsGet;
import logicalguess.persistence.command.ItemsCommandProcessor;
import logicalguess.persistence.query.ItemsQueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.pattern.PatternsCS.ask;

/**
 * Created by logicalguess on 6/24/17.
 */
public class QueryRun {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Logger log = LoggerFactory.getLogger(QueryRun.class);
        log.info("Start actor system");
        final ActorSystem actorSystem = ActorSystem.create("items");

        ActorRef items = actorSystem.actorOf(ItemsQueryProcessor.props("1"), "items");

        //items.tell(ItemsGet.create("1"), null);
        CompletionStage<Object> f = ask(items, ItemsGet.create("1"), 3000L);
        Thread.sleep(3000);
        System.out.println(f.toCompletableFuture().get());

    }
}
