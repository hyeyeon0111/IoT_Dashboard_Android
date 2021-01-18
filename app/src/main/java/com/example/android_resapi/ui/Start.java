package com.example.android_resapi.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_resapi.R;

public class Start extends AppCompatActivity {

    final static String TAG = "AndroidAPITest";

    private Button listThingsBtn;
    EditText listThingsURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        listThingsURL = findViewById(R.id.listThingsURL);

        listThingsBtn = findViewById(R.id.list);
        listThingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String urlstr = listThingsURL.getText().toString();
                Log.i(TAG, "listThingsURL=" + urlstr);
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(getApplicationContext(), "사물목록 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ListThingsActivity.class);
                intent.putExtra("listThingsURL", listThingsURL.getText().toString());
                startActivity(intent);
            }
        });
    }
}
