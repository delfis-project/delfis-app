package goldenage.delfis.app.api;

import goldenage.delfis.app.model.LoginRequest;
import goldenage.delfis.app.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginRequestApi {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
