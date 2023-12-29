package de.fhkiel.eki.helper;

import java.io.PrintStream;

public class ContinuousLogger implements Runnable {

    private static final int CONTINUOUS_LOGGING_DELAY = 5_000;

    private final PrintStream console;

    /**
     * To stop the Worker.
     * volatile so that all threads notice the change immediately
     */
    private volatile boolean doStop = false;

    public ContinuousLogger(PrintStream console) {
        this.console = console;
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return !this.doStop;
    }

    @Override
    public void run() {
        System.out.println("starting continuous logging.");
        do {

            try {
                //noinspection BusyWait
                Thread.sleep(CONTINUOUS_LOGGING_DELAY);
            } catch (InterruptedException e) {
                System.out.println("interrupted continuous logging.");
            }
            // check again after the sleep, maybe operation has finished
            if (keepRunning()) {
                console.print("...");
            }

        } while (keepRunning());

        // reset after the loop is finished
        doStop = false;
        System.out.println("stopped continuous logging.");
    }
}
