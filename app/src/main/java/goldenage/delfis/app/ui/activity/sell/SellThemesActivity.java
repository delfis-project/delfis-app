package goldenage.delfis.app.ui.activity.sell;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.adapter.AdapterThemeStore;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Theme;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellThemesActivity extends AppCompatActivity {
    private User user;
    private RecyclerView recyclerViewLoja;
    private TextView textMoedas;
    private ImageView btSeta;
    private List<Theme> themes = new ArrayList<>();
    private AdapterThemeStore adapterThemeStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_themes);

        recyclerViewLoja = findViewById(R.id.recyclerViewLoja);
        user = (User) getIntent().getSerializableExtra("user");
        textMoedas = findViewById(R.id.textMoedas);
        btSeta = findViewById(R.id.btSeta);

        btSeta.setOnClickListener(v -> {
            Intent intent = new Intent(SellThemesActivity.this, StoreActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
        textMoedas.setText(String.valueOf(user.getCoins()));

        adapterThemeStore = new AdapterThemeStore(themes, user);
        recyclerViewLoja.setAdapter(adapterThemeStore);
        recyclerViewLoja.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Toast.makeText(SellThemesActivity.this, "Carregando temas...", Toast.LENGTH_LONG).show();
        loadThemes(user.getToken());
    }

    private void loadThemes(String token) {
        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
        Call<List<Theme>> call = delfisApiService.getAllThemes(token);
        call.enqueue(new Callback<List<Theme>>() {
            @Override
            public void onResponse(Call<List<Theme>> call, Response<List<Theme>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    themes.clear();
                    themes.addAll(response.body());
                    adapterThemeStore.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Erro ao recuperar themes: " + response.code());
                    Toast.makeText(SellThemesActivity.this, "Falha ao carregar themes. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Theme>> call, Throwable t) {
                Log.d(TAG, "Erro ao recusperar themes: " + t.getMessage());
                Toast.makeText(SellThemesActivity.this, "Não foi possível carregar themes. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }
}