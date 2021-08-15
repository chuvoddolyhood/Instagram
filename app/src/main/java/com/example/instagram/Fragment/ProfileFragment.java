package com.example.instagram.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.PhotoAdapter;
import com.example.instagram.EditProfileActivity;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.OptionsActivity;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;
    ImageButton my_photos, saved_photo;

    FirebaseUser firebaseUser;
    String profileid;

    View view;

    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    List<Post> postList;

    private List<String> saveList;
    RecyclerView recyclerView_save;
    PhotoAdapter photoAdapter_save;
    List<Post> postList_save;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Lay profileid trong SharedPre
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        profileid = preferences.getString("profileid", "none");
        profileid = sharedPreferences.getString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        anhXa();

        userInfo();
        getFollowersandFollowing();
        getNrPosts();

        if(profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        } else {
            checkFollow();
            saved_photo.setVisibility(View.GONE);
        }

        /*
        ProfileFragment su dung chung cho user va individual
        Xet dieu kien cac nut "Edit Profile" or "Follow" or "Following"
        */
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();
                if(btn.equals("Edit Profile")){
                    //go to Edit Profile
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivity(intent);
                } else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    //Tao noti
                    addNotification();
                } else if(btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                    //Tao noti
                    addNotification_unfollow();
                }
            }
        });

        //Hien thi hinh anh ra grid o muc main
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(photoAdapter);
        myPhotos();

        //Hien thi hinh anh ra grid o muc save
        recyclerView_save.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_save = new GridLayoutManager(getContext(), 3);
        recyclerView_save.setLayoutManager(linearLayoutManager_save);
        postList_save = new ArrayList<>();
        photoAdapter_save = new PhotoAdapter(getContext(), postList_save);
        recyclerView_save.setAdapter(photoAdapter_save);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_save.setVisibility(View.GONE);

        //Xu ly su kien khi bam nut Photo tren Profile
        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);
            }
        });

        //Xu ly su kien khi bam nut Save tren Profile
        saved_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_save.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        //Doc danh sach hinh anh save
        mySave();

        //Event: Bam vao nut 3 gach trong profile de lua chon options
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }

                User user = snapshot.getValue(User.class);
//                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);

                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersandFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow")
                .child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Dem so luong bai post tren nf
    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Show photo from firebase
    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post mpost = dataSnapshot.getValue(Post.class);
                    if(mpost.getPublisher().equals(profileid)){
                        postList.add(mpost);
                    }
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mySave(){
        saveList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    saveList.add(dataSnapshot.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_save.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    for(String id : saveList){
                        if(post.getPostid().equals(id)){
                            postList_save.add(post);
                        }
                    }
                }
                photoAdapter_save.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Them thong tin vao Firebase khi co thong bao Notification co nguoi follow
    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        Map<String, Object> map = new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text", "started following you");
        map.put("postid", "");
        map.put("isPost", false);

        reference.push().setValue(map);
    }

    //Them thong tin vao Firebase khi co thong bao Notification co nguoi unfollowed
    private void addNotification_unfollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        Map<String, Object> map = new HashMap<>();
        map.put("userid", firebaseUser.getUid());
        map.put("text", "unfollowed you");
        map.put("postid", "");
        map.put("isPost", false);

        reference.push().setValue(map);
    }

    private void anhXa(){
        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options_profile);
        posts = view.findViewById(R.id.posts_number_profile);
        followers = view.findViewById(R.id.followers_number_profile);
        following = view.findViewById(R.id.following_number_profile);
        fullname = view.findViewById(R.id.fullname_profile);
        bio = view.findViewById(R.id.bio_profile);
        username = view.findViewById(R.id.username_profile);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_photos = view.findViewById(R.id.my_photo_profile);
        saved_photo = view.findViewById(R.id.savePhoto_profile);
        recyclerView = view.findViewById(R.id.recycler_view_profile);
        recyclerView_save = view.findViewById(R.id.recycler_view_save_profile);
    }
}
