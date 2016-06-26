package com.opendesk.openhttp;

import org.json.JSONObject;

import android.os.AsyncTask;

public class MakeAPICall extends AsyncTask<Void, Void, JSONObject>{

	private String url;
	private JSONObject jsonObject;
	private RequestType requestType;
	private OnResponseListener onCommonAsyncTask;
	private int tag;
	private boolean isSessionEnabled;

	private MakeAPICall(){
		super();
	}

	private MakeAPICall(String url,JSONObject jsonObject,RequestType requestType,OnResponseListener onCommonAsyncTask,int tag) {
		super();
		this.url = url;
		this.jsonObject = jsonObject;
		this.requestType = requestType;
		this.onCommonAsyncTask = onCommonAsyncTask;
		this.tag = tag;
	}

	private MakeAPICall setEnableSession(boolean isSessionEnabled){
		this.isSessionEnabled = isSessionEnabled;
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		WebServiceHelper.setEnableSession(isSessionEnabled);
	}
	

	@Override
	protected JSONObject doInBackground(Void... params) {
		return WebServiceHelper.runService(url, jsonObject, requestType);
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		onCommonAsyncTask.onSuccess(tag,result);
	}

	public static class Create implements APIExecuter<Create>{

		private String endPoint;
		private RequestType requestType;
		private String urlPath;
		private JSONObject jsonPostObject;
		private OnResponseListener onCommonAsyncTask;
		private int tag;
		private boolean isSessionEnabled;

		private Create(){

		}

		private Create(String endPoint,boolean isSessionEnabled){
			this.endPoint = endPoint;
			this.isSessionEnabled = isSessionEnabled;
		}

		@Override
		public Create setRequestType(RequestType requestType) {
			this.requestType = requestType;
			return this;
		}

		@Override
		public Create setURLPath(String urlPath) {
			this.urlPath = endPoint+urlPath;
			return this;
		}

		@Override
		public Create setPostData(JSONObject jsonPostObject) {
			this.jsonPostObject = jsonPostObject;
			return this;
		}

		@Override
		public Create getResponse(OnResponseListener onCommonAsyncTask) {
			this.onCommonAsyncTask = onCommonAsyncTask;
			return this;
		}

		@Override
		public Create setTag(int tag) {
			this.tag = tag;
			return this;
		}



		@Override
		public void run() {
			new MakeAPICall(urlPath, jsonPostObject, requestType, onCommonAsyncTask,tag)
					.setEnableSession(isSessionEnabled).execute();
		}

	}

	public static class Builder implements APIBuilder<Builder,Create> {

		private String url;
		private boolean isSessionEnabled;

		public Builder(){
		}

		@Override
		public Builder setEndPoint(String url) {
			this.url = url;
			return this;
		}
		@Override
		public Builder setEnableSession(boolean isSessionEnabled) {
			this.isSessionEnabled = isSessionEnabled;
			return this;
		}

		@Override
		public Create build() {
			return new Create(url,isSessionEnabled);
		}
	}


}
