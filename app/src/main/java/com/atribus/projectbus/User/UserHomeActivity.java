package com.atribus.projectbus.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.atribus.projectbus.Model.User;
import com.atribus.projectbus.R;
import com.google.gson.Gson;

public class UserHomeActivity extends AppCompatActivity {
    String MY_PREFS_NAME = "Mydatabase";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj == null) {
            startActivity(new Intent(this, UserRegister.class));

            finish();
        }


        Toast.makeText(this, "welcome " + obj.getName(), Toast.LENGTH_SHORT).show();
    }
}
