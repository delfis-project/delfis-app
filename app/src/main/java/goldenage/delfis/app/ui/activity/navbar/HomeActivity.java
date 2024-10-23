package goldenage.delfis.app.ui.activity.navbar;

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
import goldenage.delfis.app.ui.activity.game.MathChallengesActivity;
import goldenage.delfis.app.ui.activity.game.SudokuActivity;
import goldenage.delfis.app.ui.activity.game.TicTacToeActivity;
import goldenage.delfis.app.model.response.Streak;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.sell.SellPremiumActivity;
import goldenage.delfis.app.util.ActivityUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    private User user;
    private BottomNavigationView nav;
    private ImageView btSudoku, btJogoVelha, btDesafiosMatematicos, btAntiAds, btDesafioDiario;
    private TextView textCoins, textStreak;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.bottom_menu);
        user = (User) getIntent().getSerializableExtra("user");
        btSudoku = findViewById(R.id.btSudoku);
        btJogoVelha = findViewById(R.id.btJogoVelha);
        btDesafiosMatematicos = findViewById(R.id.btDesafiosMatematicos);
        textCoins = findViewById(R.id.textCoins);
        textStreak = findViewById(R.id.textStreak);
        btAntiAds = findViewById(R.id.btAntiAds);
        btDesafioDiario = findViewById(R.id.btDesafioDiario);

        textCoins.setText(String.valueOf(user.getCoins()));

        atualizarStreak();

        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(HomeActivity.this, item);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
            
            return true;
        });

        btAntiAds.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SellPremiumActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btSudoku.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SudokuActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btJogoVelha.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, TicTacToeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btDesafiosMatematicos.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MathChallengesActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        btDesafioDiario.setOnClickListener(view -> {
            int n = random.nextInt(4);
            Intent intent;

            switch (n) {
                case 0:
                    intent = new Intent(HomeActivity.this, SudokuActivity.class);
                    break;
                case 1:
                    intent = new Intent(HomeActivity.this, TicTacToeActivity.class);
                    break;
                default:
                    intent = new Intent(HomeActivity.this, MathChallengesActivity.class);
                    break;
            }

            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    private void atualizarStreak() {
        Streak streakAtual = user.getCurrentStreak();
        int dias = 0;
        if (streakAtual != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate inicio = LocalDate.parse(streakAtual.getInitialDate(), formatter);
            dias = (int) (LocalDate.now().toEpochDay() - inicio.toEpochDay()) + 1;
        }
        textStreak.setText(String.valueOf(dias));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
