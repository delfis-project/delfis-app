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
import android.widget.ImageView;
import android.widget.TextView;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.HomeActivity;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;

public class SellCoinsActivity extends AppCompatActivity {
    private User user;
    private ImageView btSeta, btCompra1, btCompra2, btCompra3, btCompra4;
    private TextView textMoedas, textMoedas1, textMoedas2, textMoedas3, textMoedas4;
    private TextView textBtCompra1, textBtCompra2, textBtCompra3, textBtCompra4;

    private static TextView[] TEXT_BOTOES;
    private static TextView[] TEXT_QUANTIDADES;
    private static ImageView[] BT_COMPRAS;
    private static final int[] QUANTIDADES = {50, 100, 150, 200};
    private static final double[] PRECOS = {9.99, 15.99, 19.99, 29.99};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins);

        user = (User) getIntent().getSerializableExtra("user");
        btSeta = findViewById(R.id.btSeta);
        btCompra1 = findViewById(R.id.btCompra1);
        btCompra2 = findViewById(R.id.btCompra2);
        btCompra3 = findViewById(R.id.btCompra3);
        btCompra4 = findViewById(R.id.btCompra4);
        textMoedas = findViewById(R.id.textMoedas);
        textMoedas1 = findViewById(R.id.textMoedas1);
        textMoedas2 = findViewById(R.id.textMoedas2);
        textMoedas3 = findViewById(R.id.textMoedas3);
        textMoedas4 = findViewById(R.id.textMoedas4);
        textBtCompra1 = findViewById(R.id.textBtCompra1);
        textBtCompra2 = findViewById(R.id.textBtCompra2);
        textBtCompra3 = findViewById(R.id.textBtCompra3);
        textBtCompra4 = findViewById(R.id.textBtCompra4);

        textMoedas.setText(String.valueOf(user.getCoins()));

        btSeta.setOnClickListener(v -> {
            Intent intent = new Intent(SellCoinsActivity.this, StoreActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        TEXT_BOTOES = new TextView[]{textBtCompra1, textBtCompra2, textBtCompra3, textBtCompra4};
        TEXT_QUANTIDADES = new TextView[]{textMoedas1, textMoedas2, textMoedas3, textMoedas4};
        for (int i = 0; i < TEXT_BOTOES.length; i++) {
            TEXT_BOTOES[i].setText("Comprar por R$" + String.valueOf(PRECOS[i]).replace('.', ','));
            TEXT_QUANTIDADES[i].setText(String.valueOf(QUANTIDADES[i]));
        }

        BT_COMPRAS = new ImageView[]{btCompra1, btCompra2, btCompra3, btCompra4};
        
    }

    public void notificarCompra(int quantidadeMoedas) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        @SuppressLint("DefaultLocale")
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Compra aprovada!")
                .setContentText(String.format("Sua compra de %d moedas foi aprovada!", quantidadeMoedas))
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