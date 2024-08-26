package com.example.delfis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Store extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store); // Certifique-se de que este é o nome correto do layout

        // Encontre o botão "home" e configure o clique
        Button homeButton = findViewById(R.id.button3);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar a atividade Home
                Intent intent = new Intent(Store.this, Home.class);
                startActivity(intent);
            }
        });
    }
}
