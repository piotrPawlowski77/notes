package com.example.samsungnotes4.note;

import android.content.Intent;
import android.os.Bundle;

import com.example.samsungnotes4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class NoteDetails extends AppCompatActivity {

    //private AppBarConfiguration appBarConfiguration;
    //private ActivityNoteDetailsBinding binding;
    //globalna zmienna typu Intent
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_note_details);

        //setSupportActionBar(binding.toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //dodanie przycisku powrotu do main activity
        //aby zadzialalo to trzeba dodac parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //create instance of Intent class - przeniesiona na poczatek klasy zeby nie tworzyc zmiennej typu final
        data = getIntent();

        //mozliwosc scroll-u notatki
        TextView content = findViewById(R.id.noteDetailsContent);
        TextView title = findViewById(R.id.noteDetailsTitle);
        content.setMovementMethod(new ScrollingMovementMethod());

        //get the data (from intent) passed from main activity
        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));
        content.setBackgroundColor(getResources().getColor(data.getIntExtra("colorCode", 0), null));

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note_details);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //przycisk FAB bedzie sluzyl jako edycje bierzacej notatki
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //jesli kliknie -> przejdz do EditNote activity
                Intent i = new Intent(view.getContext(), EditNote.class);
                //przekaz dane content i title do activity
                i.putExtra("title", data.getStringExtra("title"));
                i.putExtra("content", data.getStringExtra("content"));
                i.putExtra("noteId", data.getStringExtra("noteId"));
                startActivity(i);
            }
        });

    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note_details);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //jesli przycisk back kliknieto
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}