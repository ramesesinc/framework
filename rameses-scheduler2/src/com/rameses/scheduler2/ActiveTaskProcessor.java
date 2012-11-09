/*
 * ActiveTaskRunnable.java
 * Created on October 27, 2011, 9:11 AM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.scheduler2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author emn
 *
 * This processor launches all taskbeans within this queue
 *
 */
public class ActiveTaskProcessor extends AbstractTaskProcessor {
    
    ActiveTaskProcessor(ScheduleManager s) {
        super(s);
    }
    
    public void run() {
        ExecutorService executors = Executors.newCachedThreadPool();
        TaskBean t = null;
        while( (t=queue.poll())!=null ) {
            executors.submit( new TaskExecutor(this.getManager(), t) );
        }
        executors.shutdown();
    }
    
    
}
