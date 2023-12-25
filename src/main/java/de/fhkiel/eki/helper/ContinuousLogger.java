package de.fhkiel.eki.helper;

import java.io.PrintStream;

public class ContinuousLogger implements Runnable {

    private static final int CONTINUOUS_LOGGING_DELAY = 5_000;

    private final PrintStream console;

    private boolean doStop = false;

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
        while (keepRunning()) {
            try {
                Thread.sleep(CONTINUOUS_LOGGING_DELAY);
                console.print("...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // reset after the loop is finished
        doStop = false;
        System.out.println("stopped continuous logging.");
    }
}
