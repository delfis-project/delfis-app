package goldenage.delfis.app.activity.navbar;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;
import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.EditInfoActivity;
import goldenage.delfis.app.activity.ProfilePictureActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Session;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.ActivityUtil;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigActivity extends AppCompatActivity {
    private User user;
    private BottomNavigationView nav;
    private TextView levelUser, textNome, textEmail, textNascimento;
    private ImageView btMudarFoto, btSair, btEditarInfo;
    private CircleImageView imgPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.configMenu);
        levelUser = findViewById(R.id.leveluser);
        textNome = findViewById(R.id.textNome);
        textEmail = findViewById(R.id.textEmail);
        textNascimento = findViewById(R.id.textNascimento);
        imgPerfil = findViewById(R.id.imgPerfil);
        btMudarFoto = findViewById(R.id.btMudarFoto);
        btSair = findViewById(R.id.btSair);
        btEditarInfo = findViewById(R.id.btEditarInfo);

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

        btMudarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(ConfigActivity.this, ProfilePictureActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        btEditarInfo.setOnClickListener(v -> {
            Intent intent = new Intent(ConfigActivity.this, EditInfoActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        // Listener para navegação
        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(ConfigActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            finish();
            return true;
        });

        btSair.setOnClickListener(v -> {
            DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
            Call<Session> call = delfisApiService.finishSession(user.getToken(), user.getId());
            Toast.makeText(ConfigActivity.this, "Saindo...", Toast.LENGTH_LONG).show();
            call.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    if(response.isSuccessful()) {
                        finishAffinity();
                    } else {
                        Log.d(TAG, "Erro ao recuperar sessão: " + response.code());
                        Toast.makeText(ConfigActivity.this, "Falha ao finalizar sessão. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    Log.e(TAG, "Erro ao conectar para finalizar sessão.", t);
                    Toast.makeText(ConfigActivity.this, "Erro de conexão ao carregar seus dados. Verifique sua internet e tente novamente.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}