package com.example.delfis;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Store extends AppCompatActivity {
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store); // Certifique-se de que este é o nome correto do layout

        // Configuração do BottomNavigationView
        nav = findViewById(R.id.navbar);
        nav.setSelectedItemId(R.id.lojafooter); // Seleciona o item "Loja"

        // Listener para navegação entre itens do BottomNavigationView
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                if (item.getItemId() == R.id.lojafooter) {
                    // Já estamos na loja, não faça nada
                    return true;
                } else if (item.getItemId() == R.id.homefooter) {
                    intent = new Intent(Store.this, Home.class);
                } else {
                    // Abrir a classe Error para os outros itens
                    intent = new Intent(Store.this, Error.class);
                }

                startActivity(intent); // Inicia a nova atividade
                return true;
            }
        });
    }
}
