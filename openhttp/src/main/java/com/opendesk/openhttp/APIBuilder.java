package com.opendesk.openhttp;

import org.json.JSONObject;

/**
 * Created by Mubarak Mohideen on 6/20/2016.
 */
public interface APIBuilder<T,C> {
    public T setEndPoint(String url);
    public T setEnableSession(boolean isSessionEnabled);
    public C build();
}
