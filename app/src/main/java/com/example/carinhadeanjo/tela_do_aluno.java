package com.example.carinhadeanjo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class tela_do_aluno extends AppCompatActivity {
    ArrayList<String> feed = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private FirebaseAuth mAuth;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference myRef = database.child("P2");


    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_do_aluno);




        final TextView a1 = (TextView) findViewById(R.id.aluno);
        a1.setText(tela_de_carregamento.nnomeAluno);

        final TextView a2 = (TextView) findViewById(R.id.turma);
        a2.setText(tela_de_carregamento.tturma+" - "+tela_de_carregamento.nnomePai);

        myRef.child("Creche I").child("feed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                   // String key= snapshot.getKey();
                    String value=snapshot.getValue().toString();
                   // feed.add(key);
                    feed.add(value);
                    listView = findViewById(R.id.listView);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setShape(GradientDrawable.RECTANGLE);
                    gd.setStroke(5, Color.argb(100, 0,0,0)); // border width and color
                    //gd.setCornerRadius(80.50f);
                    gd.setCornerRadius(150);
                    listView.setBackground(gd);
                    listView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, feed);

    }

    public void lista_um_click(View view){
        Intent intent = new Intent(getBaseContext(), lista_um.class);
        startActivity(intent);
    }


        }



