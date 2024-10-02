package goldenage.delfis.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

import goldenage.delfis.app.R;

public class ConfigActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);


        nav = findViewById(R.id.navbar);

        // Listener para navegação
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                if (item.getItemId() == R.id.lojafooter) {
                    intent = new Intent(ConfigActivity.this, StoreActivity.class);
                } else if (item.getItemId() == R.id.homefooter) {
                    intent = new Intent(ConfigActivity.this, HomeActivity.class);
                } else if (item.getItemId() == R.id.configfooter) {
                    intent = new Intent(ConfigActivity.this, ConfigActivity.class);
                } else {
                    intent = new Intent(ConfigActivity.this, ErrorActivity.class);
                }

                startActivity(intent);
                return true;
            }
        });
    }
}