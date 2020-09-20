package org.ridlo.insta.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.ridlo.insta.DashboardLoginActivity;
import org.ridlo.insta.MainActivity;
import org.ridlo.insta.MyPostsAdapter;
import org.ridlo.insta.PostsAdapter;
import org.ridlo.insta.R;
import org.ridlo.insta.SetupProfilActivity;
import org.ridlo.insta.data.Posts;
import org.ridlo.insta.data.Users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private TextView txt_namaProfile, txt_email_profile, txt_btn_keluar, txt_btn_edit;
    private FirebaseAuth mFirebaseAuth;
    private CircleImageView iv_avatar;
    private StorageReference storageReference;
    private Uri mainImageUri = null;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private List<Posts> post_list;
    private RecyclerView rv_galeri;
    private MyPostsAdapter myPostsAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            user_id = mFirebaseAuth.getCurrentUser().getUid();
            post_list = new ArrayList<>();
            firebaseFirestore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            txt_btn_keluar = v.findViewById(R.id.txt_btn_keluar);
            txt_btn_edit = v.findViewById(R.id.txt_btn_edit);
            rv_galeri = v.findViewById(R.id.rv_galeri);

            iv_avatar = v.findViewById(R.id.iv_avatar);
            txt_namaProfile = v.findViewById(R.id.tv_nama_profile);
            txt_email_profile = v.findViewById(R.id.tv_email_profile);


            iv_avatar.setVisibility(View.INVISIBLE);
            txt_namaProfile.setVisibility(View.INVISIBLE);
            txt_email_profile.setVisibility(View.INVISIBLE);

            txt_btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SetupProfilActivity.class));
                }
            });
            txt_btn_keluar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFirebaseAuth.signOut();
                    Intent mainIntent = new Intent(getActivity(), DashboardLoginActivity.class);
                    startActivity(mainIntent);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                    startActivity(mainIntent);
                }
            });


            firebaseFirestore.collection("Users").document(user_id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (isAdded()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String name = documentSnapshot.getString("name");
                                String image = documentSnapshot.getString("image");
                                assert user != null;
                                String email = user.getEmail();
                                mainImageUri = Uri.parse(image);

                                txt_namaProfile.setText(name);
                                txt_email_profile.setText(email);
                                RequestOptions placeholderRequest = new RequestOptions();
                                placeholderRequest.placeholder(R.drawable.user_male);
                                Glide.with(getContext()).setDefaultRequestOptions(placeholderRequest).load(image).into(iv_avatar);

                                iv_avatar.setVisibility(View.VISIBLE);
                                txt_namaProfile.setVisibility(View.VISIBLE);
                                txt_email_profile.setVisibility(View.VISIBLE);
                            }
                        }
                    });

            myPostsAdapter = new MyPostsAdapter(post_list);
            rv_galeri.setLayoutManager(new GridLayoutManager(getContext(), 3));
            rv_galeri.setAdapter(myPostsAdapter);

            Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id",user_id).orderBy("timestamp", Query.Direction.ASCENDING).limit(30);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                post_list.clear();
                            }

                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    String postId = documentChange.getDocument().getId();
                                    final Posts posts = documentChange.getDocument().toObject(Posts.class).withId(postId);

                                    String userId = documentChange.getDocument().getString("user_id");
                                    firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (isFirstPageFirstLoad) {
                                                    post_list.add(posts);
                                                } else {
                                                    post_list.add(0, posts);
                                                }

                                                myPostsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                                isFirstPageFirstLoad = false;
                            }
                        }
                    }
                }
            });


        }else {
            onStart();
        }

        return v;
    }


}
