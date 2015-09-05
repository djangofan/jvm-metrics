package org.github.suhorukov;

import org.jolokia.backend.LocalRequestDispatcher;
import org.jolokia.backend.RequestDispatcher;
import org.jolokia.config.ConfigKey;
import org.jolokia.config.Configuration;
import org.jolokia.config.ProcessingParameters;
import org.jolokia.converter.Converters;
import org.jolokia.converter.json.JsonConvertOptions;
import org.jolokia.converter.json.ObjectToJsonConverter;
import org.jolokia.request.JmxRequest;
import org.jolokia.request.JmxRequestFactory;
import org.jolokia.restrictor.AllowAllRestrictor;
import org.jolokia.util.LogHandler;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import javax.management.AttributeNotFoundException;
import javax.management.MalformedObjectNameException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class JmxCollect {

    private RequestDispatcher requestDispatcher;
    private ObjectToJsonConverter jsonConverter;

    public JmxCollect() {
        this(new LocalRequestDispatcher(new Converters(), new AllowAllRestrictor(), new Configuration(), LogHandler.QUIET));
    }

    public JmxCollect(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
        jsonConverter = JsonUtils.createObjectToJsonConverter();
    }

    public String getJsonJmxInfo(String filter, Date requestDate) throws Exception{
        return toJson(getJmxInfo(filter, requestDate));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getJmxInfo(String filter, Date requestDate) throws Exception{
        JmxRequest jmxReadRequest = prepareJolokiaRequest(filter);
        Map<String, Object> jmxInfo = (Map<String, Object>) requestDispatcher.dispatchRequest(jmxReadRequest);
        jmxInfo.put("jmxFilter", filter);
        jmxInfo.put("@version", "1");
        jmxInfo.put("@timestamp", requestDate);
        return jmxInfo;
    }

    private String toJson(Map result) throws AttributeNotFoundException {
        JSONAware json = (JSONAware) jsonConverter.convertToJson(result, null, JsonConvertOptions.DEFAULT);
        return json.toJSONString();
    }

    @SuppressWarnings("unchecked")
    private JmxRequest prepareJolokiaRequest(String mbeanFilter) throws MalformedObjectNameException {
        JSONObject params = new JSONObject();

        params.put("type","read");
        params.put("mbean", mbeanFilter);
        fillRequestParams(params);

        ProcessingParameters processingParameters = new Configuration().
                getProcessingParameters(new HashMap<String, String>()).
                mergedParams(new HashMap<String, String>() {{
                    put(ConfigKey.IGNORE_ERRORS.getKeyValue(), "true");
                }});
        return JmxRequestFactory.createPostRequest(params, processingParameters);
    }

    protected void fillRequestParams(JSONObject params) {
    }
}
