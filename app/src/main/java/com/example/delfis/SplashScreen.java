package com.example.delfis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000; // Duração da Splash Screen em milissegundos (3 segundos)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen); // Referência ao layout da Splash Screen

        // Temporizador para exibir a Splash Screen por um tempo determinado
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Após o tempo acabar, inicie a MainActivity (tela de login)
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
                finish(); // Finaliza a Splash Screen Activity
            }
        }, SPLASH_TIME_OUT);
    }
}
