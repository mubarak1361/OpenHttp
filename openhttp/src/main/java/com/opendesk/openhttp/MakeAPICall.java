package com.opendesk.openhttp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.Map;

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

	private MakeAPICall setRequestProperties(Map<String,String> requestProperties){
		HttpHelper.setRequestProperties(requestProperties);
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		HttpHelper.setEnableSession(isSessionEnabled);
	}
	

	@Override
	protected JSONObject doInBackground(Void... params) {
		return HttpHelper.connectHttp(url, jsonObject, requestType);
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		onCommonAsyncTask.onSuccess(tag,result);
	}

	public static class Connecter implements APIConnecter<Connecter> {

		private String endPoint;
		private RequestType requestType;
		private String urlPath;
		private JSONObject jsonPostObject;
		private OnResponseListener onCommonAsyncTask;
		private int tag;
		private boolean isSessionEnabled;
		private Map<String,String> requestProperties;

		private Connecter(){

		}

		private Connecter(String endPoint,boolean isSessionEnabled,Map<String,String> requestProperties){
			this.endPoint = endPoint;
			this.isSessionEnabled = isSessionEnabled;
			this.requestProperties = requestProperties;
		}

		@Override
		public Connecter setRequestType(RequestType requestType) {
			this.requestType = requestType;
			return this;
		}

		@Override
		public Connecter setURLPath(String urlPath) {
			this.urlPath = endPoint+urlPath;
			return this;
		}

		@Override
		public Connecter setPostData(JSONObject jsonPostObject) {
			this.jsonPostObject = jsonPostObject;
			return this;
		}

		@Override
		public Connecter getResponse(OnResponseListener onCommonAsyncTask) {
			this.onCommonAsyncTask = onCommonAsyncTask;
			return this;
		}

		@Override
		public Connecter setTag(int tag) {
			this.tag = tag;
			return this;
		}



		@Override
		public void connect() {
			new MakeAPICall(urlPath, jsonPostObject, requestType, onCommonAsyncTask,tag)
					.setEnableSession(isSessionEnabled).setRequestProperties(requestProperties).execute();
		}

	}

	public static class Builder implements APIBuilder<Builder,Connecter> {

		private String url;
		private boolean isSessionEnabled;
		private Map<String,String> requestProperties;

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
		public Builder setRequestProperty(Map<String,String> requestProperties) {
				this.requestProperties = requestProperties;
			return this;
		}

		@Override
		public Connecter build() {
			return new Connecter(url,isSessionEnabled,requestProperties);
		}
	}


}
