package com.opendesk.openhttp;

/**
 * Created by Mubarak Mohideen on 6/20/2016.
 */
public interface APIBuilder<T,C> {
    public T setEndPoint(String url);
    public T setEnableSession(boolean isSessionEnabled);
    public T setRequestProperty(String field,String value);
    public C build();
}
