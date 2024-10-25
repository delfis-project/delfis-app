package goldenage.delfis.app.ui.activity.navbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;
import goldenage.delfis.app.ui.activity.sell.SellCoinsActivity;
import goldenage.delfis.app.ui.activity.sell.SellPowerupsActivity;
import goldenage.delfis.app.ui.activity.sell.SellPremiumActivity;
import goldenage.delfis.app.ui.activity.sell.SellThemesActivity;
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
        btTemas = findViewById(R.id.btTemas);
        btPowerups = findViewById(R.id.btPowerups);
        btMoedas = findViewById(R.id.btMoedas);
        btPremium = findViewById(R.id.btPremium);

        renderizarInfo();

        nav.setSelectedItemId(R.id.lojaMenu);
        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(StoreActivity.this, item);
            intent.putExtra("user", user);
            startActivity(intent);
            
            return true;
        });

        btTemas.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, SellThemesActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btPowerups.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, SellPowerupsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btMoedas.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, SellCoinsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btPremium.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, SellPremiumActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderizarInfo();
    }

    private void renderizarInfo() {
        user = (User) getIntent().getSerializableExtra("user");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StoreActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
