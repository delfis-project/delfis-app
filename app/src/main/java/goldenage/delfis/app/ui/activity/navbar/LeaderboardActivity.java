package goldenage.delfis.app.ui.activity.navbar;

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
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {
    private BottomNavigationView nav;
    private User user;
    private RecyclerView recyclerViewUsers;
    private List<User> users = new ArrayList<>();
    private AdapterUserLeaderboard adapterUserLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.placarMenu);
        user = (User) getIntent().getSerializableExtra("user");
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        adapterUserLeaderboard = new AdapterUserLeaderboard(users, user);
        recyclerViewUsers.setAdapter(adapterUserLeaderboard);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        Toast.makeText(LeaderboardActivity.this, "Carregando usuários...", Toast.LENGTH_LONG).show();
        loadLeaderboard(user.getToken());

        nav.setOnItemSelectedListener(item -> {
            Intent intent = ActivityUtil.getNextIntent(LeaderboardActivity.this, item);
            intent.putExtra("user", user);
            startActivity(intent);
            
            return true;
        });
    }

    private void loadLeaderboard(String token) {
        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
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
                Log.d(TAG, "Erro ao recusperar usuários: " + t.getMessage());
                Toast.makeText(LeaderboardActivity.this, "Não foi possível carregar o placar. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
