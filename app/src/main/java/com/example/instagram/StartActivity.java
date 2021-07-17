package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    Button btnLogin, btnRegister;

    FirebaseUser firebaseUser;

    //Hình như nếu đăng nhập thành công sẽ tự vào thẳng mainActivity
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //redirect if user is not null
        if(firebaseUser != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        anhXa();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }

    private void anhXa() {
        btnLogin = findViewById(R.id.btnLogin_Start);
        btnRegister = findViewById(R.id.btnRegister_Start);
    }
}