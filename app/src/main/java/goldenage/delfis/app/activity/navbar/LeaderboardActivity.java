package goldenage.delfis.app.activity.navbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.User;
import goldenage.delfis.app.util.ActivityUtil;

public class LeaderboardActivity extends AppCompatActivity {
    BottomNavigationView nav;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        nav = findViewById(R.id.navbar);
        user = (User) getIntent().getSerializableExtra("user");

        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(LeaderboardActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            return true;
        });
    }
}