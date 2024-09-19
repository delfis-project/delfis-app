package com.example.delfis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import network.ApiService;
import network.LoginRequest;
import network.LoginResponse;
import network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; // Tag para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword2);
        Button entrarButton = findViewById(R.id.entrar);
        TextView criarContaTextView = findViewById(R.id.criarconta);

        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(email, password);

                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                Call<LoginResponse> call = apiService.login(loginRequest);

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse != null) {
                                String token = loginResponse.getToken();
                                if (token != null && !token.isEmpty()) {
                                    // Token recebido, redireciona para a atividade Home
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Login.this, "Login falhou: token não recebido", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Token não recebido: " + token);
                                }
                            } else {
                                Toast.makeText(Login.this, "Resposta vazia da API", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Resposta vazia da API");
                            }
                        } else {
                            Toast.makeText(Login.this, "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Erro na resposta da API: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(Login.this, "Falha na conexão com a API", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Falha na conexão com a API", t);
                    }
                });
            }
        });

        criarContaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
