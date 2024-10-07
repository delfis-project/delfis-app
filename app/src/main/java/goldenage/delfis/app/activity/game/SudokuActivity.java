package goldenage.delfis.app.activity.game;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.response.SudokuBoard;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SudokuActivity extends AppCompatActivity {
    private User user;
    private GridLayout sudokuGrid;
    private Button btCheck;
    private final int[][] sudokuBoard = new int[6][6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        user = (User) getIntent().getSerializableExtra("user");
        sudokuGrid = findViewById(R.id.sudokuGrid);
        btCheck = findViewById(R.id.btCheck);

        Toast.makeText(SudokuActivity.this, "Carregando sudoku...", Toast.LENGTH_SHORT).show();
        fetchSudokuBoard();

        btCheck.setOnClickListener(v -> checkSudokuAnswers());
    }

    private void checkSudokuAnswers() {
        if (isSudokuValid(sudokuBoard)) {
            Toast.makeText(SudokuActivity.this, "Parabéns! Todas as respostas estão corretas!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SudokuActivity.this, "Algumas respostas estão incorretas. Tente novamente!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSudokuValid(int[][] board) {
        for (int i = 0; i < 6; i++) {
            boolean[] rowCheck = new boolean[7];
            boolean[] colCheck = new boolean[7];

            for (int j = 0; j < 6; j++) {
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

        for (int blockRow = 0; blockRow < 2; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                boolean[] blockCheck = new boolean[7];

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 3; j++) {
                        int value = board[blockRow * 2 + i][blockCol * 3 + j];
                        if (value != 0) {
                            if (blockCheck[value]) {
                                return false;
                            }
                            blockCheck[value] = true;
                        }
                    }
                }
            }
        }

        return true;
    }

    private void fetchSudokuBoard() {
        DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
        Call<SudokuBoard> call = delfisApiService.generateSudokuBoard(user.getToken());

        call.enqueue(new Callback<SudokuBoard>() {
            @Override
            public void onResponse(@NonNull Call<SudokuBoard> call, @NonNull Response<SudokuBoard> response) {
                if (response.isSuccessful()) {
                    SudokuBoard sudoku = response.body();
                    populateSudokuGrid(sudoku.getBoard());
                } else {
                    Toast.makeText(SudokuActivity.this, "Falha ao carregar o Sudoku", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SudokuBoard> call, @NonNull Throwable t) {
                Toast.makeText(SudokuActivity.this, "Erro ao conectar ao servidor. Verifique sua conexão.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateSudokuGrid(List<List<String>> board) {
        sudokuGrid.removeAllViews();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        int availableWidth = screenWidth - (int) (16 * metrics.density * 2);
        int availableHeight = screenHeight - (int) (16 * metrics.density);

        int buttonSize = Math.min(availableWidth / 6, availableHeight / 6); // Divide pela quantidade de colunas e linhas

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                String value = board.get(i).get(j);
                createSudokuButton(i, j, value, buttonSize);
            }
        }
    }

    private void createSudokuButton(int row, int col, String value, int buttonSize) {
        Button button = new Button(this);
        button.setGravity(Gravity.CENTER);

        if (!value.isEmpty()) {
            button.setText(value);
            button.setEnabled(false);
            sudokuBoard[row][col] = Integer.parseInt(value);
        } else {
            button.setText("0");
            sudokuBoard[row][col] = 0;
        }

        button.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(button.getText().toString());
            currentValue = (currentValue + 1) % 7;
            if (currentValue == 0) {
                currentValue = 1;
            }

            button.setText(String.valueOf(currentValue));
            sudokuBoard[row][col] = currentValue;
        });

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.rowSpec = GridLayout.spec(row);
        param.columnSpec = GridLayout.spec(col);
        param.width = buttonSize;
        param.height = buttonSize;
        button.setLayoutParams(param);

        sudokuGrid.addView(button);
    }
}