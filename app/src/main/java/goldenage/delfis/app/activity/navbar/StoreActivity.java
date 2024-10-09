package goldenage.delfis.app.activity.navbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.sell.CoinsActivity;
import goldenage.delfis.app.activity.sell.PowerupsActivity;
import goldenage.delfis.app.activity.sell.PremiumActivity;
import goldenage.delfis.app.activity.sell.ThemesActivity;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.ActivityUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoreActivity extends AppCompatActivity {
    private User user;
    private BottomNavigationView nav;
    private ImageView btTemas, btPowerups, btMoedas, btPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.bottom_menu);
        user = (User) getIntent().getSerializableExtra("user");
        btTemas = findViewById(R.id.btTemas);
        btPowerups = findViewById(R.id.btPowerups);
        btMoedas = findViewById(R.id.btMoedas);
        btPremium = findViewById(R.id.btPremium);

        nav.setSelectedItemId(R.id.lojaMenu);
        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(StoreActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            return true;
        });

        btTemas.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, ThemesActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btPowerups.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, PowerupsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btMoedas.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, CoinsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btPremium.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, PremiumActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }
}
