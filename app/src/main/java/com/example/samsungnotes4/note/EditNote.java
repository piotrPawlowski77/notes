package com.example.samsungnotes4.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.samsungnotes4.MainActivity;
import com.example.samsungnotes4.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {

    //globalna zmienna typu Intent
    Intent data;

    //zmienne layoutu
    EditText editNoteTitle;
    EditText editNoteContent;

    //instancja do FB
    FirebaseFirestore firebaseFirestore;

    //obiekt Usera
    FirebaseUser user;

    //progressBar2
    //ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //pamietaj ustaw toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //polacz z baza FB
        firebaseFirestore = firebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser(); //get the currently logged user

        //progressBar2
        //progressBar = findViewById(R.id.progressBar2);


        //zmienna data bedzie zawierac: tytul, tresc i Id notatki
        data = getIntent();

        editNoteTitle = findViewById(R.id.editNoteTitle);
        editNoteContent = findViewById(R.id.editNoteContent);

        //odzyskaj dane
        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        //przypisz do zmiennych layoutu
        editNoteTitle.setText(noteTitle);
        editNoteContent.setText(noteContent);

        //obsluga zapisu do FB
        FloatingActionButton fab = findViewById(R.id.saveEditedNote);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //wstaw dane do FB
                String nTitle = editNoteTitle.getText().toString();
                String nContent = editNoteContent.getText().toString();

                //walidacja czy pola sa puste
                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(EditNote.this, "Nie możesz zapisać pustej notatki", Toast.LENGTH_SHORT).show();
                    return;
                }

                //display the progressBar
                //progressBar.setVisibility(View.VISIBLE);

                //save data
                //walidacja ok = zapisz notatke do FB
                //znajdz pozycje notatki w kolekcji - "notes" = kolekcja w FS
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));

                //Obiekt Map = zawiera title i content jako string i odpowiadajace obiekty
                Map<String, Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", nContent);

                //insert the data to document
                //listener = czy notatka jest dodana pomyslnie do FStore czy nie
                documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //przechwyc warunek jesli notatka jest dodana pomyslnie
                        Toast.makeText(EditNote.this, "Edytowana notatka dodana do FS", Toast.LENGTH_SHORT).show();

                        //automatycznie return to main activity (startActivity)
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //przechwyc warunek jesli notatka nie jest dodana pomyslnie - wystapil blad
                        Toast.makeText(EditNote.this, "Błąd nie można dodać notatki do FS", Toast.LENGTH_SHORT).show();
                        //progressBar.setVisibility(View.VISIBLE);
                    }
                });


            }
        });


    }
}