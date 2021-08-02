package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    EditText addComment;
    TextView postComment;
    ImageView imageProfile;
    Toolbar toolbar;

    String postid;
    String publisher;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        anhXa();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisher = intent.getStringExtra("publisher");

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComments();
                }
            }
        });

        getImage();
    }

    //Them comment vao firebase
    private void addComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        Map<String, Object> map = new HashMap<>();
        map.put("comment", addComment.getText().toString());
        map.put("publisher", firebaseUser.getUid());

        reference.push().setValue(map);
        addComment.setText("");
    }

    //Lay hinh anh tu firebase bo vao image avatar o comment
    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void anhXa() {
        toolbar = findViewById(R.id.toolbar);
        addComment = findViewById(R.id.txt_image_profile_comments);
        postComment = findViewById(R.id.post_comments);
        imageProfile = findViewById(R.id.image_profile_comments);
    }
}