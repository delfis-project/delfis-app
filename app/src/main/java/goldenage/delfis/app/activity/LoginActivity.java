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
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Session;
import goldenage.delfis.app.model.request.LoginRequest;
import goldenage.delfis.app.model.response.LoginResponse;
import goldenage.delfis.app.model.response.Streak;
import goldenage.delfis.app.model.response.User;
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
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos antes de continuar.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(LoginActivity.this, "Entrando...", Toast.LENGTH_SHORT).show();

            LoginRequest loginRequest = new LoginRequest(email, password);

            DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
            Call<LoginResponse> call = delfisApiService.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            String token = loginResponse.getToken();
                            if (token != null && !token.isEmpty()) {
                                Call<User> userCall = delfisApiService.getUserByUsername(token, loginRequest.getUsername());

                                userCall.enqueue(new Callback<User>() {
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

                                                            Call<Session> callSession = delfisApiService.startSession(token, user.getId());
                                                            callSession.enqueue(new Callback<Session>() {
                                                                @Override
                                                                public void onResponse(Call<Session> call, Response<Session> response) {
                                                                    if (response.isSuccessful()) {
                                                                        Session session = response.body();
                                                                        user.setCurrentSession(session);

                                                                        intent.putExtra("user", user);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        Log.d(TAG, "Erro ao recuperar sessão: " + response.code());
                                                                        Toast.makeText(LoginActivity.this, "Falha ao carregar sua sessão. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<Session> call, Throwable t) {
                                                                    Log.d(TAG, "Erro ao recuperar sessão: " + t.getMessage());
                                                                    Toast.makeText(LoginActivity.this, "Não foi possível iniciar sua sessão no momento. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        } else {
                                                            Log.d(TAG, "Erro ao recuperar streak: " + response.code());
                                                            Toast.makeText(LoginActivity.this, "Falha ao carregar sua sequência atual de conquistas. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Streak> call, Throwable t) {
                                                        Log.e(TAG, "Erro na conexão ao buscar streak.", t);
                                                        Toast.makeText(LoginActivity.this, "Erro de conexão com o servidor. Por favor, verifique sua internet e tente novamente.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Log.d(TAG, "Erro ao recuperar dados do usuário: resposta vazia.");
                                                Toast.makeText(LoginActivity.this, "Ocorreu um erro ao carregar suas informações. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.d(TAG, "Erro ao recuperar usuário: código " + response.code());
                                            Toast.makeText(LoginActivity.this, "Não foi possível carregar seus dados de usuário. Verifique sua conexão e tente novamente.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Log.e(TAG, "Erro ao conectar para recuperar dados do usuário.", t);
                                        Toast.makeText(LoginActivity.this, "Erro de conexão ao carregar seus dados. Verifique sua internet e tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.d(TAG, "Token não recebido ou inválido.");
                                Toast.makeText(LoginActivity.this, "Autenticação falhou. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Resposta da API vazia.");
                            Toast.makeText(LoginActivity.this, "Não recebemos resposta do servidor. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Erro no login: código " + response.code());
                        Toast.makeText(LoginActivity.this, "Credenciais incorretas. Verifique seu email e senha e tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e(TAG, "Erro na conexão durante o login.", t);
                    Toast.makeText(LoginActivity.this, "Falha ao conectar com o servidor. Verifique sua conexão com a internet e tente novamente.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        criarContaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
