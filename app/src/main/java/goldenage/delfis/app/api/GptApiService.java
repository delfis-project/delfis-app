package goldenage.delfis.app.api;

import goldenage.delfis.app.model.request.GptRequest;
import goldenage.delfis.app.model.response.GptResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GptApiService {
    @Headers("Content-Type: application/json")
    @POST("path/to/endpoint")
    Call<GptResponse> enviarMensagem(@Header("Authorization") String authHeader, @Body GptRequest body);
}