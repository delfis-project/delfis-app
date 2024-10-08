package goldenage.delfis.app.activity.game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.model.response.WordSearch;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordSearchActivity extends AppCompatActivity {
    private final int GRID_SIZE = 10;
    private User user;
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search);

        user = (User) getIntent().getSerializableExtra("user");
        gridLayout = findViewById(R.id.gridLayout);

        gridLayout.post(this::fetchWordSearchData);
    }

    private void fetchWordSearchData() {
        DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
        List<String> list = new ArrayList<>();
        list.add("Calabreso");
        list.add("Davi");

        Call<WordSearch> call = delfisApiService.generateWordSearch(user.getToken(), GRID_SIZE, String.join(",", list));
        call.enqueue(new Callback<WordSearch>() {
            @Override
            public void onResponse(@NonNull Call<WordSearch> call, @NonNull Response<WordSearch> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WordSearch wordSearch = response.body();
                    buildWordSearchGrid(wordSearch);
                } else {
                    Toast.makeText(WordSearchActivity.this, "Falha ao carregar o caça-palavras", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WordSearch> call, @NonNull Throwable t) {
                Toast.makeText(WordSearchActivity.this, "Erro ao conectar ao servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buildWordSearchGrid(WordSearch wordSearch) {
        String[] rows = wordSearch.getGrid().split("\n");
        int size = wordSearch.getGridSize();

        gridLayout.setRowCount(size);
        gridLayout.setColumnCount(size);

        int gridWidth = gridLayout.getWidth();
        int gridHeight = gridLayout.getHeight();
        int buttonSize = Math.min(gridWidth / size, gridHeight / size);

        for (int i = 0; i < size; i++) {
            // Aqui usamos split("") para obter cada letra individualmente na linha
            String[] columns = rows[i].split("");
            for (int j = 0; j < size; j++) {
                Button button = new Button(this);

                if (j < columns.length) {
                    button.setText(columns[j]);
                } else {
                    button.setText("");
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(i, 1f), GridLayout.spec(j, 1f)
                );
                params.width = buttonSize;
                params.height = buttonSize;
                params.setMargins(2, 2, 2, 2);

                button.setLayoutParams(params);

                button.setOnClickListener(new View.OnClickListener() {
                    private boolean isSelected = false;

                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            button.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        } else {
                            button.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        }
                        isSelected = !isSelected;
                    }
                });

                gridLayout.addView(button);
            }
        }
    }
}