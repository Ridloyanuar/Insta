package org.ridlo.insta;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.ridlo.insta.data.Posts;
import org.ridlo.insta.data.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {

    public List<Posts> post_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public MyPostsAdapter(List<Posts> post_list) {
        this.post_list = post_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.galeri_item,viewGroup,false);

        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.setIsRecyclable(false);

        final String resepPostId = post_list.get(position).PostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String image_uri = post_list.get(position).getImage_url();
        String thumbUri = post_list.get(position).getThumb();
        viewHolder.setBlogImage(image_uri,thumbUri);


    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView blogImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setBlogImage(String downloadUri,String thumb){
            blogImageView = mView.findViewById(R.id.iv_galeri);

            RequestOptions requestOptions = new RequestOptions();

            requestOptions.placeholder(R.drawable.user_male);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
              Glide.with(context).load(thumb)
            ).into(blogImageView);

        }


    }
}


