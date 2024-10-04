package goldenage.delfis.app.activity.sell;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Plan;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumActivity extends AppCompatActivity {
    User user;
    private final String PREMIUM_NAME = "Premium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_premium);

        user = (User) getIntent().getSerializableExtra("user");

        DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
        Call<Plan> call = delfisApiService.getPlanByName(user.getToken(), PREMIUM_NAME);
        call.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Plan plan = response.body();
                } else {
                    Toast.makeText(PremiumActivity.this, "Erro ao buscar plano. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                Toast.makeText(PremiumActivity.this, "Erro ao buscar plano. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}