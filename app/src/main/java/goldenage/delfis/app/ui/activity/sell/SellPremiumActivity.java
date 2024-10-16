package goldenage.delfis.app.ui.activity.sell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Plan;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellPremiumActivity extends AppCompatActivity {
    private User user;
    private ImageView btX;
    private TextView textBtCompra;
    private static final String PREMIUM_NAME = "Premium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_premium);

        user = (User) getIntent().getSerializableExtra("user");
        textBtCompra = findViewById(R.id.textBtCompra);
        btX = findViewById(R.id.btX);

        btX.setOnClickListener(v -> {
            Intent intent = new Intent(SellPremiumActivity.this, StoreActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
        Call<Plan> call = delfisApiService.getPlanByName(user.getToken(), PREMIUM_NAME);
        call.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Plan plan = response.body();
                    textBtCompra.setText("Comprar por R$" + String.valueOf(plan.getPrice()).replace(".", ","));
                } else {
                    Toast.makeText(SellPremiumActivity.this, "Erro ao buscar plano. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                Toast.makeText(SellPremiumActivity.this, "Erro ao buscar plano. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }
}