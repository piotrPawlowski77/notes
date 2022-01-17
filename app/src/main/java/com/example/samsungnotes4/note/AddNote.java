package com.example.samsungnotes4.note;

import android.os.Bundle;

import com.example.samsungnotes4.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    //zmienne do firebase
    FirebaseFirestore firebaseFirestore;
    //zmienna przechowujaca tytul i tresc notatki
    EditText noteTitle;
    EditText noteContent;
    //progress bar
    ProgressBar progressBar;

    //obiekt Usera
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);

        progressBar = findViewById(R.id.progressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();


        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AddNote.this, "Przycisk save kliknieto", Toast.LENGTH_SHORT).show();
                //extract the string passes through variebles noteContent and noteTitle
                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                //walidacja czy pola sa puste
                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(AddNote.this, "Nie możesz zapisać pustej notatki", Toast.LENGTH_SHORT).show();
                    return;
                }

                //display the progressBar
                progressBar.setVisibility(View.VISIBLE);

                //save data
                //walidacja ok = zapisz notatke do FB
                //znajdz pozycje notatki w kolekcji - "notes" = kolekcja w FS
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document();

                //Obiekt Map = zawiera title i content jako string i odpowiadajace obiekty
                Map<String, Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", nContent);

                //insert the data to document
                //listener = czy notatka jest dodana pomyslnie do FStore czy nie
                documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //przechwyc warunek jesli notatka jest dodana pomyslnie
                        Toast.makeText(AddNote.this, "Notatka dodana do FS", Toast.LENGTH_SHORT).show();

                        //return to main activity
                        onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //przechwyc warunek jesli notatka nie jest dodana pomyslnie - wystapil blad
                        Toast.makeText(AddNote.this, "Błąd nie można dodać notatki do FS", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
        //to mozna wyrzucic (<- w pasku)
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //nadpisuje tu ta metode, zeby wyswietlic menu z przyciskami (close_menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.close_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //przechwyc on click z przycisku (close_menu.xml)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.close){
            onBackPressed();
            Toast.makeText(this, "Notatka nie została zapisana", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}