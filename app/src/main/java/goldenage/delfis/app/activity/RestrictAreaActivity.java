package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.game.SudokuActivity;
import goldenage.delfis.app.activity.navbar.ConfigActivity;
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.activity.navbar.LeaderboardActivity;
import goldenage.delfis.app.model.response.User;

public class RestrictAreaActivity extends AppCompatActivity {
    private User user;
    private ImageView btDashboard, btForm, btSetaVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict_area);

        user = (User) getIntent().getSerializableExtra("user");
        btDashboard = findViewById(R.id.btDashboard);
        btForm = findViewById(R.id.btForm);
        btSetaVoltar = findViewById(R.id.btSetaVoltar9);

        btDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(RestrictAreaActivity.this, WebViewActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("url", "https://delfis-project.github.io/delfis-restrict-area");
            startActivity(intent);
        });

        btForm.setOnClickListener(v -> {
            Intent intent = new Intent(RestrictAreaActivity.this, WebViewActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("url", "http://ec2-34-232-168-144.compute-1.amazonaws.com:5000");
            startActivity(intent);
        });

        btSetaVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(RestrictAreaActivity.this, ConfigActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RestrictAreaActivity.this, ConfigActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}