package org.ridlo.insta.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.ridlo.insta.PostsAdapter;
import org.ridlo.insta.data.Posts;
import org.ridlo.insta.R;
import org.ridlo.insta.data.Users;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BerandaFragment extends Fragment {

    private RecyclerView rv_posting;
    private List<Posts> post_list;
    private List<Users> user_list;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;
    private PostsAdapter postsAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public BerandaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);
        post_list = new ArrayList<>();
        user_list = new ArrayList<>();
        rv_posting = view.findViewById(R.id.resep_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            postsAdapter = new PostsAdapter(post_list,user_list);
            rv_posting.setLayoutManager(new LinearLayoutManager(container.getContext()));
            rv_posting.setAdapter(postsAdapter);

        }



        if (firebaseAuth.getCurrentUser() != null){

            firebaseFirestore = FirebaseFirestore.getInstance();

            rv_posting.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    boolean reacheBottom = !recyclerView.canScrollVertically(1);
                    if (reacheBottom){
                        loadPost();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.ASCENDING).limit(30);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                post_list.clear();
                                user_list.clear();
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
                                                Users users = task.getResult().toObject(Users.class);
                                                if (isFirstPageFirstLoad) {

                                                    user_list.add(users);
                                                    post_list.add(posts);
                                                } else {
                                                    user_list.add(0, users);
                                                    post_list.add(0, posts);
                                                }

                                                postsAdapter.notifyDataSetChanged();
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
        }


        return view;
    }

    public void loadPost(){
        if (firebaseAuth.getCurrentUser() != null){
            Query nextQuery = firebaseFirestore.collection("Posts").
                    orderBy("timestamp", Query.Direction.ASCENDING)
                    .startAfter(lastVisible).limit(30);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String postId = doc.getDocument().getId();
                                final Posts posts = doc.getDocument().toObject(Posts.class).withId(postId);
                                String userId = doc.getDocument().getString("user_id");

                                firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            Users users = task.getResult().toObject(Users.class);

                                            user_list.add(users);
                                            post_list.add(posts);


                                            postsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }

        else{
            Query nextQuery = firebaseFirestore.collection("Posts").
                    orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible).limit(30);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String postId = doc.getDocument().getId();
                                final Posts posts = doc.getDocument().toObject(Posts.class).withId(postId);
                                String userId = doc.getDocument().getString("user_id");

                                firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            Users users = task.getResult().toObject(Users.class);

                                            user_list.add(users);
                                            post_list.add(posts);


                                            postsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

}