package goldenage.delfis.app.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.request.StreakRequest;
import goldenage.delfis.app.ui.activity.navbar.HomeActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Session;
import goldenage.delfis.app.model.request.LoginRequest;
import goldenage.delfis.app.model.response.LoginResponse;
import goldenage.delfis.app.model.response.Streak;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.RetrofitFactory;
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
        EditText passwordEditText = findViewById(R.id.editTextSenha);
        Button entrarButton = findViewById(R.id.btAtualizar);
        TextView criarContaTextView = findViewById(R.id.criarConta);

        entrarButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos antes de continuar.", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(LoginActivity.this, "Entrando...", Toast.LENGTH_LONG).show();

            LoginRequest loginRequest = new LoginRequest(username, password);

            DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
            Call<LoginResponse> call = delfisApiService.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            String token = loginResponse.getToken();
                            if (token != null && !token.isEmpty()) {
                                Call<User> userCall = delfisApiService.getUserByUsername(token, loginRequest.getUsername());

                                userCall.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                        if (response.isSuccessful()) {
                                            User user = response.body();
                                            if (user != null) {
                                                user.setToken(token);

                                                Call<Streak> streakCall = delfisApiService.getCurrentStreakByUser(token, user.getId());
                                                streakCall.enqueue(new Callback<Streak>() {
                                                    @Override
                                                    public void onResponse(@NonNull Call<Streak> call, @NonNull Response<Streak> response) {
                                                        Streak streak = response.body();
                                                        if (streak != null) {
                                                            user.setCurrentStreak(streak);
                                                        } else {
                                                            StreakRequest streakRequest = new StreakRequest(
                                                                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                                                    user.getId()
                                                            );

                                                            Call<Streak> streakInsertCall = delfisApiService.createStreak(token, streakRequest);
                                                            streakInsertCall.enqueue(new Callback<Streak>() {
                                                                @Override
                                                                public void onResponse(Call<Streak> call, Response<Streak> response) {
                                                                    if (response.isSuccessful()) {
                                                                        Streak newStreak = response.body();
                                                                        user.setCurrentStreak(newStreak);
                                                                    } else {
                                                                        Toast.makeText(LoginActivity.this, "Erro ao criar streak.", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<Streak> call, Throwable t) {
                                                                    Toast.makeText(LoginActivity.this, "Erro de conexão ao criar streak. Verifique sua internet e tente novamente.", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }

                                                        Call<Session> callSession = delfisApiService.startSession(token, user.getId());
                                                        callSession.enqueue(new Callback<Session>() {
                                                            @Override
                                                            public void onResponse(@NonNull Call<Session> call, @NonNull Response<Session> response) {
                                                                if (response.isSuccessful()) {
                                                                    Session session = response.body();
                                                                    user.setCurrentSession(session);

                                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                                    intent.putExtra("user", user);
                                                                    startActivity(intent);
                                                                    
                                                                } else {
                                                                    Log.d(TAG, "Erro ao recuperar sessão: " + response.code());
                                                                    Toast.makeText(LoginActivity.this, "Falha ao carregar sua sessão. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(@NonNull Call<Session> call, @NonNull Throwable t) {
                                                                Log.d(TAG, "Erro ao recuperar sessão: " + t.getMessage());
                                                                Toast.makeText(LoginActivity.this, "Não foi possível iniciar sua sessão no momento. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onFailure(@NonNull Call<Streak> call, @NonNull Throwable t) {
                                                        Log.e(TAG, "Erro na conexão ao buscar streak.", t);
                                                        Toast.makeText(LoginActivity.this, "Erro de conexão com o servidor. Por favor, verifique sua internet e tente novamente.", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            } else {
                                                Log.d(TAG, "Erro ao recuperar dados do usuário: resposta vazia.");
                                                Toast.makeText(LoginActivity.this, "Ocorreu um erro ao carregar suas informações. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Log.d(TAG, "Erro ao recuperar usuário: código " + response.code());
                                            Toast.makeText(LoginActivity.this, "Não foi possível carregar seus dados de usuário. Verifique sua conexão e tente novamente.", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                        Log.e(TAG, "Erro ao conectar para recuperar dados do usuário.", t);
                                        Toast.makeText(LoginActivity.this, "Erro de conexão ao carregar seus dados. Verifique sua internet e tente novamente.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Log.d(TAG, "Token não recebido ou inválido.");
                                Toast.makeText(LoginActivity.this, "Autenticação falhou. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "Resposta da API vazia.");
                            Toast.makeText(LoginActivity.this, "Não recebemos resposta do servidor. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(TAG, "Erro no login: código " + response.code());
                        Toast.makeText(LoginActivity.this, "Credenciais incorretas. Verifique seu username e senha e tente novamente.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Erro na conexão durante o login.", t);
                    Toast.makeText(LoginActivity.this, "Falha ao conectar com o servidor. Verifique sua conexão com a internet e tente novamente.", Toast.LENGTH_LONG).show();
                }
            });
        });

        criarContaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            
        });
    }
}
