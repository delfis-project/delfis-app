package goldenage.delfis.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.navbar.ConfigActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditInfoActivity extends AppCompatActivity {
    private User user;
    private EditText editTextNome, editTextSenha, editTextData, editTextUsername, editTextEmail;
    private Button btAtualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        user = (User) getIntent().getSerializableExtra("user");

        editTextNome = findViewById(R.id.editTextNome);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextData = findViewById(R.id.editTextData);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        btAtualizar = findViewById(R.id.btAtualizar);

        setFormatterDate();

        editTextNome.setText(user.getName());
        editTextUsername.setText(user.getUsername());
        editTextEmail.setText(user.getEmail());

        DateTimeFormatter apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = LocalDate.parse(user.getBirthDate(), apiFormatter).format(outFormatter);
        editTextData.setText(dataFormatada);

        btAtualizar.setOnClickListener(v -> {
            String name = editTextNome.getText().toString().trim();
            String username = editTextUsername.getText().toString().trim();
            String password = editTextSenha.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String birthDate = editTextData.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu nome.", Toast.LENGTH_LONG).show();
                editTextNome.requestFocus();
                return;
            }
            if (username.isEmpty()) {
                Toast.makeText(this, "Por favor, escolha um nome de usuário.", Toast.LENGTH_LONG).show();
                editTextUsername.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu e-mail.", Toast.LENGTH_LONG).show();
                editTextEmail.requestFocus();
                return;
            }
            if (birthDate.isEmpty()) {
                Toast.makeText(this, "Por favor, insira sua data de nascimento.", Toast.LENGTH_LONG).show();
                editTextData.requestFocus();
                return;
            }

            Toast.makeText(this, "Aguarde...", Toast.LENGTH_LONG).show();

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("username", username);
            updates.put("email", email);
            if (!password.isEmpty())
                updates.put("password", password);

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            birthDate = LocalDate.parse(birthDate, inputFormatter).format(outputFormatter);
            updates.put("birthDate", birthDate);

            DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
            Call<User> call = delfisApiService.updateUserPartially(user.getToken(), user.getId(), updates);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User updatedUser = response.body();
                        if (updatedUser != null) {
                            user.setName(updatedUser.getName());
                            user.setUsername(updatedUser.getUsername());
                            user.setEmail(updatedUser.getEmail());
                            user.setPassword(updatedUser.getPassword());
                            user.setBirthDate(updatedUser.getBirthDate());

                            Intent intent = new Intent(EditInfoActivity.this, ConfigActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                            Toast.makeText(EditInfoActivity.this, "Atualização realizada com sucesso!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            if (response.code() == 400) {
                                String errorBody = response.errorBody().string();
                                JSONObject errorJson = new JSONObject(errorBody);
                                if (errorJson.has("name")) {
                                    String nameError = errorJson.getString("name");
                                    editTextNome.setError(nameError);
                                }
                                if (errorJson.has("email")) {
                                    String emailError = errorJson.getString("email");
                                    editTextEmail.setError(emailError);
                                }
                                if (errorJson.has("username")) {
                                    String usernameError = errorJson.getString("username");
                                    editTextUsername.setError(usernameError);
                                }
                                if (errorJson.has("password")) {
                                    String passwordError = errorJson.getString("password");
                                    editTextSenha.setError(passwordError);
                                }
                                if (errorJson.has("birthDate")) {
                                    String birthDateError = errorJson.getString("birthDate");
                                    editTextData.setError(birthDateError);
                                }
                            } else if (response.code() == 409) {
                                Toast.makeText(EditInfoActivity.this, "Desculpe, já existe uma conta com esse nome de usuário ou e-mail. Tente outro.", Toast.LENGTH_LONG).show();
                            } else {
                                System.out.println(response.code());
                                Toast.makeText(EditInfoActivity.this, "Ocorreu um erro ao atualizar sua conta. Tente novamente.", Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditInfoActivity.this, "Ocorreu um erro inesperado. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(EditInfoActivity.this, "Falha na conexão com a API.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void setFormatterDate() {
        editTextData.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    return;
                }
                String currentText = s.toString();
                String cleanInput = currentText.replaceAll("[^\\d]", "");
                if (before == 1 && currentText.length() < previousText.length()) {
                    isUpdating = true;
                    editTextData.setText(cleanInput);
                    editTextData.setSelection(cleanInput.length());
                    isUpdating = false;
                    return;
                }
                if (cleanInput.length() > 8) {
                    cleanInput = cleanInput.substring(0, 8);
                }
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
                editTextData.setText(formatted.toString());
                editTextData.setSelection(formatted.length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}