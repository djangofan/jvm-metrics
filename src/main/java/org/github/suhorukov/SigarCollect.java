package org.github.suhorukov;

import kamon.sigar.SigarProvisioner;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.jolokia.converter.json.ObjectToJsonConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SigarCollect {

    private Sigar sigar;
    private long pid;
    private ObjectToJsonConverter jsonConverter;

    public SigarCollect() {
        try {
            SigarProvisioner.provision();
            sigar = new Sigar();
            pid = sigar.getPid();
            jsonConverter = JsonUtils.createObjectToJsonConverter();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public long getPid() {
        return pid;
    }

    public String getJsonFullInfo(long pid){
        return getJsonFullInfo(pid, null);
    }

    public String getJsonFullInfo(long pid, Map<String, Object> additionalRecords){
        return toJson(getFullInfo(pid, additionalRecords));
    }

    public String getJsonFullInfo(){
        return getJsonFullInfo(null);
    }

    public String getJsonFullInfo(Map<String, Object> additionalRecords){
        return toJson(getFullInfo(additionalRecords));
    }

    public String getJsonProcessInfo(){
        return getJsonProcessInfo(null);
    }

    public String getJsonProcessInfo(Map<String, Object> additionalRecords){
        return toJson(getProcessInfo(additionalRecords));
    }

    public String getJsonProcessInfo(long pid){
        return getJsonProcessInfo(pid, null);
    }

    public String getJsonProcessInfo(long pid, Map<String, Object> additionalRecords){
        return toJson(getProcessInfo(pid, additionalRecords));
    }

    public String getJsonSystemInfo(){
        return getJsonSystemInfo(null);
    }

    public String getJsonSystemInfo(Map<String, Object> additionalRecords){
        return toJson(getSystemInfo(additionalRecords));
    }

    public Map<String, Object> getProcessInfo() {
        return getProcessInfo(pid, null);
    }

    public Map<String, Object> getProcessInfo(Map<String, Object> additionalRecords) {
        return getProcessInfo(pid, additionalRecords);
    }

    public Map<String, Object> getFullInfo(long pid) {
        return getFullInfo(pid, null);
    }

    public Map<String, Object> getFullInfo(long pid, Map<String, Object> additionalRecords) {
        Map<String, Object> sigarInfo = createSigarInfo(additionalRecords);
        fillProcessInfo(pid, sigarInfo);
        fillSystemWideInfo(sigarInfo);
        return sigarInfo;
    }

    public Map<String, Object> getFullInfo() {
        return getFullInfo(null);
    }

    public Map<String, Object> getFullInfo(Map<String, Object> additionalRecords) {
        Map<String, Object> sigarInfo = createSigarInfo(additionalRecords);
        fillProcessInfo(pid, sigarInfo);
        fillSystemWideInfo(sigarInfo);
        return sigarInfo;
    }

    public Map<String, Object> getProcessInfo(long pid) {
        return getProcessInfo(pid, null);
    }

    public Map<String, Object> getProcessInfo(long pid, Map<String, Object> additionalRecords) {
        Map<String, Object> sigarInfo = createSigarInfo(additionalRecords);
        fillProcessInfo(pid, sigarInfo);
        return sigarInfo;
    }

    public Map<String, Object> getSystemInfo() {
        return getSystemInfo(null);
    }

    public Map<String, Object> getSystemInfo(Map<String, Object> additionalRecords) {
        Map<String, Object> sigarInfo = createSigarInfo(additionalRecords);
        fillProcessInfo(pid, sigarInfo);
        fillSystemWideInfo(sigarInfo);
        return sigarInfo;
    }

    private Map<String, Object> createSigarInfo(Map<String, Object> additionalRecords) {
        Map<String, Object> sigarInfo = new HashMap<String, Object>();
        sigarInfo.put("@timestamp", new Date());
        sigarInfo.put("@version", "1");
        if(additionalRecords!=null && !additionalRecords.isEmpty()) {
            sigarInfo.putAll(additionalRecords);
        }
        return sigarInfo;
    }

    private String toJson(Map<String, Object> sigarInfo) {
        return JsonUtils.toJson(jsonConverter, sigarInfo);
    }

    private void fillProcessInfo(long pid, Map<String, Object> sigarInfo) {
        try {
            sigarInfo.put("ProcMem", sigar.getProcMem(pid));
            sigarInfo.put("ProcState", sigar.getProcState(pid));
            sigarInfo.put("ProcTime", sigar.getProcTime(pid));
            sigarInfo.put("ProcCpu", sigar.getProcCpu(pid));
            sigarInfo.put("ProcFd", sigar.getProcFd(pid));
        } catch (SigarException e) {
            throw new RuntimeException(e);
        }
        sigarInfo.put("Pid", pid);
    }

    private void fillSystemWideInfo(Map<String, Object> sigarInfo) {
        try {
            sigarInfo.put("Pid", sigar.getPid());
            sigarInfo.put("Uptime", sigar.getUptime());
            sigarInfo.put("Mem", sigar.getMem());
            sigarInfo.put("Swap", sigar.getSwap());
            sigarInfo.put("Cpu", sigar.getCpu());
            sigarInfo.put("CpuPerc", sigar.getCpuPerc());
            sigarInfo.put("CpuPercList", sigar.getCpuPercList());
            sigarInfo.put("ResourceLimit", sigar.getResourceLimit());
            sigarInfo.put("ProcList", sigar.getProcList());
            sigarInfo.put("ProcStat", sigar.getProcStat());
            sigarInfo.put("FileSystemList", sigar.getFileSystemList());
            sigarInfo.put("FileSystemMap", sigar.getFileSystemMap());
            sigarInfo.put("CpuInfoList", sigar.getCpuInfoList());
            sigarInfo.put("CpuList", sigar.getCpuList());
            sigarInfo.put("NetRouteList", sigar.getNetRouteList());
            sigarInfo.put("NetStat", sigar.getNetStat());
            sigarInfo.put("WhoList", sigar.getWhoList());
            sigarInfo.put("Tcp", sigar.getTcp());
            sigarInfo.put("NetInfo", sigar.getNetInfo());
            sigarInfo.put("NetInterfaceConfig", sigar.getNetInterfaceConfig());
            sigarInfo.put("NetInterfaceList", sigar.getNetInterfaceList());
            sigarInfo.put("FQDN", sigar.getFQDN());
            sigarInfo.put("LoadAverage", sigar.getLoadAverage());
        } catch (SigarException e) {
            throw new RuntimeException(e);
        }
    }
}
