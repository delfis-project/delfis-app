package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.game.MathChallengesActivity;
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.model.response.User;

public class PointsActivity extends AppCompatActivity {
    private User user;
    private TextView textCoinsGanhadas;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        user = (User) getIntent().getSerializableExtra("user");
        textCoinsGanhadas = findViewById(R.id.textCoinsGanhadas);

        textCoinsGanhadas.setText(String.format("+%d DELFICOINS", getIntent().getIntExtra("coins", 1)));

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PointsActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }, 3000);
    }
}