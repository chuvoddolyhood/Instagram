package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText username, fullname, email, password;
    Button btnRegister;
    TextView txtLogin;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        anhXa();
        auth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                //Lay du lieu tu form
                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if(str_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    Register(str_username, str_fullname, str_email, str_password);
                }
            }
        });
    }

    private void Register(String username, String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,"Dang ky thanh cong",Toast.LENGTH_SHORT).show();

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            Map<String, String> myMap = new HashMap<String, String>();
                            myMap.put("id", userid);
                            myMap.put("username", username.toLowerCase());
                            myMap.put("fullname", fullname);
                            myMap.put("bio", "");
                            myMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/facebook-e425a.appspot.com/o/icons8_male_user_50px.png?alt=media&token=d7f15be5-a67e-4d02-bb6d-5c7edeb3d3ec");

                            reference.setValue(myMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"You can't register with this email or password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void anhXa() {
        username = findViewById(R.id.edtUserName_Register);
        fullname = findViewById(R.id.edtFullName_Register);
        email = findViewById(R.id.edtEmail_Register);
        password = findViewById(R.id.edtPassword_Register);
        btnRegister = findViewById(R.id.btnRegister_Register);
        txtLogin = findViewById(R.id.txtRegister);
    }
}