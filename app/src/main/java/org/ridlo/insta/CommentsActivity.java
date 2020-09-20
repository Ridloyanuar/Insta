package org.ridlo.insta;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.ridlo.insta.data.Comments;
import org.ridlo.insta.data.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentsActivity extends AppCompatActivity {

    private EditText comment_field;
    private TextView comment_post_btn;
    private RecyclerView comment_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String resep_post_id;
    private String current_user_id;
    private List<Comments> commentsList;
    private List<Users> userList;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;

    public static final String EXTRA_CAPTION = "extra_caption";
    public static final String EXTRA_USERIMAGE = "extra_userimage";
    public static final String EXTRA_USERNAME = "extra_username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar komenToolbar = findViewById(R.id.komenToolbar);
        komenToolbar.setTitle("Komentar");
        setSupportActionBar(komenToolbar);

        komenToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        komenToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView desc = findViewById(R.id.txt_caption_comments);
        CircleImageView userimage = findViewById(R.id.iv_avatar_comments);
        TextView username = findViewById(R.id.txt_username_comments);

        String deskripsiResep = getIntent().getStringExtra(EXTRA_CAPTION);
        String userimageResep = getIntent().getStringExtra(EXTRA_USERIMAGE);
        String usernameResep = getIntent().getStringExtra(EXTRA_USERNAME);

        String text2 =  deskripsiResep;
        String gbr2 = userimageResep;
        String text3 =  usernameResep;

        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.user_male);
        Glide.with(CommentsActivity.this).applyDefaultRequestOptions(requestOptions).load(gbr2).into(userimage);

        desc.setText(text2);
        setUserImage(gbr2);
        username.setText(text3);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        resep_post_id = getIntent().getStringExtra("resep_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        commentsList = new ArrayList<>();
        userList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList,userList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        Query firstcomment = firebaseFirestore.collection("Posts/" + resep_post_id + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING);
        firstcomment.addSnapshotListener(CommentsActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String commentId = doc.getDocument().getId();
                            final Comments comments = doc.getDocument().toObject(Comments.class).withId(commentId);
                            String commentsUserId = doc.getDocument().getString("user_id");
                            firebaseFirestore.collection("Users").document(commentsUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Users users = task.getResult().toObject(Users.class);
                                    if (task.isSuccessful()){
                                        commentsList.add(comments);
                                        userList.add(users);
                                    }else{
                                        commentsList.add(0, comments);
                                        userList.add(0, users);
                                    }
                                    commentsRecyclerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }

            }
        });

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = comment_field.getText().toString();

                if (!comment_message.isEmpty()){

                    Map<String , Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment_message);
                    commentsMap.put("user_id",current_user_id);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + resep_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this,"Error Posting Comment : " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }else{

                                comment_field.setText("");

                            }

                        }
                    });

                }


            }
        });
    }

    private void setUserImage(String userImageUri) {
        CircleImageView userimage = findViewById(R.id.iv_avatar_comments);

        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.user_male);
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(userImageUri).into(userimage);
    }
}
