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

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public List<Posts> post_list;
    public List<Users> users_list;
    public Context context;

    private OnItemClickCallback onItemClickCallback;

    void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public PostsAdapter(List<Posts> post_list, List<Users> users_list) {
        this.post_list = post_list;
        this.users_list = users_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_card,viewGroup,false);

        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.setIsRecyclable(false);

        final String postId = post_list.get(position).PostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        String desc_data = post_list.get(position).getPostingan();
        viewHolder.setDescText(desc_data);
        String image_uri = post_list.get(position).getImage_url();
        String thumbUri = post_list.get(position).getThumb();
        viewHolder.setBlogImage(image_uri,thumbUri);

        String userName = users_list.get(position).getName();
        String userImage = users_list.get(position).getImage();
        viewHolder.setUserData(userName,userImage);


        try {
            long milliseconds = post_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
            viewHolder.setTime(dateString);

        }catch (Exception e ){
            Toast.makeText(context,"Error "+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        //Get Likes Count
        firebaseFirestore.collection("Posts/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        int count = queryDocumentSnapshots.size();
                        viewHolder.updateLikesCount(count);

                    } else {

                        viewHolder.updateLikesCount(0);
                    }
                }
            }
        });

        //Get Comment Count
        firebaseFirestore.collection("Posts/" + postId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        viewHolder.updateCommentCount(count);
                    } else {
                        viewHolder.updateCommentCount(0);
                    }
                }
            }
        });

        //Get Likes
        firebaseFirestore.collection("Posts/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {

                        viewHolder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_24));

                    } else {

                        viewHolder.blogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_black_24));

                    }

                }
            }
        });


        //Likes Feature
        viewHolder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + postId + "/Likes").document(currentUserId).set(likesMap);

                        }else{

                            firebaseFirestore.collection("Posts/" + postId + "/Likes").document(currentUserId).delete();

                        }

                    }
                });
            }
        });

        //Comments
        viewHolder.blogComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Posts posts = post_list.get(viewHolder.getAdapterPosition());
                Users userlist = users_list.get(viewHolder.getAdapterPosition());
                Intent commentIntent = new Intent(context,CommentsActivity.class);
                commentIntent.putExtra("resep_post_id",postId);
                commentIntent.putExtra(CommentsActivity.EXTRA_USERIMAGE,userlist.getImage());
                commentIntent.putExtra(CommentsActivity.EXTRA_USERNAME,userlist.getName());
                commentIntent.putExtra(CommentsActivity.EXTRA_CAPTION,posts.getPostingan());
                context.startActivity(commentIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView descView;
        private TextView judulView;
        private ImageView blogImageView;
        private TextView blogDate;
        private TextView blogUserName;
        private CircleImageView blogUserImage;

        private ImageView blogLikeBtn;
        private TextView blogLikecount;
        private TextView blogCommentCount;
        private ImageView blogComment;
        private ImageView blogShareBtn;
        private TextView blogSharecount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.iv_like);
            blogLikecount = mView.findViewById(R.id.txt_hitung_like);
            blogComment = mView.findViewById(R.id.iv_komen);
            blogCommentCount = mView.findViewById(R.id.txt_hitung_komen);


        }


        public void setDescText(String descText){

            descView = mView.findViewById(R.id.txt_postingan);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri,String thumb){
            blogImageView = mView.findViewById(R.id.iv_post);

            RequestOptions requestOptions = new RequestOptions();

            requestOptions.placeholder(R.drawable.user_male);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
              Glide.with(context).load(thumb)
            ).into(blogImageView);

        }

        public void setTime(String date){

            blogDate = mView.findViewById(R.id.txt_waktu_post);
            blogDate.setText(date);

        }

        public void setUserData(String name, String image){

            blogUserImage = mView.findViewById(R.id.iv_user);
            blogUserName = mView.findViewById(R.id.txt_name_post);
            blogUserName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.user_male);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

        }

        public void updateLikesCount(int count){

            blogLikecount = mView.findViewById(R.id.txt_hitung_like);
            blogLikecount.setText(count+"");

        }
        public void updateCommentCount(int count){
            blogCommentCount = mView.findViewById(R.id.txt_hitung_komen);
            blogCommentCount.setText(count+"");

        }

    }
    public interface OnItemClickCallback{
        void klik(Posts data, Users users);
    }

}


