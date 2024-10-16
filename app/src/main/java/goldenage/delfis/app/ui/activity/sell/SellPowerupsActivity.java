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
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellPowerupsActivity extends AppCompatActivity {
    private User user;
    private RecyclerView recyclerViewLoja, recyclerViewUser;
    private TextView textMoedas, textVazio;
    private ImageView btSeta;
    private final List<Powerup> powerups = new ArrayList<>();
    private final List<Powerup> powerupsUser = new ArrayList<>();
    private AdapterPowerupStore adapterPowerupStore, adapterPowerupStoreUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_powerups);

        recyclerViewLoja = findViewById(R.id.recyclerViewLoja);
        user = (User) getIntent().getSerializableExtra("user");
        textMoedas = findViewById(R.id.textMoedas);
        btSeta = findViewById(R.id.btSeta);
        recyclerViewUser = findViewById(R.id.recyclerViewUser);
        textVazio = findViewById(R.id.textVazio);

        btSeta.setOnClickListener(v -> {
            Intent intent = new Intent(SellPowerupsActivity.this, StoreActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
        textMoedas.setText(String.valueOf(user.getCoins()));

        adapterPowerupStore = new AdapterPowerupStore(powerups, user);
        recyclerViewLoja.setAdapter(adapterPowerupStore);
        recyclerViewLoja.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterPowerupStoreUser = new AdapterPowerupStore(powerupsUser, user);
        recyclerViewUser.setAdapter(adapterPowerupStoreUser);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Toast.makeText(SellPowerupsActivity.this, "Carregando powerups...", Toast.LENGTH_LONG).show();
        loadPowerups(user.getToken());
    }

    private void loadPowerups(String token) {
        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);

        Call<List<Powerup>> call = delfisApiService.getAllPowerups(token);
        call.enqueue(new Callback<List<Powerup>>() {
            @Override
            public void onResponse(@NonNull Call<List<Powerup>> call, @NonNull Response<List<Powerup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    powerups.clear();
                    powerups.addAll(response.body());
                    adapterPowerupStore.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Erro ao recuperar powerups: " + response.code());
                    Toast.makeText(SellPowerupsActivity.this, "Falha ao carregar powerups. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Powerup>> call, @NonNull Throwable t) {
                Log.d(TAG, "Erro ao recuperar powerups: " + t.getMessage());
                Toast.makeText(SellPowerupsActivity.this, "Não foi possível carregar powerups. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });

        Call<List<Powerup>> call2 = delfisApiService.getPowerupsByAppUserId(token, user.getId());
        call2.enqueue(new Callback<List<Powerup>>() {
            @Override
            public void onResponse(@NonNull Call<List<Powerup>> call, @NonNull Response<List<Powerup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    powerupsUser.clear();
                    powerupsUser.addAll(response.body());
                    adapterPowerupStoreUser.notifyDataSetChanged();

                    textVazio.setVisibility(View.INVISIBLE);
                } else {
                    recyclerViewUser.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Powerup>> call, @NonNull Throwable t) {
                recyclerViewUser.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Erro ao recuperar powerups: " + t.getMessage());
                Toast.makeText(SellPowerupsActivity.this, "Não foi possível carregar powerups. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }
}