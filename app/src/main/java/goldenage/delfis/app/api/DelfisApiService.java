package goldenage.delfis.app.api;

import java.util.Map;

import goldenage.delfis.app.model.Session;
import goldenage.delfis.app.model.request.LoginRequest;
import goldenage.delfis.app.model.response.LoginResponse;
import goldenage.delfis.app.model.Streak;
import goldenage.delfis.app.model.User;
import goldenage.delfis.app.model.request.UserRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.GET;

public interface DelfisApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/app-user/insert")
    Call<Void> createUser(@Header("Authorization") String token, @Body UserRequest userRequest);

    @GET("api/app-user/get-by-username/{username}")
    Call<User> getUserByUsername(@Header("Authorization") String token, @Path("username") String username);

    @PATCH("api/app-user/update/{id}")
    Call<User> updateUserPartially(@Header("Authorization") String token, @Path("id") long id, @Body Map<String, Object> updates);

    @GET("api/streak/get-current-streak-by-app-user-id/{id}")
    Call<Streak> getCurrentStreakByUser(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("/api/session/insert/{id}")
    Call<Session> startSession(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("/api/session/finish/{id}")
    Call<Session> finishSession(@Header("Authorization") String token, @Path("id") long fkAppUserId);
}
