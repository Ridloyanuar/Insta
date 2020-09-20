package org.ridlo.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.ridlo.insta.fragment.BerandaFragment;
import org.ridlo.insta.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ProfileFragment akunFragment;
    private BerandaFragment berandaFragment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private  String current_user_id;
    private BottomNavigationView mainBottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBottomNav = findViewById(R.id.bottomNavigationView);

        berandaFragment = new BerandaFragment();
        akunFragment = new ProfileFragment();
        replaceFragment(berandaFragment);


        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_home:
                        replaceFragment(berandaFragment);
                        return true;

                    case R.id.nav_tambah:
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.nav_akun:
                        replaceFragment(akunFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });


//
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mainBottomNav.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        mainBottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }
}