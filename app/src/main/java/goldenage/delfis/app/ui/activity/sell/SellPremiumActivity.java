package goldenage.delfis.app.ui.activity.sell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Plan;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.HomeActivity;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellPremiumActivity extends AppCompatActivity {
    private User user;
    private Plan premiumPlan;
    private ImageView btX, btComprar;
    private TextView textBtCompra;
    private static final String PREMIUM_NAME = "Premium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_premium);

        user = (User) getIntent().getSerializableExtra("user");
        textBtCompra = findViewById(R.id.textBtCompra);
        btX = findViewById(R.id.btX);
        btComprar = findViewById(R.id.btComprar);

        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
        Call<Plan> call = delfisApiService.getPlanByName(user.getToken(), PREMIUM_NAME);
        call.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    premiumPlan = response.body();
                    textBtCompra.setText("Comprar por R$" + String.valueOf(premiumPlan.getPrice()).replace(".", ","));
                } else {
                    Toast.makeText(SellPremiumActivity.this, "Erro ao buscar plano. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                Toast.makeText(SellPremiumActivity.this, "Erro ao buscar plano. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });

        btComprar.setOnClickListener(v -> {
            if (premiumPlan != null && user.getPlanId() == premiumPlan.getId()) {
                Toast.makeText(SellPremiumActivity.this, "Você já é Premium!.", Toast.LENGTH_LONG).show();
                return;
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (premiumPlan == null) return;

                notificarCompra();

                Map<String, Object> updates = new HashMap<>();
                updates.put("fkPlanId", premiumPlan.getId());

                Call<User> call2 = delfisApiService.updateUserPartially(user.getToken(), user.getId(), updates);
                call2.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User updatedUser = response.body();
                            if (updatedUser != null)
                                user.setPlanId(updatedUser.getPlanId());
                        } else {
                            Toast.makeText(SellPremiumActivity.this, "Falha ao atualizar informações.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(SellPremiumActivity.this, "Falha na conexão.", Toast.LENGTH_LONG).show();
                    }
                });
            }, 90_000);
        });

        btX.setOnClickListener(v -> {
            Intent intent = new Intent(SellPremiumActivity.this, StoreActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    public void notificarCompra() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        @SuppressLint("DefaultLocale")
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Compra aprovada!")
                .setContentText(String.format("Sua compra de 1 mês do plano Premium foi aprovada!"))
                .setSmallIcon(R.drawable.delfis)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationChannel channel = new NotificationChannel("channel_id", "Notificar",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}