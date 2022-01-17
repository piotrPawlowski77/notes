package com.example.samsungnotes4.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsungnotes4.MainActivity;
import com.example.samsungnotes4.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    //pola
    EditText regUserName, regUserEmail, regUserPass, regUserConfPass;
    Button createAccount;
    TextView loginActivity;
    ProgressBar progressBar;
    //instancja do FB
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ustaw tytyl paska
        getSupportActionBar().setTitle("Załóż nowe konto");

        //przycisk powrotu back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //powiąż z widokiem
        regUserName = findViewById(R.id.userName);
        regUserEmail = findViewById(R.id.userEmail);
        regUserPass = findViewById(R.id.password);
        regUserConfPass = findViewById(R.id.passwordConfirm);

        //extract the data only when sb click on createAccount button
        createAccount = findViewById(R.id.createAccount);
        loginActivity = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);

        //instancja do FB
        firebaseAuth = FirebaseAuth.getInstance();

        //onclick na button login => loginActivity -> przekieruj do LoginActivity
        loginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        //onclick na createAccount
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extract the data from fields
                String uUserName = regUserName.getText().toString();
                String uUserEmail = regUserEmail.getText().toString();
                String uUserPass = regUserPass.getText().toString();
                String uConfPass = regUserConfPass.getText().toString();

                //walidacja czy puste pola
                if(uUserEmail.isEmpty() || uUserName.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()){
                    Toast.makeText(Register.this, "Wszystkie pola są wymagane!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //czy pola hasel sa rozne
                if(!uUserPass.equals(uConfPass)){
                    regUserConfPass.setError("Podane Hasła są różne!");
                }

                progressBar.setVisibility(View.VISIBLE);

                //Toast.makeText(Register.this, "condition passed", Toast.LENGTH_SHORT).show();
                //merge/link the anonymous account to real account
                //1. create auth credential  with anonumous account
                AuthCredential credential = EmailAuthProvider.getCredential(uUserEmail, uUserPass);
                //2. firebaseAuth.getCurrentUser() = anonymous User
                //firebaseAuth.getCurrentUser().linkWithCredential(); => merge/link the anonymous account to real account
                //addOnSuccesslistener = czy sie udalo zlinkowac czy nie
                firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notatki zsynchronizowane", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class)); //TU ZAKOMENTOWANE BUG


                        //restore the data of particular user in the user profile object.
                        //So we can access that in the main activity for set name and email in nav-draver
                        FirebaseUser usr = firebaseAuth.getCurrentUser();
                        //save the username to FB auth profile object
                        //u can for example .setPhotoUri() to store user images and display it in header view
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uUserName)
                                .build();
                        //use user object to save the ChangeRequest
                        usr.updateProfile(request); //than we can access to main activity

                        //naprawa bug-a = username wyswietlal sie w nav-draver po restarcie apki dopiero
                        //start aktywnosci naprawia problem
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        //finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Błąd z połączeniem", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });

    }

    //przechwycenie przycisku powrotu back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}