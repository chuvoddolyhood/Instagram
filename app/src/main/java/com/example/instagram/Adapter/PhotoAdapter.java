package com.example.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragment.PostDetailFragment;
import com.example.instagram.Model.Post;
import com.example.instagram.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{
    private Context context;
    private List<Post> postList;

    public PhotoAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photos_item, parent, false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        Glide.with(context).load(post.getPostimage()).into(holder.post_image);

        //Event: khi bam vao moi tam anh trong grid -> show detail
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image_gridview_profile);
        }
    }
}
