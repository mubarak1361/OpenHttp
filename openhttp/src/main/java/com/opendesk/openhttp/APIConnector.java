package com.opendesk.openhttp;

import org.json.JSONObject;

/**
 * Created by Mubarak Mohideen on 6/22/2016.
 */
interface APIConnector<T> {
    public T setRequestType(RequestType requestType);
    public T setURLPath(String urlPath);
    public T setPostData(JSONObject jsonObject);
    public T getResponse(OnResponseListener onResponseListener);
    public T setTag(int id);
    public void connect();
}
