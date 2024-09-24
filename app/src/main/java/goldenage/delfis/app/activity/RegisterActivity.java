package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import goldenage.delfis.app.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Encontre o Spinner pelo ID
        Spinner spinnerEstados = findViewById(R.id.spinnerEstados);

        // Lista de estados do Brasil
        String[] estados = {"Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal",
                "Espírito Santo", "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul",
                "Minas Gerais", "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí",
                "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul",
                "Rondônia", "Roraima", "Santa Catarina", "São Paulo", "Sergipe", "Tocantins"};

        // Defina o adapter para o Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstados.setAdapter(adapter);

        // Defina o placeholder "##"
        spinnerEstados.setPrompt("##");

        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String estadoSelecionado = estados[position];
                Toast.makeText(getApplicationContext(), "Estado: " + estadoSelecionado, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Caso nenhum item seja selecionado
            }
        });

        // Encontre o botão "CRIAR" pelo ID
        Button criarButton = findViewById(R.id.entrar);

        // Adicione um OnClickListener ao botão
        criarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para iniciar a atividade Login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
