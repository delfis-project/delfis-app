package goldenage.delfis.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.request.StreakRequest;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.Theme;
import goldenage.delfis.app.activity.navbar.HomeActivity;
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
    private static final String TAG = "LoginActivity";

    private EditText passwordEditText;
    private ImageView mudarViewSenha;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.editTextTextUsername);
        passwordEditText = findViewById(R.id.editTextSenha);
        Button entrarButton = findViewById(R.id.btAtualizar);
        TextView criarContaTextView = findViewById(R.id.criarConta);

        mudarViewSenha = findViewById(R.id.mudarviewsenha);
        mudarViewSenha.setOnClickListener(view -> togglePasswordVisibility());

        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class); // Instância única do serviço

        entrarButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!validateInputs(username, password)) return;

            Toast.makeText(LoginActivity.this, "Entrando...", Toast.LENGTH_LONG).show();

            loginUser(delfisApiService, username, password);
        });

        criarContaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos antes de continuar.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void loginUser(DelfisApiService delfisApiService, String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = delfisApiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    if (token != null && !token.isEmpty()) {
                        fetchUserData(delfisApiService, token, username);
                    } else {
                        showError("Autenticação falhou. Tente novamente.");
                    }
                } else {
                    showError("Credenciais incorretas. Verifique seu username e senha.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                showError("Falha ao conectar com o servidor.");
                Log.e(TAG, "Erro na conexão durante o login.", t);
            }
        });
    }

    private void fetchUserData(DelfisApiService delfisApiService, String token, String username) {
        Call<User> userCall = delfisApiService.getUserByUsername(token, username);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    user.setToken(token);

                    // user -> streak -> powerup -> theme -> session -> home
                    fetchStreakData(delfisApiService, token, user);
                } else {
                    showError("Erro ao carregar suas informações.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                showError("Erro de conexão ao carregar seus dados.");
                Log.e(TAG, "Erro ao conectar para recuperar dados do usuário.", t);
            }
        });
    }

    private void fetchStreakData(DelfisApiService delfisApiService, String token, User user) {
        Call<Streak> streakCall = delfisApiService.getCurrentStreakByUser(token, user.getId());
        streakCall.enqueue(new Callback<Streak>() {
            @Override
            public void onResponse(@NonNull Call<Streak> call, @NonNull Response<Streak> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setCurrentStreak(response.body());
                } else {
                    createNewStreak(delfisApiService, token, user);
                }

                fetchPowerupData(delfisApiService, token, user);
            }

            @Override
            public void onFailure(@NonNull Call<Streak> call, @NonNull Throwable t) {
                showError("Erro de conexão ao buscar streak.");
                Log.e(TAG, "Erro na conexão ao buscar streak.", t);
            }
        });
    }

    private void createNewStreak(DelfisApiService delfisApiService, String token, User user) {
        StreakRequest streakRequest = new StreakRequest(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), user.getId());
        Call<Streak> streakInsertCall = delfisApiService.createStreak(token, streakRequest);
        streakInsertCall.enqueue(new Callback<Streak>() {
            @Override
            public void onResponse(Call<Streak> call, Response<Streak> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setCurrentStreak(response.body());
                } else {
                    showError("Erro ao criar streak.");
                }
            }

            @Override
            public void onFailure(Call<Streak> call, Throwable t) {
                showError("Erro de conexão ao criar streak.");
            }
        });
    }


    private void fetchPowerupData(DelfisApiService delfisApiService, String token, User user) {
        Call<List<Powerup>> call = delfisApiService.getPowerupsByAppUserId(token, user.getId());
        call.enqueue(new Callback<List<Powerup>>() {
            @Override
            public void onResponse(@NonNull Call<List<Powerup>> call, @NonNull Response<List<Powerup>> response) {
                user.setPowerups(response.body());
                fetchThemeData(delfisApiService, token, user);
            }

            @Override
            public void onFailure(@NonNull Call<List<Powerup>> call, @NonNull Throwable t) {
                showError("Não foi possível carregar powerups. Tente novamente mais tarde.");
                Log.d(TAG, "Erro ao recuperar powerups: " + t.getMessage());
            }
        });
    }

    private void fetchThemeData(DelfisApiService delfisApiService, String token, User user) {
        Call<List<Theme>> call = delfisApiService.getThemesByAppUserId(token, user.getId());
        call.enqueue(new Callback<List<Theme>>() {
            @Override
            public void onResponse(@NonNull Call<List<Theme>> call, @NonNull Response<List<Theme>> response) {
                user.setThemes(response.body());
                startSession(delfisApiService, token, user);
            }

            @Override
            public void onFailure(@NonNull Call<List<Theme>> call, @NonNull Throwable t) {
                showError("Não foi possível carregar temas. Tente novamente mais tarde.");
                Log.d(TAG, "Erro ao recuperar temas: " + t.getMessage());
            }
        });
    }

    private void startSession(DelfisApiService delfisApiService, String token, User user) {
        Call<Session> sessionCall = delfisApiService.startSession(token, user.getId());
        sessionCall.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(@NonNull Call<Session> call, @NonNull Response<Session> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setCurrentSession(response.body());
                    goToHome(user);
                } else {
                    showError("Falha ao carregar sua sessão.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Session> call, @NonNull Throwable t) {
                showError("Não foi possível iniciar sua sessão.");
                Log.e(TAG, "Erro ao recuperar sessão.", t);
            }
        });
    }

    private void goToHome(User user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mudarViewSenha.setImageResource(R.drawable.olhofechado);
            isPasswordVisible = false;
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mudarViewSenha.setImageResource(R.drawable.olhoaberto);
            isPasswordVisible = true;
        }

        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
