package com.opendesk.openhttp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebServiceHelper {

	private static final WebServiceHelper webServiceHelper = new WebServiceHelper();

	private final int TIMEOUT = 1000 * 2;
	private final String CONTENT_TYPE_KEY = "Content-Type";
	private final String CONTENT_TYPE_VALUE = "application/json";
	private final String SET_COOKIE_KEY = "Set-Cookie";
	private final String COOKIE_KEY = "Cookie";
	private HttpURLConnection httpURLConnection;
	private int requestResponseCode;
	private JSONObject jsonResponseObject;
	private StringBuilder sb;
	private OutputStream outputStream;
	private StringBuilder cookie = null;
	private boolean isSessionEnabled = false;

	private WebServiceHelper() {

	}

	public static JSONObject runService(String url, JSONObject object, RequestType request) {
		return webServiceHelper.callService(url, object, request);
	}

	private JSONObject callService(String url, JSONObject object, RequestType request) {
		try {

			httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
			httpURLConnection.setReadTimeout(TIMEOUT);
			httpURLConnection.setConnectTimeout(TIMEOUT);
			httpURLConnection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);

			if (cookie != null) {
				httpURLConnection.setRequestProperty(COOKIE_KEY, cookie.toString());
			}

			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod(request.name());
			httpURLConnection.connect();

			Log.d("URL", url);

			// Checking HTTP request method type
			switch (request) {

			case GET:
				break;

			case POST:
			case PUT:
				if (object != null) {
					Log.d("Post Data", object.toString());
					byte[] outputInBytes = object.toString().getBytes("UTF-8");
					outputStream = httpURLConnection.getOutputStream();
					outputStream.write(outputInBytes);
					outputStream.flush();
					outputStream.close();
				}
				break;

			case DELETE:
				break;

			default:
				break;
			}

			requestResponseCode = httpURLConnection.getResponseCode();

			if (requestResponseCode == HttpURLConnection.HTTP_OK) {
				sb = new StringBuilder();
				InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream(), "iso-8859-1");
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					sb.append(buff, 0, read);

				}
				Log.d("Response Data", sb.toString());

				if (isSessionEnabled) {
					getCookie();
				}

				jsonResponseObject = new JSONObject(sb.toString());
			} else
				Log.d("Error", "Http " + requestResponseCode + " Error From URL: "+url);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("JSON Parser", "ERROR: " + e.toString());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return jsonResponseObject;

	}

	public static void setEnableSession(boolean enableSession) {
		webServiceHelper.isSessionEnabled = enableSession;
	}

	private StringBuilder getCookie() {
		Map<String, List<String>> headerList = httpURLConnection.getHeaderFields();
		if (headerList != null && headerList.containsKey(SET_COOKIE_KEY)) {
			List<String> headers = headerList.get(SET_COOKIE_KEY);
			if (headers != null && !headers.isEmpty()) {
				cookie = new StringBuilder();
				for (String value : headers) {
					cookie.append(value);
				}
			}

		}
		return cookie;
	}

}
