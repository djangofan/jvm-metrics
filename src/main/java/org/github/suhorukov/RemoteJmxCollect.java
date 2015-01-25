package org.github.suhorukov;

import org.jolokia.converter.Converters;
import org.jolokia.detector.ServerHandle;
import org.jolokia.jsr160.Jsr160RequestDispatcher;
import org.jolokia.restrictor.AllowAllRestrictor;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 */
public class RemoteJmxCollect extends JmxCollect{

    private String url;
    private String user;
    private String password;


    public RemoteJmxCollect(String url) {
        this(url, null, null);
    }

    public RemoteJmxCollect(String url, String user, String password) {
        super(new Jsr160RequestDispatcher(new Converters(), new ServerHandle(null, null, null, null), new AllowAllRestrictor()));
        if(url==null){
            throw new IllegalArgumentException("url");
        }
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void fillRequestParams(JSONObject params) {
        HashMap target = new HashMap();
        target.put("url", url);
        if(user!=null && password!=null){
            target.put("user", user);
            target.put("password", password);
        }
        params.put("target", target);
    }
}
