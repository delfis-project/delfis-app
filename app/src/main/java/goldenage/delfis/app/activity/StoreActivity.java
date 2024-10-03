package goldenage.delfis.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.User;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StoreActivity extends AppCompatActivity {
    User user;
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store); // Certifique-se de que este é o nome correto do layout

        // Configuração do BottomNavigationView
        nav = findViewById(R.id.navbar);
        nav.setSelectedItemId(R.id.lojafooter); // Seleciona o item "Loja"

        user = (User) getIntent().getSerializableExtra("user");

        // Listener para navegação entre itens do BottomNavigationView
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                if (item.getItemId() == R.id.lojafooter) {
                    // Já estamos na loja, não faça nada
                    return true;
                } else if (item.getItemId() == R.id.homefooter) {
                    intent = new Intent(StoreActivity.this, HomeActivity.class);
                } else {
                    // Abrir a classe Error para os outros itens
                    intent = new Intent(StoreActivity.this, ErrorActivity.class);
                }

                if (user != null)
                    intent.putExtra("user", user);

                startActivity(intent); // Inicia a nova atividade
                return true;
            }
        });
    }
}
