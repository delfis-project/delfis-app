package goldenage.delfis.app.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.api.LoginRequestApi;
import goldenage.delfis.app.model.LoginRequest;
import goldenage.delfis.app.model.LoginResponse;
import goldenage.delfis.app.model.UserRequest;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import goldenage.delfis.app.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, passwordEditText, emailEditText, birthDateEditText;
    private Button criarButton;
    private LoginRequestApi apiService;

    private final String usernameUnlogged = "SEM-LOGIN";
    private final String passwordUnlogged = "SEM-LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.editTextText);
        usernameEditText = findViewById(R.id.editTextTextPassword3);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        emailEditText = findViewById(R.id.editTextTextEmailAddress3);
        birthDateEditText = findViewById(R.id.datas);
        criarButton = findViewById(R.id.entrar);

        Retrofit retrofit = RetrofitClient.getClient();
        apiService = retrofit.create(LoginRequestApi.class);

        // TextWatcher para formatar a data de nascimento
        birthDateEditText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();  // Armazena o texto anterior
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    return;
                }

                String currentText = s.toString();
                String cleanInput = currentText.replaceAll("[^\\d]", ""); // Remove qualquer caractere que não seja número

                // Se o usuário está deletando, permite apagar números e hífens
                if (before == 1 && currentText.length() < previousText.length()) {
                    isUpdating = true;
                    birthDateEditText.setText(cleanInput);
                    birthDateEditText.setSelection(cleanInput.length());
                    isUpdating = false;
                    return;
                }

                // Limita a 8 dígitos
                if (cleanInput.length() > 8) {
                    cleanInput = cleanInput.substring(0, 8);
                }

                // Adiciona os hífens no formato dd-MM-yyyy
                StringBuilder formatted = new StringBuilder();
                int len = cleanInput.length();
                if (len >= 2) {
                    formatted.append(cleanInput.substring(0, 2));
                    if (len >= 4) {
                        formatted.append("-").append(cleanInput.substring(2, 4));
                        if (len > 4) {
                            formatted.append("-").append(cleanInput.substring(4));
                        }
                    } else {
                        formatted.append("-").append(cleanInput.substring(2));
                    }
                } else {
                    formatted.append(cleanInput);
                }

                isUpdating = true;
                birthDateEditText.setText(formatted.toString());
                birthDateEditText.setSelection(formatted.length()); // Move o cursor para o final do texto
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nada a fazer aqui
            }
        });

        criarButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String birthDate = birthDateEditText.getText().toString();

            LoginRequest loginRequest = new LoginRequest(usernameUnlogged, passwordUnlogged);
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
                                UserRequest userRequest = new UserRequest(name, username, password, email, birthDate);
                                Call<Void> call2 = apiService.createUser(token, userRequest);
                                call2.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call2, Response<Void> response) {
                                        System.out.println(response.raw().body());
                                        System.out.println(response.code());
                                        if (response.isSuccessful()) {
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Erro ao criar usuário.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call2, Throwable t) {
                                        Toast.makeText(RegisterActivity.this, "Falha na conexão com a API.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Login falhou: token não recebido", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Token não recebido: " + token);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Resposta vazia da API", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Resposta vazia da API");
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Erro na resposta da API: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Falha na conexão com a API", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Falha na conexão com a API", t);
                }
            });
        });
    }
}