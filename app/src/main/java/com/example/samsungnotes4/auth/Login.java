package com.example.samsungnotes4.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.example.samsungnotes4.Splash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText lEmail, lPassword;
    Button loginNow;
    TextView forgetPass, createAcc;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    FirebaseUser user;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //enable the back button and title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Logowanie");

        //powiąż z widokiem
        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);

        spinner = findViewById(R.id.progressBar3);

        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);

        //get instance of FBAuth
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();

        //Display alert dialog to the user when a temp user try to login into existing account -> display message
        showWarningForAnonymousUser();

        //extract the data if loginNow clicked
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = lEmail.getText().toString();
                String mPassword = lPassword.getText().toString();

                //walidacja
                if(mEmail.isEmpty() || mPassword.isEmpty()){
                    Toast.makeText(Login.this, "Wszystkie pola są wymagane!", Toast.LENGTH_SHORT).show();
                    return; //return to the same activity
                }

                //delete notes first

                spinner.setVisibility(View.VISIBLE);

                //if the user is anonymous
                if(firebaseAuth.getCurrentUser().isAnonymous()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    //delete all the notes
                    firebaseStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //note or document deleted successfully
                            Toast.makeText(Login.this, "Wszystkie notatki tymczasowe zostały usunięte", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //delete anonymous user
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Login.this, "Użytkownik tymczasowy usunięty", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                    });

                    //jesli te 2 operacje powyzej sie powiodly, to pozwol zalogowac
                    //uzytkownika uzywajac email and password
                    //i potem user uzyskuje dostep do swoich notatek w main activity


                }

                //use email and pass to signin the user
                firebaseAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Zalogowano pomyślnie!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Błąd w logowaniu! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    //Display alert dialog to the user when a temp user try to login into existing account -> display message
    private void showWarningForAnonymousUser(){
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Jesteś pewien?")
                .setMessage("Zalogowanie na istniejące konto spowoduje usunięcie wszystkich tymczasowych notatek. Stwórz nowe konto by je zapisać!")
                .setPositiveButton("Zapisz notatki", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //wyslij usera do register activity
                        //zeby mogl zalozyc konto i zapisac notatki
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();

                    }
                }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       //do nothing

                    }
                });

        //wyswietl alert uzytkownikowi
        warning.show();
    }

    //przechwycenie przycisku powrotu back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}