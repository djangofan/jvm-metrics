package org.github.suhorukov;

import kamon.sigar.SigarProvisioner;
import org.hyperic.sigar.Sigar;
import org.jolokia.converter.json.ObjectToJsonConverter;
import sun.management.counter.Counter;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class PerformanceCounters {

    public static final String VALUE_FIELD = "value";
    public static final String VARIABILITY_FIELD = "variability";
    public static final String UNITS_FIELD = "units";
    public static final String READ_ONLY_FLAG = "r";
    public static final int INITIAL_VALUE_MAP_CAPACITY = 3;
    private final int pid;

    private ObjectToJsonConverter jsonConverter;

    public PerformanceCounters() {
        try {
            SigarProvisioner.provision();
            pid = (int) new Sigar().getPid();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
        this.jsonConverter = JsonUtils.createObjectToJsonConverter();
    }

    public PerformanceCounters(int pid) {
        this.pid = pid;
        this.jsonConverter = JsonUtils.createObjectToJsonConverter();
    }

    public Map<String, Object> getBaseInfo() throws IOException {
        return getBaseInfo(null);
    }

    public Map<String, Object> getBaseInfo(String filter) throws IOException {
        return getPerformanceCounters(pid, filter).getBaseCounters();

    }

    public Map<String, Map<String, Object>> getFullInfo() throws IOException {
        return getFullInfo(null);
    }

    public Map<String, Map<String, Object>> getFullInfo(String filter) throws IOException {
        return getPerformanceCounters(pid, filter).getExtendedCounters();
    }

    public String getJsonFullInfo() throws IOException {
        return getJsonFullInfo(null);
    }

    public String getJsonFullInfo(Map<String, Object> additionalRecords) throws IOException {
        return getJsonFullInfo(null, additionalRecords);
    }

    public String getJsonFullInfo(String filter, Map<String, Object> additionalRecords) throws IOException {
        return JsonUtils.toJson(jsonConverter, appendRecords(additionalRecords, getFullInfo(filter)));
    }

    public String getJsonBaseInfo() throws IOException {
        return getJsonBaseInfo(null);
    }
    public String getJsonBaseInfo(Map<String, Object> additionalRecords) throws IOException {
        return getJsonBaseInfo(null, additionalRecords);
    }

    public String getJsonBaseInfo(String filter, Map<String, Object> additionalRecords) throws IOException {
        return JsonUtils.toJson(jsonConverter, appendRecords(additionalRecords, getBaseInfo(filter)));
    }

    private Map<String, ?> appendRecords(Map<String, Object> additionalRecords, Map fullInfo) {
        if(additionalRecords!=null && !additionalRecords.isEmpty()){
            fullInfo.putAll(additionalRecords);
        }
        return fullInfo;
    }

    private static TransformCountersToMap getPerformanceCounters(int pid, String filter) throws IOException {
        Perf perf = Perf.getPerf();
        ByteBuffer buffer = perf.attach(pid, READ_ONLY_FLAG);
        PerfInstrumentation perfInstrumentation = new PerfInstrumentation(buffer);
        List<Counter> countersList = (filter!=null && !filter.isEmpty())? perfInstrumentation.findByPattern(filter):perfInstrumentation.getAllCounters();
        return new TransformCountersToMap(countersList).invoke();
    }

    private static class TransformCountersToMap {
        private List<Counter> countersList;
        private HashMap<String, Object> baseCounters;
        private HashMap<String, Map<String, Object>> extendedCounters;

        public TransformCountersToMap(List<Counter> countersList) {
            this.countersList = countersList;
        }

        public HashMap<String, Object> getBaseCounters() {
            return baseCounters;
        }

        public HashMap<String, Map<String, Object>> getExtendedCounters() {
            return extendedCounters;
        }

        public TransformCountersToMap invoke() {
            baseCounters = new HashMap<String, Object>(countersList.size());
            extendedCounters = new HashMap<String, Map<String, Object>>(countersList.size());
            for(Counter counterObj: countersList) {
                Counter counter = (Counter) counterObj;
                HashMap<String,Object> value = new HashMap<String,Object>(INITIAL_VALUE_MAP_CAPACITY);
                value.put(UNITS_FIELD, counter.getUnits().toString());
                value.put(VARIABILITY_FIELD, counter.getVariability().toString());
                value.put(VALUE_FIELD, counter.getValue());
                extendedCounters.put(counter.getName(), value);
                baseCounters.put(counter.getName(), counter.getValue());
            }
            return this;
        }
    }
}
