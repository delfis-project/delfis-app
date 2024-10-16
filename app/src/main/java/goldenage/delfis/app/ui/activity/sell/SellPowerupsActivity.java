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
    private RecyclerView recyclerViewLoja;
    private TextView textMoedas;
    private ImageView btSeta;
    private List<Powerup> powerups = new ArrayList<>();
    private AdapterPowerupStore adapterPowerupStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_powerups);

        recyclerViewLoja = findViewById(R.id.recyclerViewLoja);
        user = (User) getIntent().getSerializableExtra("user");
        textMoedas = findViewById(R.id.textMoedas);
        btSeta = findViewById(R.id.btSeta);

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

        Toast.makeText(SellPowerupsActivity.this, "Carregando powerups...", Toast.LENGTH_LONG).show();
        loadPowerups(user.getToken());
    }

    private void loadPowerups(String token) {
        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
        Call<List<Powerup>> call = delfisApiService.getAllPowerups(token);
        call.enqueue(new Callback<List<Powerup>>() {
            @Override
            public void onResponse(Call<List<Powerup>> call, Response<List<Powerup>> response) {
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
            public void onFailure(Call<List<Powerup>> call, Throwable t) {
                Log.d(TAG, "Erro ao recusperar powerups: " + t.getMessage());
                Toast.makeText(SellPowerupsActivity.this, "Não foi possível carregar powerups. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }
}