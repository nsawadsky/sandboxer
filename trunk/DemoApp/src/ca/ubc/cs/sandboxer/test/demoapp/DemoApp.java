package ca.ubc.cs.sandboxer.test.demoapp;

import ca.ubc.cs.sandboxer.core.QuarantineException;
import ca.ubc.cs.sandboxer.test.logger.Logger;

public class DemoApp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Logger log = Logger.getInstance();
            log.log("Starting demo app");
            log.baseLog("Starting demo app");
            log.toString();
            
            long startTimeMillis = System.currentTimeMillis();
            
            long START_PHASE_MSECS = 7000;
            
            long runningTimeMsecs = 0;
            do {
                log.log("In start phase");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                runningTimeMsecs = System.currentTimeMillis() - startTimeMillis;
            } while (runningTimeMsecs < START_PHASE_MSECS);
            
            allocateLotsOfLoggers();
            
            try {
                log.longRunningCall();
            } catch (QuarantineException e) {
                System.out.println("DemoApp caught expected exception #1: " + e);
            }
            try {
                 log.log("Should generate QuarantineException");
            } catch (QuarantineException e) {
                System.out.println("DemoApp caught expected exception #2: " + e);
            }
        } catch (Exception e) {
            System.out.println("DemoApp caught exception: " + e);
        }
    }
    
    private static void allocateLotsOfLoggers() {
        long lastTimestampMsecs = System.currentTimeMillis();
        for (long i = 1; i <= 10000000000L; i++) {
            Logger l = new Logger();
            
            if (i % 100000 == 0) {
                long newTimestampMsecs = System.currentTimeMillis();
                System.out.println("Time to allocate 100000 objects = " + 
                        (newTimestampMsecs - lastTimestampMsecs) + " msecs");
                lastTimestampMsecs = newTimestampMsecs;
                System.out.println("Actual allocation count = " + i);
            }
        }
    }

}
