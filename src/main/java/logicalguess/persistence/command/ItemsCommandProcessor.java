package logicalguess.persistence.command;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import akka.persistence.journal.Tagged;
import logicalguess.domain.Item;
import logicalguess.domain.Items;
import logicalguess.domain.command.ItemAdd;
import logicalguess.domain.event.ItemAdded;
import scala.Option;

/**
 * Created by logicalguess on 6/24/17.
 */
public class ItemsCommandProcessor extends CommandProcessor {

    private boolean persisted = false;
    private boolean pendingChanges = false;

    private Items items;

    public static Props props(String id) {
        return Props.create(ItemsCommandProcessor.class, id);
    }

    ItemsCommandProcessor(String id) {
        items = Items.create(id);
    }


    @Override
    public String persistenceId() {
        return items.id;
    }

    @Override
    public Receive createReceiveRecover() {
        return ReceiveBuilder.create()
                .match(SnapshotOffer.class, this::recoverSnapshot)
                .match(RecoveryCompleted.class, this::recoveryCompleted)
                .match(ItemAdded.class, this::recoverItemAdded)
                .build();
    }

    @Override
    public Receive createReceive() {

        return ReceiveBuilder.create()
                .match(IdleTimeout.class, this::receiveTimeout)
                .match(SnapshotTick.class, this::snapshotPendingChanges)
                .match(SaveSnapshotSuccess.class, this::snapshotSuccess)
                .match(ItemAdd.class, this::itemAdd)
                .build();
    }


    @Override
    public void onPersistRejected(Throwable cause, Object event, long seqNr) {
        super.onPersistRejected(cause, event, seqNr);
    }

    @Override
    public void onRecoveryFailure(Throwable cause, Option<Object> event) {
        super.onRecoveryFailure(cause, event);
    }

    private void recoverItemAdded(ItemAdded e) {
        log.info("Recover {}", e);
        items.remove(e.id);
        persisted = true;
    }

    private void snapshotPendingChanges(SnapshotTick snapshotTick) {
        if (pendingChanges) {
            saveSnapshot(items);
            pendingChanges = false;
            log.info("Snapshot {} {}", items, snapshotTick);
        }
    }

    private void snapshotSuccess(SaveSnapshotSuccess saveSnapshotSuccess) {
        log.info("Snapshot success {}", saveSnapshotSuccess.metadata());
    }

    private void recoverSnapshot(SnapshotOffer snapshotOffer) {
        log.info("Recover {} {}", snapshotOffer, snapshotOffer.snapshot());
        items = (Items) snapshotOffer.snapshot();
        persisted = true;
    }


    private void recoveryCompleted(RecoveryCompleted recoveryCompleted) {
        log.info("RecoveryCompleted {}", recoveryCompleted);
    }

    private void receiveTimeout(IdleTimeout idleTimeout) {
        log.info("Idle timeout {}, {} timeout", items, idleTimeout);
        context().stop(self());
    }

    private void itemAdd(ItemAdd c) {
        log.info("Command {}", c);
        persist(asTagged(ItemAdded.create(c.id, c.value), "item"), this::itemAddedEventPersisted);

    }

    private void itemAddedEventPersisted(Tagged tagged) {
        ItemAdded e = (ItemAdded) tagged.payload();
        items = items.add(Item.create(e.id, e.value));
        getSender().tell(e, self());
        resetIdleTimeout();
        persisted = pendingChanges = true;
        log.info("State change {} add {}", items, e.value);
    }

}
