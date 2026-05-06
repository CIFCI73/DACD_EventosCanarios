package org.example.feeder;

import org.example.model.Event;
import java.util.List;

public interface EventFeeder {
    // Se conecta a la URL de la agenda, scrapea el HTML y devuelve la lista de eventos encontrados
    List<Event> getEvents();
}
