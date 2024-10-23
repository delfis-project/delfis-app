package goldenage.delfis.app.ui.activity.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.HomeActivity;

public class MathChallengesActivity extends AppCompatActivity {
    private User user;
    private static final int NUM_CONTAS = 3;
    private static final Random random = new Random();
    private static final int[] RESPOSTAS = new int[NUM_CONTAS];
    private ImageView btVoltar, btChecar;

    private static final String SOMA = "+";
    private static final String SUBTRACAO = "-";
    private static final String MULTIPLICACAO = "x";
    private static final String DIVISAO = "/";
    private static final String[] OPERACOES = {SOMA, SUBTRACAO, MULTIPLICACAO, DIVISAO};


    private TextView
            textOperacaoConta1,
            textOperacaoConta2,
            textOperacaoConta3,
            textNum1Conta1,
            textNum2Conta1,
            textNum1Conta2,
            textNum2Conta2,
            textNum1Conta3,
            textNum2Conta3;
    private static TextView[] TEXT_OPERACOES;
    private static TextView[] TEXT_NUMS;

    private EditText editTextRespostaConta1, editTextRespostaConta2, editTextRespostaConta3;
    private static EditText[] EDIT_TEXT_RESPOSTAS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_challenges);

        user = (User) getIntent().getSerializableExtra("user");
        textOperacaoConta1 = findViewById(R.id.textOperacaoConta1);
        textOperacaoConta2 = findViewById(R.id.textOperacaoConta2);
        textOperacaoConta3 = findViewById(R.id.textOperacaoConta3);
        textNum1Conta1 = findViewById(R.id.textNum1Conta1);
        textNum2Conta1 = findViewById(R.id.textNum2Conta1);
        textNum1Conta2 = findViewById(R.id.textNum1Conta2);
        textNum2Conta2 = findViewById(R.id.textNum2Conta2);
        textNum1Conta3 = findViewById(R.id.textNum1Conta3);
        textNum2Conta3 = findViewById(R.id.textNum2Conta3);
        editTextRespostaConta1 = findViewById(R.id.editTextRespostaConta1);
        editTextRespostaConta2 = findViewById(R.id.editTextRespostaConta2);
        editTextRespostaConta3 = findViewById(R.id.editTextRespostaConta3);
        btVoltar = findViewById(R.id.btVoltar);
        btChecar = findViewById(R.id.btChecar);

        // sorteando operações
        TEXT_OPERACOES = new TextView[]{textOperacaoConta1, textOperacaoConta2, textOperacaoConta3};
        for (TextView textOperacao : TEXT_OPERACOES) {
            int operacao = random.nextInt(OPERACOES.length);
            textOperacao.setText(OPERACOES[operacao]);
        }

        // gerando números das contas
        TEXT_NUMS = new TextView[]{
                textNum1Conta1,
                textNum2Conta1,
                textNum1Conta2,
                textNum2Conta2,
                textNum1Conta3,
                textNum2Conta3
        };
        for (int i = 0; i < TEXT_NUMS.length; i++) {
            String operacao;
            if (i < 2) {
                operacao = TEXT_OPERACOES[0].getText().toString();
            } else if (i < 4) {
                operacao = TEXT_OPERACOES[1].getText().toString();
            } else {
                operacao = TEXT_OPERACOES[2].getText().toString();
            }

            switch (operacao) {
                case SOMA:
                case SUBTRACAO:
                    TEXT_NUMS[i].setText(String.valueOf(random.nextInt(150)));
                    break;
                case MULTIPLICACAO:
                    TEXT_NUMS[i].setText(String.valueOf(random.nextInt(15)));
                    break;
                case DIVISAO:
                    if (i % 2 == 0) {
                        int denominador = random.nextInt(14) + 1;
                        int numerador = denominador * (random.nextInt(9) + 1);
                        TEXT_NUMS[i].setText(String.valueOf(numerador));
                        TEXT_NUMS[i + 1].setText(String.valueOf(denominador));
                        i++;
                    }
                    break;
            }
        }

        // gerando respostas
        for (int i = 0; i < NUM_CONTAS; i++) {
            String operacao = TEXT_OPERACOES[i].getText().toString();
            int num1 = Integer.parseInt(TEXT_NUMS[i * 2].getText().toString());
            int num2 = Integer.parseInt(TEXT_NUMS[i * 2 + 1].getText().toString());

            switch (operacao) {
                case SOMA:
                    RESPOSTAS[i] = num1 + num2;
                    break;
                case SUBTRACAO:
                    RESPOSTAS[i] = num1 - num2;
                    break;
                case MULTIPLICACAO:
                    RESPOSTAS[i] = num1 * num2;
                    break;
                case DIVISAO:
                    RESPOSTAS[i] = num1 / num2;
                    break;
            }
        }

        btChecar.setOnClickListener(v -> {
            EDIT_TEXT_RESPOSTAS = new EditText[]{editTextRespostaConta1, editTextRespostaConta2, editTextRespostaConta3};

            boolean haveErrors = false;
            for (int i = 0; i < NUM_CONTAS; i++) {
                int resposta = Integer.parseInt(EDIT_TEXT_RESPOSTAS[i].getText().toString());
                if (resposta != RESPOSTAS[i]) {
                    Toast.makeText(MathChallengesActivity.this, "Há alguma conta errada! Verifique e tente novamente!", Toast.LENGTH_LONG).show();
                    haveErrors = true;
                    break;
                }
            }

            if (!haveErrors) {
                Toast.makeText(MathChallengesActivity.this, "Correto! Parabéns!", Toast.LENGTH_LONG).show();
                GameUtil.payUser(MathChallengesActivity.this, user);

                Intent intent = new Intent(MathChallengesActivity.this, HomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });

        btVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(MathChallengesActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MathChallengesActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}