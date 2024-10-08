package com.example.delfis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Encontre o botão "CRIAR" pelo ID
        Button criarButton = findViewById(R.id.entrar);

        // Adicione um OnClickListener ao botão
        criarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para iniciar a atividade Login
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
