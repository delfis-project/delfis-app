package goldenage.delfis.app.activity.navbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.User;
import goldenage.delfis.app.util.ActivityUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoreActivity extends AppCompatActivity {
    User user;
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // Configuração do BottomNavigationView
        nav = findViewById(R.id.navbar);
        nav.setSelectedItemId(R.id.lojafooter); // Seleciona o item "Loja"

        user = (User) getIntent().getSerializableExtra("user");

        // Listener para navegação entre itens do BottomNavigationView
        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(StoreActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            return true;
        });
    }
}
