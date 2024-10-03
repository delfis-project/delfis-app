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
import goldenage.delfis.app.model.Streak;
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
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
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
                                Call<User> userCall = delfisApiService.getUserByUsername(token, loginRequest.getUsername());

                                userCall.enqueue(new Callback<User>() {  // pegando infos do usuário
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if (response.isSuccessful()) {
                                            User user = response.body();
                                            if (user != null) {
                                                user.setToken(token);

                                                Call<Streak> streakCall = delfisApiService.getCurrentStreakByUser(token, user.getId());
                                                streakCall.enqueue(new Callback<Streak>() {
                                                    @Override
                                                    public void onResponse(Call<Streak> call, Response<Streak> response) {
                                                        if (response.isSuccessful()) {
                                                            Streak streak = response.body();
                                                            user.setCurrentStreak(streak);

                                                            intent.putExtra("user", user);
                                                            startActivity(intent);
                                                        } else {
                                                            Log.d(TAG, "Erro ao recuperar streak: código " + response.code());
                                                            Toast.makeText(LoginActivity.this, "Não foi possível recuperar as informações do streak. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Streak> call, Throwable t) {
                                                        Log.e(TAG, "Erro na conexão ao buscar streak.", t);
                                                        Toast.makeText(LoginActivity.this, "Erro ao conectar com o servidor. Verifique sua conexão e tente novamente.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Log.d(TAG, "Erro ao recuperar dados do usuário: resposta vazia.");
                                                Toast.makeText(LoginActivity.this, "Erro ao carregar dados do usuário. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.d(TAG, "Erro ao recuperar usuário: código " + response.code());
                                            Toast.makeText(LoginActivity.this, "Erro ao buscar informações do usuário. Tente novamente.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Log.e(TAG, "Erro ao conectar para recuperar dados do usuário.", t);
                                        Toast.makeText(LoginActivity.this, "Falha na conexão ao recuperar dados do usuário. Verifique sua conexão e tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.d(TAG, "Token não recebido ou inválido.");
                                Toast.makeText(LoginActivity.this, "Erro ao autenticar. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Resposta da API vazia.");
                            Toast.makeText(LoginActivity.this, "Falha ao realizar login. Resposta do servidor vazia.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Erro no login: código " + response.code());
                        Toast.makeText(LoginActivity.this, "Erro ao realizar login. Verifique suas credenciais e tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e(TAG, "Erro na conexão durante o login.", t);
                    Toast.makeText(LoginActivity.this, "Falha ao conectar com o servidor. Verifique sua conexão e tente novamente.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        criarContaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}