package org.github.suhorukov;

import kamon.sigar.SigarProvisioner;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.jolokia.converter.json.JsonConvertOptions;
import org.jolokia.converter.json.ObjectToJsonConverter;
import org.jolokia.converter.object.StringToObjectConverter;
import org.json.simple.JSONAware;

import javax.management.AttributeNotFoundException;
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
            jsonConverter = new ObjectToJsonConverter(new StringToObjectConverter());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public long getPid() {
        return pid;
    }

    public String getJsonFullInfo(long pid){
        return toJson(getFullInfo(pid));
    }

    public String getJsonFullInfo(){
        return toJson(getFullInfo());
    }

    public String getJsonProcessInfo(){
        return toJson(getProcessInfo());
    }

    public String getJsonProcessInfo(long pid){
        return toJson(getProcessInfo(pid));
    }

    public String getJsonSystemInfo(){
        return toJson(getSystemInfo());
    }

    public Map<String, Object> getProcessInfo() {
        return getProcessInfo(pid);
    }

    public Map<String, Object> getFullInfo(long pid) {
        Map<String, Object> sigarInfo = createSigarInfo();
        fillProcessInfo(pid, sigarInfo);
        fillSystemWideInfo(sigarInfo);
        return sigarInfo;
    }

    public Map<String, Object> getFullInfo() {
        Map<String, Object> sigarInfo = createSigarInfo();
        fillProcessInfo(pid, sigarInfo);
        fillSystemWideInfo(sigarInfo);
        return sigarInfo;
    }

    public Map<String, Object> getProcessInfo(long pid) {
        Map<String, Object> sigarInfo = createSigarInfo();
        fillProcessInfo(pid, sigarInfo);
        return sigarInfo;
    }

    public Map<String, Object> getSystemInfo() {
        Map<String, Object> sigarInfo = createSigarInfo();
        fillProcessInfo(pid, sigarInfo);
        fillSystemWideInfo(sigarInfo);
        return sigarInfo;
    }

    private String toJson(Map<String, Object> sigarInfo) {
        try {
            JSONAware json = (JSONAware) jsonConverter.convertToJson(sigarInfo, null, JsonConvertOptions.DEFAULT);
            return json.toJSONString();
        } catch (AttributeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> createSigarInfo() {
        Map<String, Object> sigarInfo = new HashMap<String, Object>();
        sigarInfo.put("@timestamp", new Date());
        sigarInfo.put("@version", "1");
        return sigarInfo;
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
