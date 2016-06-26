package com.opendesk.openhttpsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.opendesk.openhttp.MakeAPICall;
import com.opendesk.openhttp.OnResponseListener;
import com.opendesk.openhttp.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnResponseListener {

    private final String BASE_URL ="https://expensetracker-opendesk.rhcloud.com/";
    private final String LOGIN_PATH = "users/login.json";
    private final String USER_CATEGORY_PATH = "expensecategories/index.json";

    private final int LOGIN =1;
    private final int CATEGORY = 2;

    private MakeAPICall.Create creater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSONObject jsonObject =  new JSONObject();
        try {
            jsonObject.putOpt("username", "mubarak");
            jsonObject.putOpt("password", "Simple123");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        creater = new MakeAPICall.Builder()
                .setEndPoint(BASE_URL)
                .setEnableSession(true)
                .build();

        creater.setURLPath(LOGIN_PATH)
                .setRequestType(RequestType.POST)
                .setPostData(jsonObject)
                .setTag(LOGIN)
                .getResponse(this)
                .run();

        creater.setURLPath(USER_CATEGORY_PATH)
                .setRequestType(RequestType.GET)
                .getResponse(this)
                .setTag(CATEGORY);


    }

    @Override
    public void onSuccess(int id, JSONObject jsonObject) {
        switch (id){
            case LOGIN:
                creater.run();
                break;
            case CATEGORY:
                break;
        }
    }

    @Override
    public void OnFailure(int id, String info) {
        switch (id){
            case LOGIN:
                break;
            case CATEGORY:
                break;
        }
    }
}
