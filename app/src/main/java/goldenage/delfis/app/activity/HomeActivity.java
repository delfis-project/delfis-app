package goldenage.delfis.app.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nav = findViewById(R.id.navbar);

        // Listener para navegação
        nav.setOnItemSelectedListener(item -> {
            Intent intent;

            if (item.getItemId() == R.id.lojafooter) {
                intent = new Intent(HomeActivity.this, StoreActivity.class);
            } else if (item.getItemId() == R.id.homefooter) {
                intent = new Intent(HomeActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(HomeActivity.this, ErrorActivity.class);
            }

            startActivity(intent);
            return true;
        });
    }
}
