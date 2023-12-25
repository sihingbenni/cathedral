package de.fhkiel.eki.boffin.work;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface Work {

    // Queue um die Arbeit zu übergeben
    // muss threadsafe sein, da mehrere Threads gleichzeitig schreiben und lesen
    BlockingQueue<Work> workToDo = new LinkedBlockingQueue<>();

    // die Arbeit verrichten
    void work();
}