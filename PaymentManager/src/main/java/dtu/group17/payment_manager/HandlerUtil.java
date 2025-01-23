package dtu.group17.payment_manager;

import dtu.group17.messaging_utilities.Event;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerUtil {

    public static <T, E extends Exception> void onErrorHandler(Map<UUID, CompletableFuture<T>> requests,
                                                               Function<String, E> mkException, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        String message = e.getArgument("message", String.class);
        requests.remove(eventId).completeExceptionally(mkException.apply(message));
    }

}
