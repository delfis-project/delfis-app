package goldenage.delfis.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.User;

public class ConfigActivity extends AppCompatActivity {
    User user;
    BottomNavigationView nav;
    TextView levelUser, textNome, textEmail, textNascimento;
    ImageView imgPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);


        nav = findViewById(R.id.navbar);
        levelUser = findViewById(R.id.leveluser);
        textNome = findViewById(R.id.textNome);
        textEmail = findViewById(R.id.textEmail);
        textNascimento = findViewById(R.id.textNascimento);
        imgPerfil = findViewById(R.id.imgPerfil);

        user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            levelUser.setText(String.valueOf(user.getLevel()));
            textNome.setText(user.getName());
            textEmail.setText(user.getEmail());
            textNascimento.setText(user.getBirthDate());
            if (user.getPictureUrl() != null)
                Glide.with(this).load(user.getPictureUrl()).into(imgPerfil);
        }

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigActivity.this, ProfilePictureActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

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

                if (user != null)
                    intent.putExtra("user", user);

                startActivity(intent);
                return true;
            }
        });
    }
}