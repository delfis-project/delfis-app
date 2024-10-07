package goldenage.delfis.app.activity.navbar;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.game.WordSearchActivity;
import goldenage.delfis.app.activity.game.SudokuActivity;
import goldenage.delfis.app.model.response.Streak;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.ActivityUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomeActivity extends AppCompatActivity {
    User user;
    BottomNavigationView nav;
    ImageView btSudoku, btCacaPalavras;
    TextView textCoins, textStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nav = findViewById(R.id.navbar);
        nav.setSelectedItemId(R.id.homefooter);
        user = (User) getIntent().getSerializableExtra("user");
        btSudoku = findViewById(R.id.btSudoku);
        btCacaPalavras = findViewById(R.id.btCacaPalavras);
        textCoins = findViewById(R.id.textCoins);
        textStreak = findViewById(R.id.textStreak);

        textCoins.setText(String.valueOf(user.getCoins()));

        Streak streakAtual = user.getCurrentStreak();
        int dias = 0;
        if (streakAtual != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate inicio = LocalDate.parse(streakAtual.getInitialDate(), formatter);
            dias = LocalDate.now().compareTo(inicio) + 1;
        }
        textStreak.setText(String.valueOf(dias));

        notificarDesafioDiario();

        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(HomeActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            return true;
        });

        btSudoku.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SudokuActivity.class);

            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
        });

        btCacaPalavras.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, WordSearchActivity.class);

            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
        });
    }

    public void notificarDesafioDiario() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Já fez sua atividade diária?")
                .setContentText("Não se esqueça de completar sua atividade diária para ganhar moedas e recompensas!")
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

        // Envia a notificação
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
