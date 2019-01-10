package com.smacon.fish2marine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GoogleLoginProfileActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textName, textEmail;
    Button logout;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_googleprofile);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Auth.GoogleSignInApi.signOut(mGoogleApiClient);

               // FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent setupIntent = new Intent(getBaseContext(), LoginActivity.class);
                Toast.makeText(getBaseContext(), "Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setupIntent);
                finish();


                GoogleSignInClient mGoogleSignInClient;
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
                mGoogleSignInClient.signOut().addOnCompleteListener(GoogleLoginProfileActivity.this,
                        new OnCompleteListener<Void>() {  //signout Google
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseAuth.getInstance().signOut(); //signout firebase
                                Intent setupIntent = new Intent(getBaseContext(), LoginActivity.class);
                                Toast.makeText(getBaseContext(), "Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                                setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(setupIntent);
                                finish();
                            }
                        });


               /* mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                updateUI(null);
                            }
                        });
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));*/

            }
        });

        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);

        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail()+" "+user.getUid()+" "+user.getPhoneNumber());
      //  textEmail.setText(user.);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is not logged in
        //opening the login activity
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

