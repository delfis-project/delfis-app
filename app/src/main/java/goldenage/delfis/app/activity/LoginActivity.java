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

import goldenage.delfis.app.api.LoginRequestApi;
import goldenage.delfis.app.model.LoginRequest;
import goldenage.delfis.app.model.LoginResponse;
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

            LoginRequestApi loginRequestApi = RetrofitClient.getClient().create(LoginRequestApi.class);
            Call<LoginResponse> call = loginRequestApi.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        if (loginResponse != null) {
                            String token = loginResponse.getToken();
                            if (token != null && !token.isEmpty()) {
                                // Token recebido, redireciona para a atividade Home
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login falhou: token não recebido", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Token não recebido: " + token);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Resposta vazia da API", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Resposta vazia da API");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Erro na resposta da API: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Falha na conexão com a API", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Falha na conexão com a API", t);
                }
            });
        });

        criarContaTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
