package com.opendesk.openhttp;

import org.json.JSONObject;

public interface OnResponseListener {
	public void onSuccess(int tag, JSONObject jsonObject);
	public void OnFailure(int tag, String info);
}
