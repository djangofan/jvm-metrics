package org.github.suhorukov;

import org.jolokia.converter.json.JsonConvertOptions;
import org.jolokia.converter.json.ObjectToJsonConverter;
import org.jolokia.converter.object.StringToObjectConverter;
import org.json.simple.JSONAware;

import javax.management.AttributeNotFoundException;
import java.util.Map;

/**
 */
public class JsonUtils {

    public static ObjectToJsonConverter createObjectToJsonConverter(){
        return new ObjectToJsonConverter(new StringToObjectConverter());
    }

    public static String toJson(ObjectToJsonConverter jsonConverter, Map<String, ?> parameters) {
        try {
            JSONAware json = (JSONAware) jsonConverter.convertToJson(parameters, null, JsonConvertOptions.DEFAULT);
            return json.toJSONString();
        } catch (AttributeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
