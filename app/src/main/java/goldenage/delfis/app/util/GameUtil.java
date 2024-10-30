package goldenage.delfis.app.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import goldenage.delfis.app.activity.PointsActivity;
import goldenage.delfis.app.activity.game.MathChallengesActivity;
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameUtil {
    private static final long ID_POWERUP_DOBRO_COINS = 1L;

    public static void payUser(Context context, User user) {
        Map<String, Object> updates = new HashMap<>();
        Intent intent = new Intent(context, PointsActivity.class);

        user.setCounterPlayedGames(user.getCounterPlayedGames() + 1);

        boolean haveDoubleCoins = false;
        for (Powerup powerup : user.getPowerups()) {
            if (powerup.getId() == ID_POWERUP_DOBRO_COINS) {
                haveDoubleCoins = true;
                break;
            }
        }

        updates.put("coins", user.getCoins() + 1);
        if (haveDoubleCoins) {
            updates.put("coins", user.getCoins() + 2);
            intent.putExtra("coins", 2);
        }
        if (user.getCounterPlayedGames() == 10)
            updates.put("level", user.getLevel() + 1);
        updates.put("points", user.getPoints() + 1);

        Toast.makeText(context, "Adicionando suas recompensas...", Toast.LENGTH_LONG).show();

        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
        Call<User> call = delfisApiService.updateUserPartially(user.getToken(), user.getId(), updates);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User updatedUser = response.body();
                    if (updatedUser != null) {
                        user.setCoins(updatedUser.getCoins());
                        user.setLevel(updatedUser.getLevel());
                        user.setPoints(updatedUser.getPoints());

                        intent.putExtra("user", user);
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, "Falha ao atualizar informações.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Falha na conexão.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
