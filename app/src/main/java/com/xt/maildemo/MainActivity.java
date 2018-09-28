package com.xt.maildemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xt.mail.MailManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MailManager.getInstance().testSendMail();
    }
}
