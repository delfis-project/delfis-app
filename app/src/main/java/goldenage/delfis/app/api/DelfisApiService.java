package goldenage.delfis.app.api;

import java.util.List;
import java.util.Map;

import goldenage.delfis.app.model.request.AppUserPowerupRequest;
import goldenage.delfis.app.model.request.AppUserThemeRequest;
import goldenage.delfis.app.model.request.StreakRequest;
import goldenage.delfis.app.model.response.AppUserPowerup;
import goldenage.delfis.app.model.response.AppUserTheme;
import goldenage.delfis.app.model.response.Plan;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.Session;
import goldenage.delfis.app.model.request.LoginRequest;
import goldenage.delfis.app.model.response.LoginResponse;
import goldenage.delfis.app.model.response.Streak;
import goldenage.delfis.app.model.response.Sudoku;
import goldenage.delfis.app.model.response.Theme;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.model.request.UserRequest;
import goldenage.delfis.app.model.response.WordSearch;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DelfisApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/app-user/insert")
    Call<Void> createUser(@Header("Authorization") String token, @Body UserRequest userRequest);

    @POST("api/streak/insert")
    Call<Streak> createStreak(@Header("Authorization") String token, @Body StreakRequest streakRequest);

    @POST("api/app-user-powerup/insert")
    Call<AppUserPowerup> createAppUserPowerup(@Header("Authorization") String token, @Body AppUserPowerupRequest appUserPowerupRequest);

    @POST("api/app-user-theme/insert")
    Call<AppUserTheme> createAppUserTheme(@Header("Authorization") String token, @Body AppUserThemeRequest appUserThemeRequest);

    @GET("api/app-user/get-by-username/{username}")
    Call<User> getUserByUsername(@Header("Authorization") String token, @Path("username") String username);

    @PATCH("api/app-user/update/{id}")
    Call<User> updateUserPartially(@Header("Authorization") String token, @Path("id") long id, @Body Map<String, Object> updates);

    @GET("api/streak/get-current-streak-by-app-user-id/{id}")
    Call<Streak> getCurrentStreakByUser(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("api/session/insert/{id}")
    Call<Session> startSession(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("api/session/finish/{id}")
    Call<Session> finishSession(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @GET("api/app-user/leaderboard")
    Call<List<User>> getLeaderboard(@Header("Authorization") String token);

    @GET("api/powerup/get-all")
    Call<List<Powerup>> getAllPowerups(@Header("Authorization") String token);

    @GET("api/theme/get-all")
    Call<List<Theme>> getAllThemes(@Header("Authorization") String token);

    @GET("api/plan/get-by-name/{name}")
    Call<Plan> getPlanByName(@Header("Authorization") String token, @Path("name") String name);

    @GET("api/powerup/get-by-app-user/{id}")
    Call<List<Powerup>> getPowerupsByAppUserId(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @GET("api/theme/get-by-app-user/{id}")
    Call<List<Theme>> getThemesByAppUserId(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("api/sudoku/generate")
    Call<Sudoku> generateSudokuBoard(@Header("Authorization") String token);
}
