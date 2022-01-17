package com.example.samsungnotes4.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samsungnotes4.note.NoteDetails;
import com.example.samsungnotes4.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    //2 listy zawierajace przykladowe notatki z tytulami
    List<String> titles;
    List<String> noteContent;

    //konsttruktor Adaptera = przekaze dane do main activity
    //jako argument: tresc notatki oraz tytul notatki
    public Adapter(List<String> title, List<String> content){

        //przypisanie danych
        this.titles = title;
        this.noteContent = content;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //metoda onCreateViewHolder jest uzyta do stworzenia widoku dla recycler view gdzie wyswietlam dane ( w xml R.layout.note_view)

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Nowy ViewHolder będzie używany do wyświetlania elementów adaptera za pomocą onBindViewHolder
        //Todo
        //bind the data received from mainactivity when create adapter object
        //extract the title/content from the List<> and assign that to the view layout
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(noteContent.get(position));

        //use holder to get CardView and save the background color
        //holder.myNoteCardView.setBackgroundColor(getToNoteRandomColor());
        final int colorCode = getToNoteRandomColor();
        holder.myNoteCardView.setBackgroundColor(holder.view.getResources().getColor(colorCode, null));

        //display message when user click the item in recyclerview
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Item in recyclerview is clicked", Toast.LENGTH_SHORT).show();

                //otworz nowa aktywnosc = tworzenie nowego intent i przez niego przeslemy dane do nowej activity
                //Intent(kontekst skad chce uruchomic aktywnosc, destination)
                Intent intent = new Intent(v.getContext(), NoteDetails.class);

                //pass the data (note title, note content and color code) from mainactivity to NoteDetails activity
                intent.putExtra("title", titles.get(position));
                intent.putExtra("content", noteContent.get(position));
                intent.putExtra("colorCode", colorCode);
                //start activity
                v.getContext().startActivity(intent);

            }
        });


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

    @Override
    public int getItemCount() {

        //zwraca ilosc elementow ktore chce wyswietlic w RecyclerView

        //return 0;
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //zmienna przechowujaca dane notatki
        TextView noteTitle;
        TextView noteContent;

        //uchwyt do layoutu notatki (zmiana kolorow)
        CardView myNoteCardView;

        //zmienna widoku (uchwyt na klikniecie w recyclerview). Jesli kliknie to pokaze szczegoly konkretnej notatki
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //przydziel zasoby XML
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);

            myNoteCardView = itemView.findViewById(R.id.noteCard);

            view = itemView;
        }
    }
}
