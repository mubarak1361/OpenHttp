package com.opendesk.openhttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHelper {

    private static HttpHelper httpHelper = new HttpHelper();

    private final int TIMEOUT = 1000 * 2;
    private final String CONTENT_TYPE_KEY = "Content-Type";
    private final String CONTENT_TYPE_VALUE = "application/json";
    private final String SET_COOKIE_KEY = "Set-Cookie";
    private final String COOKIE_KEY = "Cookie";
    private HttpURLConnection httpURLConnection;
    private int requestResponseCode;
    private StringBuilder sb;
    private StringBuilder cookie = null;
    private boolean isSessionEnabled = false;
    private Map<String, String> requestProperties;
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    private HttpHelper() {

    }

    private JSONObject httpConnect(String url, String charset,Map<String,Object> fields) {
        this.charset = charset;
        try {
            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            if (cookie != null) {
                httpURLConnection.setRequestProperty(COOKIE_KEY, cookie.toString());
            }
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);    // indicates POST method
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty(CONTENT_TYPE_KEY, "multipart/form-data; boundary=" + boundary);
            outputStream = httpURLConnection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            for (Map.Entry<String, Object> field : fields.entrySet()) {
                if(field.getValue() instanceof  File){
                    addFilePart(field.getKey(),(File)field.getValue());
                }else{
                   addFormField(field.getKey(),(String)field.getValue());
                }
            }
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

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

                return new JSONObject(sb.toString());
            } else
                Log.d("Error", "Http " + requestResponseCode + " Error From URL: " + url);
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    private void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
    }


    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    public static void setRequestProperties(Map<String, String> requestProperties) {
        httpHelper.requestProperties = requestProperties;
    }

    public static JSONObject connectHttp(String url, JSONObject object, RequestType request) {
        return httpHelper.httpConnect(url, object, request);
    }

    private JSONObject httpConnect(String url, JSONObject object, RequestType request) {
        try {

            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setConnectTimeout(TIMEOUT);

            if (requestProperties == null || requestProperties.isEmpty() || !requestProperties.containsKey(CONTENT_TYPE_KEY))
                httpURLConnection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);

            if (requestProperties != null) {
                for (Map.Entry<String, String> requestProperty : requestProperties.entrySet()) {
                    httpURLConnection.setRequestProperty(requestProperty.getKey(), requestProperty.getValue());
                }
            }

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
                InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    sb.append(buff, 0, read);

                }
                Log.d("Response Data", sb.toString());

                if (isSessionEnabled) {
                    getCookie();
                }

                return new JSONObject(sb.toString());
            } else
                Log.d("Error", "Http " + requestResponseCode + " Error From URL: " + url);
            return null;

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
        return null;

    }

    public static void setEnableSession(boolean enableSession) {
        httpHelper.isSessionEnabled = enableSession;
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
