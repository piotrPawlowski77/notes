package com.example.samsungnotes4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //instancja fbauth
        firebaseAuth = FirebaseAuth.getInstance();

        //Tworze handler = wysle nas do main activity po 2 sek.
        Handler handler = new Handler();

        //klasa Handler ma metode: public final boolean postDelayed(@NonNull Runnable r, long delayMillis) {
        //opoznia okreslona operacje w danym zakresie czasu.
        //
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(Splash.this, "po 2 sekundach", Toast.LENGTH_SHORT).show();

                //sprawdz czy user jest zalogowany
                //real user -> email, password
                //anonymous user -> not have name, email, password
                if(firebaseAuth.getCurrentUser() != null){
                    //ma konto wiec redirect to main activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else
                {
                    //user = null => user nie ma konta w apce lub nie zalogowal sie do apki wczescniej
                    //utworz konto anonymous
                    firebaseAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Splash.this, "Zalogowano jako konto tymczasowe", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Splash.this, "BLAD " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            }
        }, 2000);
    }
}