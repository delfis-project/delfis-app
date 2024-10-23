package goldenage.delfis.app.model.client;

import java.util.ArrayList;
import java.util.List;

import goldenage.delfis.app.api.GptApiService;
import goldenage.delfis.app.model.request.GptRequest;
import goldenage.delfis.app.model.response.GptResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GptClient {
    private final GptApiService gptApiService;
    private String baseUrl, apiKey;

    public GptClient(String baseUrl, String apiKey) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.gptApiService = retrofit.create(GptApiService.class);
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public void enviarJogadaGpt(String jogoStr, final GPTCallback callback) {
        String prompt = String.format("Tenho essa matriz que representa um jogo da velha em Java: %s\nO caracter 'o' representa a bolinha. O caracter 'x' representa o x. O resto são posições que você pode jogar.\nVocê é a bolinha. Faça uma jogada inteligente. Represente colocando a sua resposta nesse formato:\ni: posição j: posição", jogoStr);

        List<GptRequest.Message> messages = new ArrayList<>();
        messages.add(new GptRequest.Message("system", "Você deve fazer uma jogada em um jogo da velha."));
        messages.add(new GptRequest.Message("user", prompt));

        GptRequest gptRequest = new GptRequest("gpt-3.5-turbo", messages);

        Call<GptResponse> call = gptApiService.enviarMensagem("Bearer " + this.apiKey, gptRequest);
        call.enqueue(new Callback<GptResponse>() {
            @Override
            public void onResponse(Call<GptResponse> call, Response<GptResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String content = response.body().getChoices().get(0).getMessage().getContent();
                        int i = Integer.parseInt(content.split("i: ")[1].split("j")[0].trim());
                        int j = Integer.parseInt(content.split("j: ")[1].trim());

                        callback.onSuccess(i, j);
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new Exception("Falha na resposta da API."));
                }
            }

            @Override
            public void onFailure(Call<GptResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface GPTCallback {
        void onSuccess(int i, int j);
        void onFailure(Throwable t);
    }
}
