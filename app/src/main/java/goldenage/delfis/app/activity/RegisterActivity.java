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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.request.LoginRequest;
import goldenage.delfis.app.model.response.LoginResponse;
import goldenage.delfis.app.model.request.UserRequest;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import goldenage.delfis.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEditText, usernameEditText, passwordEditText, emailEditText, birthDateEditText;
    private DelfisApiService apiService;
    private final String UNLOGGED_USERNAME = "SEM-LOGIN";
    private final String UNLOGGED_PASSWORD = "SEM-LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.editTextNome);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextSenha);
        emailEditText = findViewById(R.id.editTextEmail);
        birthDateEditText = findViewById(R.id.editTextData);

        Button criarButton = findViewById(R.id.btAtualizar);
        Retrofit retrofit = RetrofitFactory.getClient();
        apiService = retrofit.create(DelfisApiService.class);

        // TextWatcher para formatar a data de nascimento
        birthDateEditText.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString(); // Armazena o texto anterior
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
                        formatted.append("/").append(cleanInput.substring(2, 4));
                        if (len > 4) {
                            formatted.append("/").append(cleanInput.substring(4));
                        }
                    } else {
                        formatted.append("/").append(cleanInput.substring(2));
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
            String name = nameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String birthDate = birthDateEditText.getText().toString().trim();

            // Verifica campos obrigatórios
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu nome.", Toast.LENGTH_LONG).show();
                nameEditText.requestFocus();
                return;
            }
            if (username.isEmpty()) {
                Toast.makeText(this, "Por favor, escolha um nome de usuário.", Toast.LENGTH_LONG).show();
                usernameEditText.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Por favor, crie uma senha.", Toast.LENGTH_LONG).show();
                passwordEditText.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu e-mail.", Toast.LENGTH_LONG).show();
                emailEditText.requestFocus();
                return;
            }
            if (birthDate.isEmpty()) {
                Toast.makeText(this, "Por favor, insira sua data de nascimento.", Toast.LENGTH_LONG).show();
                birthDateEditText.requestFocus();
                return;
            } else {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate localDate = LocalDate.parse(birthDate, inputFormatter);
                if(LocalDate.now().getYear() - localDate.getYear() > 130
                    || localDate.getDayOfMonth() > 31
                    || localDate.getDayOfMonth() < 1
                    || localDate.getMonthValue() > 12
                    || localDate.getMonthValue() < 1) {
                    Toast.makeText(this, "Por favor, insira uma data de nascimento válida.", Toast.LENGTH_LONG).show();
                    birthDateEditText.requestFocus();
                }
            }

            Toast.makeText(this, "Aguarde...", Toast.LENGTH_LONG).show();

            LoginRequest loginRequest = new LoginRequest(UNLOGGED_USERNAME, UNLOGGED_PASSWORD);
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
                                UserRequest userRequest = new UserRequest(name, username, password, email, birthDate);
                                Call<Void> call2 = apiService.createUser(token, userRequest);

                                call2.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call2, @NonNull Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            
                                            Toast.makeText(RegisterActivity.this, "Cadastro realizado com sucesso! Você pode fazer login agora.", Toast.LENGTH_LONG).show();
                                        } else {
                                            try {
                                                if (response.code() == 400) {
                                                    String errorBody = response.errorBody().string();
                                                    System.out.println(errorBody);
                                                    JSONObject errorJson = new JSONObject(errorBody);

                                                    if (errorJson.has("name")) {
                                                        String nameError = errorJson.getString("name");
                                                        nameEditText.setError(nameError);
                                                    }
                                                    if (errorJson.has("email")) {
                                                        String emailError = errorJson.getString("email");
                                                        emailEditText.setError(emailError);
                                                    }
                                                    if (errorJson.has("username")) {
                                                        String usernameError = errorJson.getString("username");
                                                        usernameEditText.setError(usernameError);
                                                    }
                                                    if (errorJson.has("password")) {
                                                        String passwordError = errorJson.getString("password");
                                                        passwordEditText.setError(passwordError);
                                                    }
                                                    if (errorJson.has("birthDate")) {
                                                        String birthDateError = errorJson.getString("birthDate");
                                                        birthDateEditText.setError(birthDateError);
                                                    }
                                                } else if (response.code() == 409) {
                                                    Toast.makeText(RegisterActivity.this,
                                                            "Desculpe, já existe uma conta com esse nome de usuário ou e-mail. Tente outro.",
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Ocorreu um erro ao criar sua conta. Tente novamente.", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (IOException | JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(RegisterActivity.this, "Ocorreu um erro inesperado. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call2, @NonNull Throwable t) {
                                        Toast.makeText(RegisterActivity.this, "Falha na conexão com a API.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Login falhou: token não recebido", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Token não recebido: " + token);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Resposta vazia da API", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Resposta vazia da API");
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Erro na resposta da API: " + response.code(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Erro na resposta da API: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Falha na conexão com a API", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Falha na conexão com a API", t);
                }
            });
        });
    }
}