package org.example.datamart;

public interface DatamartUpdater {
    void processEvent(String topic, String jsonEvent);
}
