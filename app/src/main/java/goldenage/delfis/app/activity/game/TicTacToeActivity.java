package goldenage.delfis.app.activity.game;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.client.GptClient;

public class TicTacToeActivity extends AppCompatActivity {
    private int jogada;
    private char[][] jogo;
    private final Random random = new Random();
    private Handler handler;
    private HashMap<String, Integer> mapPosicoes;
    private List<Integer> botoes;
    private static final char PLAYER_X = 'x';
    private static final char PLAYER_O = 'o';
    private final String API_URL = "";
    private final String API_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        // Iniciando variáveis ao criar a tela
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
    }

    private boolean verificarLinhaOuColuna(char a, char b, char c) {
        return (a == b && b == c && (a == PLAYER_X || a == PLAYER_O));
    }

    public boolean verificarVitoria() {
        for (int i = 0; i < jogo.length; i++) {
            if (verificarLinhaOuColuna(jogo[i][0], jogo[i][1], jogo[i][2]) ||
                    verificarLinhaOuColuna(jogo[0][i], jogo[1][i], jogo[2][i])) {
                return true;
            }
        }
        return verificarLinhaOuColuna(jogo[0][0], jogo[1][1], jogo[2][2]) ||
                verificarLinhaOuColuna(jogo[0][2], jogo[1][1], jogo[2][0]);
    }

    public void limparJogo() {
        jogo = new char[3][3];
        jogada = 0;

        for (int botao : botoes) {
            ((ImageView) findViewById(botao)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ((TextView) findViewById(R.id.resultado)).setText(" ");
    }

    public void verificarSeAcabou(int jogada) {
        if (verificarVitoria()) {
            ((TextView) findViewById(R.id.resultado)).setText(jogada % 2 != 0 ? "Vitória!" : "Derrota :(");
            jogada = 9;
        } else if (jogada == 9) {
            ((TextView) findViewById(R.id.resultado)).setText("Empate...");
        }

        if (jogada == 9) {
            handler.postDelayed(() -> {
                ((TextView) findViewById(R.id.resultado)).setText("Reiniciando!");
                handler.postDelayed(this::limparJogo, 2000);
            }, 2000);
        }
    }

    public void colocarJogada(View view) throws InterruptedException, JSONException, IOException {
        ImageView posicao = (ImageView) view;
        String id = view.getResources().getResourceEntryName(posicao.getId()).replace("a", "");
        int i = Integer.parseInt(String.valueOf(id.charAt(0)));
        int j = Integer.parseInt(String.valueOf(id.charAt(1)));

        if (jogo[i][j] == '\u0000') {
            if (jogada % 2 == 0) {
                posicao.setImageResource(R.drawable.x);
                jogo[i][j] = PLAYER_X;
                jogada++;
            }

            verificarSeAcabou(jogada);
            setPosicaoGpt();
            jogada++;
            verificarSeAcabou(jogada);
        }
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
    }

    public void setPosicaoGpt() {
        String jogoStr = "";
        for (char[] chars : jogo) {
            jogoStr += Arrays.toString(chars);
        }

        desabilitarInteracaoTabuleiro();

        GptClient gptClient = new GptClient(API_URL, API_KEY);
        gptClient.enviarJogadaGPT(jogoStr, new GptClient.GPTCallback() {
            @Override
            public void onSuccess(int i, int j) {
                runOnUiThread(() -> {
                    ImageView posicao = findViewById(mapPosicoes.get(String.format("a%d%d", i, j)));
                    posicao.setImageResource(R.drawable.o);
                    jogo[i][j] = PLAYER_O;
                    habilitarInteracaoTabuleiro();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                fazerJogadaAutomatica();
                Log.e("GptClient", t.getMessage());
                habilitarInteracaoTabuleiro();
            }
        });
    }

    private void desabilitarInteracaoTabuleiro() {
        for (int botao : botoes) {
            findViewById(botao).setEnabled(false);
        }
    }

    private void habilitarInteracaoTabuleiro() {
        for (int botao : botoes) {
            findViewById(botao).setEnabled(true);
        }
    }
}