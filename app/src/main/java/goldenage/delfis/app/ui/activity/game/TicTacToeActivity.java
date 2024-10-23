package goldenage.delfis.app.ui.activity.game;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import goldenage.delfis.app.BuildConfig;
import goldenage.delfis.app.R;
import goldenage.delfis.app.ui.activity.navbar.HomeActivity;
import goldenage.delfis.app.model.client.GptClient;
import goldenage.delfis.app.model.response.User;

import android.content.Intent;
import android.widget.Toast;

public class TicTacToeActivity extends AppCompatActivity {
    private int jogada;
    private char[][] jogo;
    private final Random random = new Random();
    private Handler handler;
    private HashMap<String, Integer> mapPosicoes;
    private List<Integer> botoes;
    private static final char PLAYER_X = 'x';
    private static final char PLAYER_O = 'o';
    private static final String API_URL = BuildConfig.GPT_API_URL;
    private static final String API_KEY = BuildConfig.GPT_API_KEY;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        user = (User) getIntent().getSerializableExtra("user");
        jogo = new char[3][3];
        jogada = 0;
        handler = new Handler();
        botoes = Arrays.asList(
                R.id.a00,
                R.id.a01,
                R.id.a02,
                R.id.a10,
                R.id.a11,
                R.id.a12,
                R.id.a20,
                R.id.a21,
                R.id.a22
        );

        mapPosicoes = new HashMap<>();
        mapPosicoes.put("a00", R.id.a00);
        mapPosicoes.put("a01", R.id.a01);
        mapPosicoes.put("a02", R.id.a02);
        mapPosicoes.put("a10", R.id.a10);
        mapPosicoes.put("a11", R.id.a11);
        mapPosicoes.put("a12", R.id.a12);
        mapPosicoes.put("a20", R.id.a20);
        mapPosicoes.put("a21", R.id.a21);
        mapPosicoes.put("a22", R.id.a22);

        for (int botao : botoes) {
            findViewById(botao).setOnClickListener(this::colocarJogada);
        }
    }

    private boolean verificarLinhaOuColuna(char a, char b, char c) {
        return (a == b && b == c && a != '\u0000');
    }

    public boolean verificarVitoria() {
        for (int i = 0; i < 3; i++) {
            if (verificarLinhaOuColuna(jogo[i][0], jogo[i][1], jogo[i][2]) || verificarLinhaOuColuna(jogo[0][i], jogo[1][i], jogo[2][i])) {
                return true;
            }
        }
        return verificarLinhaOuColuna(jogo[0][0], jogo[1][1], jogo[2][2]) || verificarLinhaOuColuna(jogo[0][2], jogo[1][1], jogo[2][0]);
    }

    public void limparJogo() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        jogo = new char[3][3];
        jogada = 0;

        for (int botao : botoes) {
            ((ImageView) findViewById(botao)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ((TextView) findViewById(R.id.resultado)).setText("");
    }

    public void verificarSeAcabou() {
        if (verificarVitoria()) {
            String resultado;
            if (jogada % 2 != 0) {
                resultado = "Vitória!";
                GameUtil.payUser(TicTacToeActivity.this, user);
            } else {
                resultado = "Derrota :(";
            }

            ((TextView) findViewById(R.id.resultado)).setText(resultado);
            handler.postDelayed(() -> {
                Intent intent = new Intent(TicTacToeActivity.this, HomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }, 2000);
        } else if (jogada == 9) {
            Toast.makeText(this, "Empate! Reiniciando...", Toast.LENGTH_LONG).show();
            limparJogo();
        }
    }

    private void habilitarBotoes(boolean habilitar) {
        for (int botao : botoes) {
            findViewById(botao).setEnabled(habilitar);
        }
    }

    public void colocarJogada(View view) {
        String id = view.getResources().getResourceEntryName(view.getId()).replace("a", "");
        int i = Integer.parseInt(String.valueOf(id.charAt(0)));
        int j = Integer.parseInt(String.valueOf(id.charAt(1)));

        if (jogo[i][j] == '\u0000') {
            ((ImageView) findViewById(mapPosicoes.get(String.format("a%d%d", i, j)))).setImageResource(R.drawable.x_velha);
            jogo[i][j] = PLAYER_X;
            jogada++;

            verificarSeAcabou();

            if (!verificarVitoria()) {
                habilitarBotoes(false);

                setPosicaoGpt();
            }
        }
    }

    public void setPosicaoGpt() {
        StringBuilder jogoStr = new StringBuilder();
        for (char[] chars : jogo) {
            jogoStr.append(Arrays.toString(chars));
        }

        GptClient gptClient = new GptClient(API_URL, API_KEY);
        gptClient.enviarJogadaGpt(jogoStr.toString(), new GptClient.GPTCallback() {
            @Override
            public void onSuccess(int i, int j) {
                if (mapPosicoes.containsKey(String.format("a%d%d", i, j))) {
                    if (jogo[i][j] == '\u0000') {
                        ((ImageView) findViewById(mapPosicoes.get(String.format("a%d%d", i, j)))).setImageResource(R.drawable.o);
                        jogo[i][j] = PLAYER_O;
                        jogada++;
                        verificarSeAcabou();
                        Log.d("GptClient", "gpt green");
                    } else {
                        fazerJogadaAutomatica();
                    }
                } else {
                    fazerJogadaAutomatica();
                }

                habilitarBotoes(true);
            }

            @Override
            public void onFailure(Throwable t) {
                fazerJogadaAutomatica();
                Log.e("GptClient", t.getMessage());
                habilitarBotoes(true);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void fazerJogadaAutomatica() {
        ImageView posicao;
        int i, j;
        do {
            i = random.nextInt(3);
            j = random.nextInt(3);
        } while (jogo[i][j] != '\u0000');

        posicao = findViewById(mapPosicoes.get(String.format("a%d%d", i, j)));
        posicao.setImageResource(R.drawable.o);
        jogo[i][j] = PLAYER_O;
        jogada++;
        verificarSeAcabou();

        habilitarBotoes(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TicTacToeActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}