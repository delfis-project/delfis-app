package com.example.delfis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Encontre o botão "loja" pelo ID
        Button lojaButton = findViewById(R.id.button2);

        // Adicione um OnClickListener ao botão "loja"
        lojaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para iniciar a atividade Store
                Intent intent = new Intent(Home.this, Store.class);
                startActivity(intent);
            }
        });
    }
}
