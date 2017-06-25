package logicalguess.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import logicalguess.domain.command.ItemAdd;
import logicalguess.persistence.command.ItemsCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by logicalguess on 6/24/17.
 */
public class CommandRun {
    public static void main(String[] args) {
        final Logger log = LoggerFactory.getLogger(CommandRun.class);
        log.info("Start actor system");
        final ActorSystem actorSystem = ActorSystem.create("items");

        ActorRef items = actorSystem.actorOf(ItemsCommandProcessor.props("1"), "items");

        items.tell(ItemAdd.create("101", "first"), null);

    }
}
