package com.example.samsungnotes4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsungnotes4.auth.Login;
import com.example.samsungnotes4.auth.Register;
import com.example.samsungnotes4.model.Adapter;
import com.example.samsungnotes4.model.Note;
import com.example.samsungnotes4.note.AddNote;
import com.example.samsungnotes4.note.EditNote;
import com.example.samsungnotes4.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //zmienne do DrawerLayout
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    //RecyclerView
    RecyclerView noteList;

    //instancja Adaptera
    Adapter adapter;

    //
    FirebaseFirestore firebaseFirestore;
    //<model class, view holder (klasa na koncu klasy main activity)>
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;

    //15
    FirebaseUser user;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //pobranie instancji
        firebaseFirestore = FirebaseFirestore.getInstance();

        //15
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        //query the database = stare zapytanie
        //Query query = firebaseFirestore.collection("notes").orderBy("title", Query.Direction.DESCENDING);

        //NOWE zapytanie do kolekcji notes -> document UID -> kolekcja myNotes -> notatki konkretnego uzytkownika
        //trzeba napisac query do kolekcji: myNotes
        //user.getUid() - zwraca UID z Firebase. W UID dalej mamy kolekcje myNotes
        Query query = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);


        //execute the query
        //<Klasa notatki - model>
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        //create the object of note adapter (allNotes)
        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder,final int i, @NonNull final Note note) {
                //bind the data

                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());

                //use holder to get CardView and save the background color
                //holder.myNoteCardView.setBackgroundColor(getToNoteRandomColor());
                final int colorCode = getToNoteRandomColor();
                noteViewHolder.myNoteCardView.setBackgroundColor(noteViewHolder.view.getResources().getColor(colorCode, null));

                //get the firestore document ID = potrzebne do edycji notatki po ID
                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                //display message when user click the item in recyclerview
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(v.getContext(), "Item in recyclerview is clicked", Toast.LENGTH_SHORT).show();

                        //otworz nowa aktywnosc = tworzenie nowego intent i przez niego przeslemy dane do nowej activity
                        //Intent(kontekst skad chce uruchomic aktywnosc, destination)
                        Intent intent = new Intent(v.getContext(), NoteDetails.class);

                        //pass the data (note title, note content and color code) from mainactivity to NoteDetails activity
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("content", note.getContent());
                        intent.putExtra("colorCode", colorCode);
                        intent.putExtra("noteId", docId);
                        //start activity
                        v.getContext().startActivity(intent);

                    }
                });

                //ImageView do popup menu notatki
                //zeby dostac sie do XML trzeba uzyc noteViewHolder.zmienna_stworzona_w_viewHolder
                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);

                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        //uzyskuje id konkretnej notatki
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                        //Toast.makeText(MainActivity.this, "Chodzi", Toast.LENGTH_SHORT).show();
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                        //set gravity on popup menu = zeby sie nie nakladalo na notatke i wyswietlalo za notatka
                        popupMenu.setGravity(Gravity.END);


                        //dodaj pozycje menu
                        popupMenu.getMenu().add("Edytuj notatkę").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //Toast.makeText(MainActivity.this, "Opcja", Toast.LENGTH_SHORT).show();
                                //nie wydobywam danych z Intent-u tylko z klasy Note przekazanej jako parametr metody onBindViewHolder(...)
                                Intent i = new Intent(v.getContext(), EditNote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteId", docId);
                                startActivity(i);

                                return false;
                            }
                        });

                        //kolejna pozycja menu
                        popupMenu.getMenu().add("Usuń notatkę").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //Toast.makeText(MainActivity.this, "Opcja1", Toast.LENGTH_SHORT).show();
                                //potrzebna referencja do konkretnej notatki w kolekcji -> po id notatki -> docId

                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                //eventListener -> jesli sie powiedze lub nie usuniecie notatki
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //notatka usunieta z recyclerView
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Wystąpił błąd podczas usunięcia notatki!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        //popup menu show
                        popupMenu.show();

                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //create new view holder
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
                return new NoteViewHolder(view);
            }
        };

        //przypisanie zasobow do zbiorczego (glownego) RecyclerView zawierajacego wszystkie notatkisss
        noteList = findViewById(R.id.notelist);

        //ustawienie DrawerLayout
        drawerLayout = findViewById(R.id.drawerId);
        navigationView = findViewById(R.id.navView);
        //dodanie listenera do navigationView
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);


        //ustawienie sluchacza do Drawer-a
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        //synchronizacja stanu - informuje action bar ze nav drawer jest otwarty lub zamkniety obecnie
        toggle.syncState();

       //...

        //set the layout manager (number of grid to display, orientation)
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //set the adapter to noteList
        noteList.setAdapter(noteAdapter);

        //przed fab tworze nowy obiekt View (potrzeba do wyswietlenia username i email w nav draver)
        //wyzej mamy navigationView (znajdowany po R.id.navView) -> dostane sie do headerView getHeaderView(position = 0);
        //0 = mamy tylko 1 nagłówek header ale moze byc ich wiecej - 1, 2 .. itd
        View headerView = navigationView.getHeaderView(0);

        //teraz dostane sie do TextView (name i email)
        TextView userName = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if(user.isAnonymous()){
            userEmail.setVisibility(View.GONE);
            userName.setText("Użytkownik tymczasowy");
        } else {
            userEmail.setText(user.getEmail()); //user = firebase user
            userName.setText(user.getDisplayName());
        }




        //handle the FAB button onclick (add note)
        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send user to AddNote activity
                startActivity(new Intent(view.getContext(), AddNote.class));
                //overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                //finish();
            }
        });


    }

    //obsluga przyciskow lewego paska Drawera
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //zwin draweer po kliknieciu w jakas opcje
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()){

            //jesli user kliknal w przycisk dodaj notatke - uruchom aktywnosc AddNote
            case R.id.addNote:
                startActivity(new Intent(this, AddNote.class));
                //overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;

            case R.id.sync:
                //Tylko anonymmous MA DOSTEP DO PRZYCISKU SYNC NOTE. Zalogowany tego nie potrzebuje bo ma juz konto
                if(user.isAnonymous()){
                    startActivity(new Intent(this, Login.class));
                    //overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                } else {
                    //message
                    Toast.makeText(this, "Jesteś połączony ze swoim kontem", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.logout:
                //spr czy user zalogowany kontem tymczasowym lub real kontem
                //real konto => sign out is not a problem
                //kontem tymczasowym => zapytaj usera czy napewno chce sie wylogowac -> spowoduje to utrate danych
                checkUser();
                break;

            default:
                Toast.makeText(this, "Todo", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void checkUser() {

        // czy user zalogowany kontem tymczasowym
        if(user.isAnonymous()){
            displayAlert();
        }else{
            //user zalogowany
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Splash.class));
            //overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            finish();
        }

    }

    //w alertDialog mamy: title, content, 2 button Y/N
    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Jesteś pewien?")
                .setMessage("Jesteś zalogowany jako użytkownik tymczasowy. Notatki nie zostaną zapisane!")
                .setPositiveButton("Zarejestruj sie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //wyslij usera do register activity
                        //zeby mogl zalozyc konto i zapisac notatki
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();

                    }
                }).setNegativeButton("Wyloguj się", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Todo: usun wszystkie notatki stworzone przez tymczasowego uzytkowniuka

                        //go to collection for current user and identify user by user id

                        //Todo: usun uzytkowniuka tymczasowego

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //deleted user successful then redirect it to splash screen
                                startActivity(new Intent(getApplicationContext(), Splash.class));
                                //overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                                finish();
                            }
                        });

                    }
                });

        //wyswietl alert uzytkownikowi
        warning.show();

    }

    //ustawia opcje menu zeby wyswietlic w aktywnosci
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //wyswietl menu w aktywnosci
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //ktory przycisk zostal klikniety w menu
        if(item.getItemId() == R.id.settings){
            Toast.makeText(this, "Ustawienia menu kliknieto", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        //zmienna przechowujaca dane notatki
        TextView noteTitle;
        TextView noteContent;

        //uchwyt do layoutu notatki (zmiana kolorow)
        CardView myNoteCardView;

        //zmienna widoku (uchwyt na klikniecie w recyclerview). Jesli kliknie to pokaze szczegoly konkretnej notatki
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            //przydziel zasoby XML
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);

            myNoteCardView = itemView.findViewById(R.id.noteCard);

            view = itemView;

        }
    }


    //zawsze gdy zamykamy lub restartujemy aplikacje albo jesli wracamy z innej aktywnosci do main activity
    //musze zrobic listenera, ktory slucha zmiany w Fire Store database w kolekcji notatek.
    //Zebby to zrobic trzeba @Override onStart() i onStop() metod w MainActivity.
    //Zawsze gdy aktywnosc sie wlacza -> sprawdzamy czy zaszly jakies zmiany w bazie danych
    @Override
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }

    //Zawsze gdy aktywnosc sie wylacza (zamykamy apke)-> NIE sprawdzamy czy zaszly jakies zmiany w bazie danych
    @Override
    protected void onStop(){
        super.onStop();
        if(noteAdapter != null){
            noteAdapter.stopListening();
        }
    }

    private int getToNoteRandomColor() {

        //list of integer that will hold the color code
        List<Integer> listOfColorCode = new ArrayList<>();

        //insert the color to list
        listOfColorCode.add(R.color.yellow);
        listOfColorCode.add(R.color.lightGreen);
        listOfColorCode.add(R.color.pink);
        listOfColorCode.add(R.color.lightPurple);
        listOfColorCode.add(R.color.skyblue);
        listOfColorCode.add(R.color.gray);
        listOfColorCode.add(R.color.red);
        listOfColorCode.add(R.color.blue);
        listOfColorCode.add(R.color.greenlight);
        listOfColorCode.add(R.color.notgreen);

        //use the Random class
        Random randomNoteColor = new Random();
        int number =  randomNoteColor.nextInt(listOfColorCode.size());

        //return the color
        return listOfColorCode.get(number);

    }

}