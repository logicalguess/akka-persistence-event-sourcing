package logicalguess.persistence.query;

import akka.NotUsed;
import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.cassandra.query.javadsl.CassandraReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.PersistenceQuery;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import logicalguess.domain.Item;
import logicalguess.domain.Items;
import logicalguess.domain.event.ItemAdded;
import logicalguess.domain.query.ItemsGet;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by logicalguess on 6/24/17.
 */
public class ItemsQueryProcessor extends AbstractLoggingActor {

    {
        context().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));

        final ActorMaterializer materializer = ActorMaterializer.create(context().system());
        final CassandraReadJournal readJournal = cassandraReadJournal(context().system());

        Source<EventEnvelope, NotUsed> events = readJournal
                //.eventsByTag("item", Offset.noOffset()); // TODO need to start from a known offset
        .eventsByPersistenceId("1", Integer.MIN_VALUE, Integer.MAX_VALUE);
        events.map(eventEnvelope -> processEvent(eventEnvelope))
                .runWith(Sink.ignore(), materializer);
    }

    private Items items;

    ItemsQueryProcessor(String id) {
        items = Items.create(id);
    }

    public static Props props(String id) {
        return Props.create(ItemsQueryProcessor.class, id);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(ItemsGet.class, q -> sender().tell(items, self()))
                .build();
    }

    private Items processEvent(EventEnvelope eventEnvelope) {
        System.out.println("EVENT: " + eventEnvelope.event());
        ItemAdded e = (ItemAdded) eventEnvelope.event();
        items.add(Item.create(e.id, e.value)); //TODO play back with same logic in command
        return items;
    }

    private CassandraReadJournal cassandraReadJournal(ActorSystem actorSystem) {
        return PersistenceQuery.get(actorSystem)
                .getReadJournalFor(CassandraReadJournal.class, CassandraReadJournal.Identifier());
    }

    private void notProcessed(EventEnvelope eventEnvelope) {
        log().info("Rejected {}", eventEnvelope);
        sender().tell(String.format("Not processed %s", eventEnvelope), self());
    }


    private void receiveTimeout(ReceiveTimeout receiveTimeout) {
        log().info("Idle timeout {}, {} timeout", items.id, receiveTimeout);
        context().stop(self());
    }

    @Override
    public void preStart() throws Exception {
        log().info("Start {}", items.id);
    }

    @Override
    public void postStop() throws Exception {
        context().setReceiveTimeout(Duration.Undefined());
        log().info("Stop {}", items.id);
    }
}
