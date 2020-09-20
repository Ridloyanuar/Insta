package org.ridlo.insta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.ridlo.insta.data.Comments;
import org.ridlo.insta.data.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> comentList;
    public List<Users> usersList;
    public Context context;

    public CommentsRecyclerAdapter(List<Comments> comentList, List<Users> usersList) {
        this.comentList = comentList;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item,viewGroup,false);
        context = viewGroup.getContext();

        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        String userName = usersList.get(i).getName();
        String userImage = usersList.get(i).getImage();
        viewHolder.setUserData(userName,userImage);
        String commentMessage = comentList.get(i).getMessage();
        viewHolder.setComment_message(commentMessage);


    }

    @Override
    public int getItemCount() {
        if (comentList != null){
            return comentList.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView comment_message;
        private TextView blogUserName;
        private CircleImageView blogUserImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }
        public void setUserData(String name, String image){

            blogUserImage = mView.findViewById(R.id.comment_circle_image);
            blogUserName = mView.findViewById(R.id.comments_username);
            blogUserName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.user_male);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

        }
    }
}
