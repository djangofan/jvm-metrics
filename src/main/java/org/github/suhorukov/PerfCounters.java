package org.github.suhorukov;

import kamon.sigar.SigarProvisioner;
import org.hyperic.sigar.Sigar;
import sun.management.counter.Counter;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;

import java.nio.ByteBuffer;



/**
 */
public class PerfCounters {

    //https://github.com/infynyxx/openjdk-jmh/blob/master/jmh-core/src/main/java/org/openjdk/jmh/profile/HotspotRuntimeProfiler.java
    //http://cr.openjdk.java.net/~xlu/6880029/webrev.00/src/share/vm/runtime/safepoint.cpp.html
    //sun.rt.safepointSyncTime
    //List byPattern = perfInstrumentation.findByPattern("sun.rt.safepointSyncTime");
    public static void main(String[] args) throws Exception{
        SigarProvisioner.provision();
        long pid = new Sigar().getPid();
        Perf p = Perf.getPerf();
        ByteBuffer buffer = p.attach((int) pid, "r");
        PerfInstrumentation perfInstrumentation = new PerfInstrumentation(buffer);
        for(Object counterObj: perfInstrumentation.getAllCounters()) {
            Counter counter = (Counter) counterObj;
            System.out.println(String.format(
                    "%s = %s [Variability: %s, Units: %s]", counter.getName(), String.valueOf(counter.getValue()),
                    counter.getVariability(), counter.getUnits()));
        }
    }
}
