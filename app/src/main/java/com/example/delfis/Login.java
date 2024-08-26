package com.example.delfis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Encontre o botão "ENTRAR" pelo ID
        Button entrarButton = findViewById(R.id.entrar);

        // Adicione um OnClickListener ao botão
        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para iniciar a atividade Home
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
            }
        });

        // Encontre o TextView "Criar Conta" pelo ID
        TextView criarContaTextView = findViewById(R.id.criarconta);

        // Adicione um OnClickListener ao TextView "Criar Conta"
        criarContaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para iniciar a atividade CadastroActivity
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
