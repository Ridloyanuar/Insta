package org.ridlo.insta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText et_emailLogin, et_passLogin;
    private TextView txt_lupaPass, txt_buatAkun;
    private Button btn_masukLogin;
    private FirebaseAuth mFirebaseAuth;
    Toolbar toolbar;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser  = mFirebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_emailLogin = findViewById(R.id.editText_emaill);
        et_passLogin = findViewById(R.id.editText_passLogin);
        btn_masukLogin = findViewById(R.id.btn_masuk);
        txt_lupaPass = findViewById(R.id.txt_lupaPassword);
        txt_buatAkun = findViewById(R.id.txt_buatAkun);
        mFirebaseAuth = FirebaseAuth.getInstance();


        //view
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Masuk");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_buatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerIntent();
            }
        });

        txt_lupaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetIntent();
            }
        });
        
        

        btn_masukLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailLogin = et_emailLogin.getText().toString();
                String passLogin = et_passLogin.getText().toString();

                if (!TextUtils.isEmpty(emailLogin) && !TextUtils.isEmpty(passLogin)){
                    mFirebaseAuth.signInWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mainIntent();
                            }else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error : "
                                        + errorMessage,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

    }


    private void forgetIntent() {
//        startActivity(new Intent(LoginActivity.this, LupaPasswordActivity.class));
    }

    private void registerIntent() {
        startActivity(new Intent(LoginActivity.this, DashboardLoginActivity.class));
        finish();
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

 
}
