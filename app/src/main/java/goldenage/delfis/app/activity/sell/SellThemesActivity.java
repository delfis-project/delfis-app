package goldenage.delfis.app.ui.activity.sell;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.adapter.AdapterPowerupStore;
import goldenage.delfis.app.adapter.AdapterThemeStore;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.Theme;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellThemesActivity extends AppCompatActivity {
    private User user;
    private RecyclerView recyclerViewLoja, recyclerViewUser;
    private TextView textMoedas, textVazio;
    private ImageView btSeta;
    private final List<Theme> themes = new ArrayList<>();
    private final List<Theme> themesUser = new ArrayList<>();
    private AdapterThemeStore adapterThemeStore, adapterThemeStoreUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_themes);

        textMoedas = findViewById(R.id.textMoedas);
        btSeta = findViewById(R.id.btSeta);
        textVazio = findViewById(R.id.textVazio);

        renderizarInfo();

        btSeta.setOnClickListener(v -> {
            Intent intent = new Intent(SellThemesActivity.this, StoreActivity.class);
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
        recyclerViewUser = findViewById(R.id.recyclerViewUser);
        recyclerViewLoja = findViewById(R.id.recyclerViewLoja);
        user = (User) getIntent().getSerializableExtra("user");

        textMoedas.setText(String.valueOf(user.getCoins()));

        adapterThemeStore = new AdapterThemeStore(themes, user);
        recyclerViewLoja.setAdapter(adapterThemeStore);
        recyclerViewLoja.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterThemeStoreUser = new AdapterThemeStore(themesUser, user);
        recyclerViewUser.setAdapter(adapterThemeStoreUser);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
                    Log.d(TAG, "Erro ao recuperar temas: " + response.code());
                    Toast.makeText(SellThemesActivity.this, "Falha ao carregar temas. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Theme>> call, Throwable t) {
                Log.d(TAG, "Erro ao recuperar temas: " + t.getMessage());
                Toast.makeText(SellThemesActivity.this, "Não foi possível carregar temas. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });

        if (user.getThemes() != null && !user.getThemes().isEmpty()) {
            themesUser.clear();
            themesUser.addAll(user.getThemes());
            adapterThemeStoreUser.notifyDataSetChanged();

            textVazio.setVisibility(View.INVISIBLE);
        } else {
            recyclerViewUser.setVisibility(View.INVISIBLE);
        }
    }
}