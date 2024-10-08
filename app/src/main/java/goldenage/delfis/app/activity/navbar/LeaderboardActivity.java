package goldenage.delfis.app.activity.navbar;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.adapter.AdapterUserLeaderboard;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.ActivityUtil;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {
    BottomNavigationView nav;
    User user;
    RecyclerView recyclerViewUsers;
    List<User> users = new ArrayList<>();
    AdapterUserLeaderboard adapterUserLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        nav = findViewById(R.id.navbar);
        nav.setSelectedItemId(R.id.leaderfooter);
        user = (User) getIntent().getSerializableExtra("user");
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        adapterUserLeaderboard = new AdapterUserLeaderboard(users, user);
        recyclerViewUsers.setAdapter(adapterUserLeaderboard);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        Toast.makeText(LeaderboardActivity.this, "Carregando usuários...", Toast.LENGTH_LONG).show();
        loadLeaderboard(user.getToken());

        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(LeaderboardActivity.this, item);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            return true;
        });
    }

    private void loadLeaderboard(String token) {
        DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
        Call<List<User>> call = delfisApiService.getLeaderboard(token);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.clear();
                    users.addAll(response.body());
                    adapterUserLeaderboard.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Erro ao recuperar usuários: " + response.code());
                    Toast.makeText(LeaderboardActivity.this, "Falha ao carregar placar. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Erro ao recuperar usuários: " + t.getMessage());
                Toast.makeText(LeaderboardActivity.this, "Não foi possível carregar o placar. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
