package com.example.delfis;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        nav = findViewById(R.id.navbar);

        // Listener para navegação
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                if (item.getItemId() == R.id.lojafooter) {
                    intent = new Intent(Home.this, Store.class);
                } else if (item.getItemId() == R.id.homefooter) {
                    intent = new Intent(Home.this, Home.class);
                } else {
                    intent = new Intent(Home.this, Error.class);
                }

                startActivity(intent);
                return true;
            }
        });
    }
}
