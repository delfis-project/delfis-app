package goldenage.delfis.app.activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;

public class ErrorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        // Botão para voltar à tela anterior
        Button backButton = findViewById(R.id.button);
        backButton.setOnClickListener(v -> {
            finish(); // Fecha a Activity e retorna para a anterior
        });
    }
}
