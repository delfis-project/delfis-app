package goldenage.delfis.app.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;
import goldenage.delfis.app.R;
import goldenage.delfis.app.model.User;

public class ConfigActivity extends AppCompatActivity {
    User user;
    BottomNavigationView nav;
    TextView levelUser, textNome, textEmail, textNascimento;
    ImageView btMudarFoto;
    CircleImageView imgPerfil;

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
        btMudarFoto = findViewById(R.id.btMudarFoto);

        user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            levelUser.setText(String.valueOf(user.getLevel()));
            textNome.setText(user.getName());
            textEmail.setText(user.getEmail());

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String data = LocalDate.parse(user.getBirthDate(), inputFormatter).format(outputFormatter);
            textNascimento.setText(data);

            if (user.getPictureUrl() != null) {
                int widthValue = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        160,
                        getResources().getDisplayMetrics());
                int heightValue = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        180,
                        getResources().getDisplayMetrics());

                ViewGroup.LayoutParams layoutParams = imgPerfil.getLayoutParams();
                layoutParams.width = widthValue;
                layoutParams.height = heightValue;
                imgPerfil.setLayoutParams(layoutParams);

                Glide.with(this).load(user.getPictureUrl()).into(imgPerfil);
            }
        }

        btMudarFoto.setOnClickListener(new View.OnClickListener() {
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