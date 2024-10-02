package goldenage.delfis.app.api;

import java.util.List;

import goldenage.delfis.app.model.LoginRequest;
import goldenage.delfis.app.model.LoginResponse;
import goldenage.delfis.app.model.Streak;
import goldenage.delfis.app.model.UserRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.GET;

public interface DelfisApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/app-user/insert")
    Call<Void> createUser(@Header("Authorization") String token, @Body UserRequest userRequest);

    @GET("api/streak/get-by-app-user/{id}")
    Call<Streak> getCurrentStreakByUser(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("/api/session/insert/{id}")
    Call<Void> startSession(@Header("Authorization") String token, @Path("id") long fkAppUserId);

    @POST("/api/session/finish/{id}")
    Call<Void> finishSession(@Header("Authorization") String token, @Path("id") long fkAppUserId);
}
