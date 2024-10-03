package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import goldenage.delfis.app.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen); // Referência ao layout da Splash Screen

        // Temporizador para exibir a Splash Screen por um tempo determinado
        // Duração da Splash Screen em milissegundos (3 segundos)
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(() -> {
            // Após o tempo acabar, inicie a MainActivity (tela de login)
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finaliza a Splash Screen Activity
        }, SPLASH_TIME_OUT);
    }
}
