package com.example.android.librarywizard;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName() + "_TAG";
    private static final String BASE_URL = "https://randomuser.me/api";
    private static final String RETROFIT_URL = "https://randomuser.me/";

    private static final String RESPONSE_CODE_EXTRA = "RESPONSE_CODE_EXTRA";
    private static final String RESPONSE_MSG_EXTRA = "RESPONSE_MSG_EXTRA";
    private static final String RESPONSE_BODY_EXTRA = "RESPONSE_BODY_EXTRA";

    private TextView nameTV;
    private TextView locationTV;
    private TextView emailTV;
    private Button displayTextBT;
    private ArrayList<Result> results;

//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            int responseCode = msg.getData().getInt(RESPONSE_CODE_EXTRA);
//            String responseMsg = msg.getData().getString(RESPONSE_MSG_EXTRA);
//            String responseBody = msg.getData().getString(RESPONSE_BODY_EXTRA);
//            postResult(responseCode, responseMsg, responseBody);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameTV = (TextView) findViewById(R.id.nameTV);
        locationTV = (TextView) findViewById(R.id.locationTV);
        emailTV = (TextView) findViewById(R.id.emailTV);
        displayTextBT = (Button) findViewById(R.id.displayText);
        displayTextBT.setOnClickListener(this);
        results = new ArrayList<Result>();
        // bindings ( dependency injection, data binding, etc) set up what you need
    }

    public void postResult(String fullName, String location, String email) {
        nameTV.setText(fullName);
        locationTV.setText(location);
        emailTV.setText(email);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  doRetrofitNetworkCall();
    }

    public void onClick(View v) {
        doRetrofitNetworkCall();
    }

    // Native way using HTTPURLConnection
    // Using OkHttp Library
    // Using Retrofit Library <--- is the common

//    private void doNativeNetworkCall() {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Message msg = handler.obtainMessage();
//                    Bundle data = new Bundle();
//                    String body = "";
//                    URL url = new URL(BASE_URL);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    int responseCode = connection.getResponseCode();
//                    String response = connection.getResponseMessage();
//                    Log.d(TAG, "doNativeNetworkCall: Code: " + responseCode + " Message: " + response);
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String inputLine;
//                    while((inputLine=reader.readLine()) != null) {
//                        body += inputLine;
//                    }
//                    Log.d(TAG, "run: Response body: " + body);
//                    data.putInt(RESPONSE_CODE_EXTRA, responseCode);
//                    data.putString(RESPONSE_MSG_EXTRA, response);
//                    data.putString(RESPONSE_BODY_EXTRA, body);
//                    msg.setData(data);
//                    handler.sendMessage(msg);
//
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }

//    private void doOkHttpNetworkCall() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(BASE_URL)
//                .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    // you handle internet access
//                    // you handle retry policies
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if(response.isSuccessful()) {
//                        // here is success
//                        final int code = response.code();
//                        final String msg = response.message();
//                        final String body = response.body().toString();
//                        MainActivity.this.runOnUiThread((new Runnable() {
//                            @Override
//                            public void run() {
//                                postResult(code, msg, body);
//                            }
//                        }));
//                    }
//                    else {
//                        // here is error
//                        // create a new token for credentials
//                    }
//                }
//            });
//
//    }

    private void doRetrofitNetworkCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RETROFIT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<Example> call = service.getRandomUser();
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, retrofit2.Response<Example> response) {
                if(response.isSuccessful()) {
                    Example randomAPI = response.body();
                    for(Result result:randomAPI.getResults()) {
                        Log.d(TAG, "doRetrofitNetworkCall: Name is " + result.getName());
                        String fullName = result.getName().getFirst() + " " + result.getName().getLast();
                        String address = result.getLocation().getStreet()+ "\n" + result.getLocation().getCity() + ", "
                                + result.getLocation().getState()+ ", " + result.getLocation().getPostcode();
                        String email = result.getEmail();
                        postResult(fullName, address, email);
                        results.add(result);
                        Log.d(TAG, "onResponse: " + results.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d(TAG, "doRetrofitNetworkCall: " + t.getMessage());
            }
        });
    }

}
