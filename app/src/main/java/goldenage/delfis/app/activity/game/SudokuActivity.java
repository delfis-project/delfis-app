package goldenage.delfis.app.activity.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.Sudoku;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SudokuActivity extends AppCompatActivity {
    private User user;
    private Button btCheck;
    private final int BOARD_HEIGHT = 6;
    private final int BOARD_WIDTH = 6;
    private final int[][] sudokuBoard = new int[BOARD_WIDTH][BOARD_HEIGHT];
    private final Button[][] buttons = new Button[BOARD_WIDTH][BOARD_HEIGHT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        user = (User) getIntent().getSerializableExtra("user");
        btCheck = findViewById(R.id.btCheck);

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                String buttonID = "btn_" + i + "_" + j;

                @SuppressLint("DiscouragedApi")
                int resId = getResources().getIdentifier(buttonID, "id", getPackageName());

                buttons[i][j] = findViewById(resId);
                int finalJ = j;
                int finalI = i;

                buttons[i][j].setOnClickListener(v -> {
                    int currentValue = Integer.parseInt(buttons[finalI][finalJ].getText().toString());
                    currentValue = (currentValue + 1) % 7;
                    if (currentValue == 0) {
                        currentValue = 1;
                    }
                    buttons[finalI][finalJ].setText(String.valueOf(currentValue));
                    sudokuBoard[finalI][finalJ] = currentValue;
                });
            }
        }

        Toast.makeText(SudokuActivity.this, "Carregando sudoku...", Toast.LENGTH_LONG).show();
        fetchSudokuBoard();

        btCheck.setOnClickListener(v -> checkSudokuAnswers());
    }

    private void checkSudokuAnswers() {
        if (isSudokuValid(sudokuBoard)) {
            Toast.makeText(SudokuActivity.this, "Parabéns! Todas as respostas estão corretas!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(SudokuActivity.this, HomeActivity.class);
            if (user != null)
                intent.putExtra("user", user);

            startActivity(intent);
            
        } else {
            Toast.makeText(SudokuActivity.this, "Algumas respostas estão incorretas. Tente novamente!", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchSudokuBoard() {
        DelfisApiService delfisApiService = RetrofitFactory.getClient().create(DelfisApiService.class);
        Call<Sudoku> call = delfisApiService.generateSudokuBoard(user.getToken());

        call.enqueue(new Callback<Sudoku>() {
            @Override
            public void onResponse(@NonNull Call<Sudoku> call, @NonNull Response<Sudoku> response) {
                if (response.isSuccessful()) {
                    Sudoku sudoku = response.body();
                    populateSudokuGrid(sudoku.getBoard());
                } else {
                    Toast.makeText(SudokuActivity.this, "Falha ao carregar o Sudoku", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SudokuActivity.this, HomeActivity.class);
                    if (user != null)
                        intent.putExtra("user", user);

                    startActivity(intent);
                    
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sudoku> call, @NonNull Throwable t) {
                Toast.makeText(SudokuActivity.this, "Erro ao conectar ao servidor. Verifique sua conexão.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SudokuActivity.this, HomeActivity.class);
                if (user != null)
                    intent.putExtra("user", user);

                startActivity(intent);
                
            }
        });
    }

    private boolean isSudokuValid(int[][] board) {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            boolean[] rowCheck = new boolean[7];
            boolean[] colCheck = new boolean[7];

            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] != 0) {
                    if (rowCheck[board[i][j]]) {
                        return false;
                    }
                    rowCheck[board[i][j]] = true;
                }

                if (board[j][i] != 0) {
                    if (colCheck[board[j][i]]) {
                        return false;
                    }
                    colCheck[board[j][i]] = true;
                }
            }
        }

        return true;
    }

    private void populateSudokuGrid(List<List<String>> board) {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                String value = board.get(i).get(j);
                Button button = buttons[i][j];
                if (!value.isEmpty()) {
                    button.setText(value);
                    button.setEnabled(false);
                    button.setBackgroundColor(getResources().getColor(R.color.delfisBackground));
                    sudokuBoard[i][j] = Integer.parseInt(value);
                } else {
                    button.setText("0");
                    button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    sudokuBoard[i][j] = 0;
                }
            }
        }
    }
}