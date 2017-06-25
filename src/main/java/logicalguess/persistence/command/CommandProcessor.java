package logicalguess.persistence.command;

import akka.actor.Cancellable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.journal.Tagged;
import scala.Option;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by logicalguess on 6/24/17.
 */
public abstract class CommandProcessor extends AbstractPersistentActor {

    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private Cancellable idleTimeout;
    private Cancellable snapshotScheduler;

    {
        resetIdleTimeout();
        scheduleSnapshot();
    }

    @Override
    public void onPersistFailure(Throwable cause, Object event, long seqNr) {
        super.onPersistFailure(cause, event, seqNr);
    }

    @Override
    public void onPersistRejected(Throwable cause, Object event, long seqNr) {
        super.onPersistRejected(cause, event, seqNr);
    }

    @Override
    public void onRecoveryFailure(Throwable cause, Option<Object> event) {
        super.onRecoveryFailure(cause, event);
    }


    protected void resetIdleTimeout() {
        // This is not working - see https://github.com/akka/akka/issues/20738
        // context().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS)); // TODO make this configurable
        FiniteDuration timeout = Duration.create(10, TimeUnit.SECONDS); // TODO make this configurable

        if (idleTimeout != null) {
            idleTimeout.cancel();
        }
        idleTimeout = context().system().scheduler().scheduleOnce(
                timeout,
                self(),
                new IdleTimeout(timeout),
                context().system().dispatcher(),
                self());
    }

    private void scheduleSnapshot() {
        FiniteDuration interval = Duration.create(5, TimeUnit.SECONDS); // TODO make this configurable

        snapshotScheduler = context().system().scheduler().schedule(
                interval,
                interval,
                self(),
                new SnapshotTick(),
                context().system().dispatcher(),
                self());
    }


    @Override
    public void preStart() throws Exception {
        log.info("Start {}", this);
    }

    @Override
    public void postStop() {
        log.info("Stop {}", this);

        if (idleTimeout != null) {
            idleTimeout.cancel();
        }
        if (snapshotScheduler != null) {
            snapshotScheduler.cancel();
        }
    }

    protected Tagged asTagged(Object event, String... tags) {
        return new Tagged(event, new HashSet<>(Arrays.asList(tags)));
    }

    protected static class IdleTimeout {
        private final Duration timeout;

        private IdleTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        @Override
        public String toString() {
            return timeout.toString();
        }
    }

    protected static class SnapshotTick {
    }

}
