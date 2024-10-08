//package goldenage.delfis.app.activity.game;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Random;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import goldenage.delfis.app.R;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class TicTacToeActivity extends AppCompatActivity {
//    private int jogada;
//    private char[][] jogo;
//    private final Random random = new Random();
//    private Handler handler;
//    // 0: amigo
//    // 1: android
//    // 2: gpt
//    private int tipoJogo;
//    private HashMap<String, Integer> mapPosicoes;
//    private String jogador1;
//    private String jogador2;
//    static private String apiUrl;
//    static private String apiKey;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tic_tac_toe);
//
//        // Iniciando variáveis ao criar a tela
//        jogo = new char[3][3];
//        jogada = 0;
//        handler = new Handler();
//        mapPosicoes = new HashMap<>();
//        mapPosicoes.put("a00", R.id.a00);
//        mapPosicoes.put("a01", R.id.a01);
//        mapPosicoes.put("a02", R.id.a02);
//        mapPosicoes.put("a10", R.id.a10);
//        mapPosicoes.put("a11", R.id.a11);
//        mapPosicoes.put("a12", R.id.a12);
//        mapPosicoes.put("a20", R.id.a20);
//        mapPosicoes.put("a21", R.id.a21);
//        mapPosicoes.put("a22", R.id.a22);
//        Intent intent = getIntent();
//        if (intent != null && intent.getExtras() != null) {
//            tipoJogo = intent.getExtras().getInt("tipo");
//            jogador1 = intent.getExtras().getString("jogador1");
//            jogador2 = intent.getExtras().getString("jogador2");
//        }
//    }
//
//    public boolean verificarVitoria() {
//        /*
//         * Percorre a matriz e retorna um boolean indicando se o jogo acabou ou não.
//         */
//
//        // laço que verifica linhas e colunas pra ver se alguém ganhou
//        for (int i = 0; i < jogo.length; i++) {
//            if ((jogo[i][0] == jogo[i][1] && jogo[i][1] == jogo[i][2] && (jogo[i][0] == 'x' || jogo[i][0] == 'o')) ||
//                    (jogo[0][i] == jogo[1][i] && jogo[1][i] == jogo[2][i] && (jogo[0][i] == 'x' || jogo[0][i] == 'o'))) {
//                return true;
//            }
//        }
//
//        // verifica as diagonais
//        return ((jogo[0][0] == jogo[1][1] && jogo[1][1] == jogo[2][2]) && (jogo[0][0] == 'x' || jogo[0][0] == 'o'))
//                || ((jogo[0][2] == jogo[1][1] && jogo[1][1] == jogo[2][0]) && (jogo[0][2] == 'x' || jogo[0][2] == 'o'));
//    }
//
//    public void limparJogo() {
//        /*
//         * Limpa o jogo, reiniciando todas as variáveis e jogadas da rodada passada.
//         */
//
//        // limpa a matriz
//        jogo = new char[3][3];
//
//        // reinicia contador de jogada
//        jogada = 0;
//
//        // limpa o tabuleiro
//        ((ImageView) findViewById(R.id.a00)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a01)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a02)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a10)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a11)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a12)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a20)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a21)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//        ((ImageView) findViewById(R.id.a22)).setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        // seta o texto como vazio
//        ((TextView) findViewById(R.id.resultado)).setText(" ");
//
//        Intent intent = new Intent(this, Menu.class);
//        startActivity(intent);
//    }
//
//    public void verificarSeAcabou(int jogada) {
//        // verifica se algúem ganhou
//        if (verificarVitoria()) {
//            if (jogada % 2 != 0) {
//                // seta o texto como vitória
//                ((TextView) findViewById(R.id.resultado)).setText(String.format("Vitória de %s!!!!", jogador1));
//            } else {
//                // seta o texto como vitória
//                ((TextView) findViewById(R.id.resultado)).setText(String.format("Vitória de %s!!!!", jogador2));
//            }
//
//            // seta jogada como 9, para sinalizar que acabou o jogo
//            jogada = 9;
//        }
//
//        // verifica ninguém ganhou e a jogada é 9
//        else if (jogada == 9) {
//
//            // seta o texto como empate
//            ((TextView) findViewById(R.id.resultado)).setText("Empate...");
//        }
//
//        // verifica se o jogo acabou e reinicia
//        if (jogada == 9) {
//
//            handler.postDelayed(() -> {
//
//                // seta o texto como reiniciando com delay
//                ((TextView) findViewById(R.id.resultado)).setText("Reiniciando!!");
//
//                // limpa o jogo com delay
//                handler.postDelayed(this::limparJogo, 2000);
//
//            }, 2000);
//        }
//    }
//
//    public void colocarJogada(View view) throws InterruptedException, JSONException, IOException {
//        /*
//         * Recebe uma view com a posição jogada.
//         * Adiciona foto da jogada na tela principal e verifica se alguém ganhou.
//         */
//
//        // pega a jogada
//        ImageView posicao = (ImageView) view;
//
//        // pega o id da jogada
//        String id = view.getResources().getResourceEntryName(posicao.getId()).replace("a", "");
//
//        // inicializando index da matriz
//        int i = Integer.parseInt(String.valueOf(id.charAt(0)));
//        int j = Integer.parseInt(String.valueOf(id.charAt(1)));
//
//        // verifica se a posição da jogada é nula na matriz
//        if (jogo[i][j] == '\u0000') {
//
//            // se a jogada for par, é jogada do X
//            if (jogada % 2 == 0) {
//
//                // seta a foto como a foto do X
//                posicao.setImageResource(R.drawable.x);
//
//                // preenche a posição com X na matriz
//                jogo[i][j] = 'x';
//                jogada++;
//            }
//
//            // se a jogada for ímpar, é jogada do O
//            else {
//                if (tipoJogo == 0) {
//                    // seta a foto como a foto do O
//                    posicao.setImageResource(R.drawable.o);
//
//                    // preenche a posição com O na matriz
//                    jogo[i][j] = 'o';
//                    jogada++;
//                }
//            }
//
//            verificarSeAcabou(jogada);
//            if (tipoJogo == 1) {
//                fazerJogadaAutomatica();
//                Thread.sleep(500);
//                jogada++;
//            } else if (tipoJogo == 2) {
//                setPosicaoGPT();
//                jogada++;
//            }
//            verificarSeAcabou(jogada);
//        }
//    }
//
//    public void fazerJogadaAutomatica() {
//        ImageView posicao;
//        int i;
//        int j;
//        do {
//            i = random.nextInt(3);
//            j = random.nextInt(3);
//        } while (jogo[i][j] != '\u0000');
//
//        // pegando posição
//        posicao = findViewById(mapPosicoes.get(String.format("a%d%d", i, j)));
//
//        // seta a foto como a foto do O
//        posicao.setImageResource(R.drawable.o);
//
//        // preenche a posição com O na matriz
//        jogo[i][j] = 'o';
//    }
//
//    public void setPosicaoGPT() throws JSONException {
//        String jogoStr = "";
//        for (int i = 0; i < jogo.length; i++) {
//            jogoStr += Arrays.toString(jogo[i]);
//        }
//        String prompt = String.format("Tenho essa matriz que representa um jogo da velha em Java: %s\nO caracter 'o' representa a bolinha. O caracter 'x' representa o x. O resto são posições que você pode jogar.\nVocê é a bolinha. Faça uma jogada. Represente colocando a sua resposta nesse formato:\ni: posição j: posição", jogoStr);
//
//        JSONObject system = new JSONObject();
//        system.put("role", "system");
//        system.put("content", "Você deve fazer uma jogada em um jogo da velha.");
//
//        JSONObject user = new JSONObject();
//        user.put("role", "user");
//        user.put("content", prompt);
//
//        JSONArray messagesArray = new JSONArray();
//        messagesArray.put(system);
//        messagesArray.put(user);
//
//        JSONObject requestJSON = new JSONObject();
//        requestJSON.put("model", "gpt-3.5-turbo");
//        requestJSON.put("messages", messagesArray);
//
//        RequestBody body = RequestBody.create(requestJSON.toString(), MediaType.get("application/json"));
//        Request request = new Request.Builder()
//                .url(apiUrl)
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + apiKey)
//                .post(body)
//                .build();
//
//        OkHttpClient client = new OkHttpClient();
//        Log.d("api-gpt", requestJSON.toString());
//
//        final int[] posicoesGPT = {-1, -1};
//        Callback callback = new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.e("api-gpt", e.getMessage());
//                try {
//                    setPosicaoGPT();
//                } catch (JSONException ex) {
//                    Log.e("api-gpt", e.getMessage());
//                    fazerJogadaAutomatica();
//                }
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                try {
//                    JSONObject json = new JSONObject(response.body().string());
//                    JSONArray choices = json.getJSONArray("choices");
//                    JSONObject choice = choices.getJSONObject(0);
//                    JSONObject message = choice.getJSONObject("message");
//                    String content = message.getString("content");
//                    int i = Integer.parseInt(content.split("i: ")[1].split("j")[0].replace(" ", ""));
//                    int j = Integer.parseInt(content.split("j: ")[1].replace(" ", ""));
//                    Log.d("api-gpt", content);
//                    Log.d("api-gpt", String.valueOf(i));
//                    Log.d("api-gpt", String.valueOf(j));
//                    posicoesGPT[0] = i;
//                    posicoesGPT[1] = j;
//                } catch (JSONException ignored) {
//                }
//            }
//        };
//
//        client.newCall(request).enqueue(callback);
//
//        while (true) {
//            try {
//                ImageView posicao = findViewById(mapPosicoes.get(String.format("a%d%d", posicoesGPT[0], posicoesGPT[1])));
//                posicao.setImageResource(R.drawable.o);
//                jogo[posicoesGPT[0]][posicoesGPT[1]] = 'o';
//                break;
//            } catch (NullPointerException ignored) {
//            }
//        }
//    }
//}