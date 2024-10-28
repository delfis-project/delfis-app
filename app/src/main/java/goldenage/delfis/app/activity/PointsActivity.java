package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.model.response.User;

public class PointsActivity extends AppCompatActivity {
    User user;
    Button btVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        user = (User) getIntent().getSerializableExtra("user");

        btVoltar = findViewById(R.id.btVoltar9);
        btVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(PointsActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }
}