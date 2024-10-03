package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import goldenage.delfis.app.R;

import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.LoginRequest;
import goldenage.delfis.app.model.LoginResponse;
import goldenage.delfis.app.model.User;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; // Tag para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.editTextTextUsername);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword2);
        Button entrarButton = findViewById(R.id.entrar);
        TextView criarContaTextView = findViewById(R.id.criarConta);

        entrarButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            System.out.println(email + password);

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(email, password);

            DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
            Call<LoginResponse> call = delfisApiService.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {  // logando
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            String token = loginResponse.getToken();
                            if (token != null && !token.isEmpty()) {
                                // Token recebido, redireciona para a atividade Home
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Call<User> userCall = delfisApiService.getUserByUsername(token, loginRequest.getUsername());

                                userCall.enqueue(new Callback<User>() {  // pegando infos do usuário
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if (response.isSuccessful()) {
                                            User user = response.body();
                                            if (user != null) {
                                                user.setToken(token);
                                                intent.putExtra("user", user);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Erro na resposta da API. Reinicie o app e tente novamente: " + response.code(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Erro na resposta da API. Reinicie o app e tente novamente: " + response.code(), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Erro na resposta da API: " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Log.e(TAG, "Erro durante a recuperação dos dados do usuário. Reinicie o app e tente novamente: ", t);
                                        Toast.makeText(LoginActivity.this, "Erro durante recuperação de dados do usuário. Reinicie o app e tente novamente: ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Login falhou: token não recebido. Reinicie o app e tente novamente: ", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Token não recebido. Reinicie o app e tente novamente: " + token);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Resposta vazia da API. Reinicie o app e tente novamente: ", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Resposta vazia da API. Reinicie o app e tente novamente: ");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro na resposta da API. Reinicie o app e tente novamente: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Erro na resposta da API. Reinicie o app e tente novamente: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Falha na conexão com a API. Reinicie o app e tente novamente: ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Falha na conexão com a API. Reinicie o app e tente novamente: ", t);
                }
            });
        });

        criarContaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
