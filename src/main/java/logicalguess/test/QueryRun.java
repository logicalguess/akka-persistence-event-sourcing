package logicalguess.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import logicalguess.domain.command.ItemAdd;
import logicalguess.domain.query.ItemsGet;
import logicalguess.persistence.command.ItemsCommandProcessor;
import logicalguess.persistence.query.ItemsQueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by logicalguess on 6/24/17.
 */
public class QueryRun {
    public static void main(String[] args) {
        final Logger log = LoggerFactory.getLogger(QueryRun.class);
        log.info("Start actor system");
        final ActorSystem actorSystem = ActorSystem.create("items");

        ActorRef items = actorSystem.actorOf(ItemsQueryProcessor.props("1"), "items");

        items.tell(ItemsGet.create("1"), null);

    }
}
