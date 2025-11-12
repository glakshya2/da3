package com.library.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager implements Serializable {

    private static final List<Consumer<BookIssuedEvent>> issueListeners = new ArrayList<>();
    private static final List<Consumer<BookReturnedEvent>> returnListeners = new ArrayList<>();
    private static final List<Consumer<BookAddedEvent>> addedBookListeners = new ArrayList<>();

    public static void registerIssueListener(Consumer<BookIssuedEvent> listener) {
        issueListeners.add(listener);
    }

    public static void registerReturnListener(Consumer<BookReturnedEvent> listener) {
        returnListeners.add(listener);
    }

    public static void registerBookAddedListener(Consumer<BookAddedEvent> listener) {
        addedBookListeners.add(listener);
    }

    public static void fireEvent(BookIssuedEvent event) {
        for (Consumer<BookIssuedEvent> listener : issueListeners) {
            listener.accept(event);
        }
    }

    public static void fireEvent(BookReturnedEvent event) {
        for (Consumer<BookReturnedEvent> listener : returnListeners) {
            listener.accept(event);
        }
    }

    public static void fireEvent(BookAddedEvent event) {
        for (Consumer<BookAddedEvent> listener : addedBookListeners) {
            listener.accept(event);
        }
    }
}
