package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThingShadow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceView extends AppCompatActivity {

    final static String TAG = "AndroidAPITest";

    private WebView moniteringView;
    private Timer timer;
    private String statusUrl;
    private String logUrl;
    private Button status;
    private Button log;
    private TextView distance;

    private TextView led;
    private ImageView red;
    private ImageView yellow;
    private ImageView green;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_view);

        Intent intent = getIntent();
        statusUrl = intent.getStringExtra("thingShadowURL");

        timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               new Thread() {
                                   public void run() {
                                       Message msg = handler.obtainMessage();
                                       handler.sendMessage(msg);
                                   }
                               }.start();
                               new GetThingShadow(DeviceView.this, statusUrl).execute();

                           }
                       },
                0,1000);

        led = (TextView) findViewById(R.id.reported_led);
        red = (ImageView) findViewById(R.id.red);
        yellow = (ImageView) findViewById(R.id.yellow);
        green = (ImageView) findViewById(R.id.green);

        moniteringView = (WebView) findViewById(R.id.webView);
        moniteringView.setPadding(0,0,0,0);
        moniteringView.getSettings().setBuiltInZoomControls(false);
        moniteringView.getSettings().setJavaScriptEnabled(true);
        moniteringView.getSettings().setLoadWithOverviewMode(true);
        moniteringView.getSettings().setUseWideViewPort(true);

        String url ="http://192.168.0.40:8090/stream/video.mjpeg";
        moniteringView.loadUrl(url);

        status = (Button) findViewById(R.id.status);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getApplicationContext(), DeviceActivity.class);
                newIntent.putExtra("thingShadowURL", statusUrl);
                startActivity(newIntent);
            }
        });

        log = (Button) findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logUrl = statusUrl + "/log";
                if (logUrl == null || logUrl.equals("")) {
                    Toast.makeText(getApplicationContext(), "사물로그 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), LogActivity.class);
                intent.putExtra("getLogsURL", logUrl);
                startActivity(intent);
            }
        });
    }

    public void setLED() {
        distance = (TextView) findViewById(R.id.reported_distance);
        if (Integer.parseInt(distance.getText().toString()) < 10) {
            Log.e("kkankkan", distance.getText().toString());
            led.setVisibility(View.INVISIBLE);
            red.setVisibility(View.VISIBLE);
            yellow.setVisibility(View.INVISIBLE);
            green.setVisibility(View.INVISIBLE);
        }
        else if (Integer.parseInt(distance.getText().toString()) < 20) {
            Log.e("kkankkan", distance.getText().toString());
            led.setVisibility(View.INVISIBLE);
            red.setVisibility(View.INVISIBLE);
            yellow.setVisibility(View.VISIBLE);
            green.setVisibility(View.INVISIBLE);
        }
        else {
            Log.e("kkankkan", distance.getText().toString());
            led.setVisibility(View.INVISIBLE);
            red.setVisibility(View.INVISIBLE);
            yellow.setVisibility(View.INVISIBLE);
            green.setVisibility(View.VISIBLE);
        }
    }

    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String distanceValue = reported.getString("distance");
            String ledValue = reported.getString("LED");
            output.put("reported_distance", distanceValue);
            output.put("reported_LED",ledValue);

            JSONObject desired = state.getJSONObject("desired");
            String desired_distanceValue = desired.getString("distance");
            String desired_ledValue = desired.getString("LED");
            output.put("desired_distance", desired_distanceValue);
            output.put("desired_LED",desired_ledValue);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            setLED();
        }
    };
}
