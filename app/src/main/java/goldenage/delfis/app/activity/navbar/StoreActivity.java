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
    User user;
    BottomNavigationView nav;
    ImageView btTemas, btPowerups, btMoedas, btPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        nav = findViewById(R.id.navbar);
        user = (User) getIntent().getSerializableExtra("user");
        nav.setSelectedItemId(R.id.lojafooter);
        btTemas = findViewById(R.id.btTemas);
        btPowerups = findViewById(R.id.btPowerups);
        btMoedas = findViewById(R.id.btMoedas);
        btPremium = findViewById(R.id.btPremium);

        btMoedas.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, CoinsActivity.class);
            if (user != null)
                intent.putExtra("user", user);
            startActivity(intent);
        });

        btPremium.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, PremiumActivity.class);
            if (user != null)
                intent.putExtra("user", user);
            startActivity(intent);
        });

        btTemas.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, ThemesActivity.class);
            if (user != null)
                intent.putExtra("user", user);
            startActivity(intent);
        });

        btPowerups.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, PowerupsActivity.class);
            if (user != null)
                intent.putExtra("user", user);
            startActivity(intent);
        });

        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(StoreActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            return true;
        });
    }
}
