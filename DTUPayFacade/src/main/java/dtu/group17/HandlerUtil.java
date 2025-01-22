package dtu.group17;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HandlerUtil {

    public static <T, E extends Exception> void errorHandler(Map<UUID, CompletableFuture<T>> requests,
                                                             Function<String, E> mkException, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        String message = e.getArgument("message", String.class);
        requests.remove(eventId).completeExceptionally(mkException.apply(message));
    }

    public static <T> void completedHandler(Map<UUID, CompletableFuture<T>> requests, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        requests.remove(eventId).complete(null);
    }

}
