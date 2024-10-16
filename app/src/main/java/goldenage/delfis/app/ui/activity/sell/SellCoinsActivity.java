package goldenage.delfis.app.ui.activity.sell;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;

public class SellCoinsActivity extends AppCompatActivity {
    private User user;
    private ImageView btSeta, btCompra1, btCompra2, btCompra3, btCompra4;
    private TextView textMoedas, textMoedas1, textMoedas2, textMoedas3, textMoedas4;
    private TextView textBtCompra1, textBtCompra2, textBtCompra3, textBtCompra4;

    private static TextView[] TEXT_BOTOES;
    private static TextView[] TEXT_QUANTIDADES;
    private static final int[] QUANTIDADES = {50, 100, 150, 200};
    private static final double[] PRECOS = {9.99, 15.99, 19.99, 29.99};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins);

        user = (User) getIntent().getSerializableExtra("user");
        btSeta = findViewById(R.id.btSeta);
        btCompra1 = findViewById(R.id.btCompra1);
        btCompra2 = findViewById(R.id.btCompra2);
        btCompra3 = findViewById(R.id.btCompra3);
        btCompra4 = findViewById(R.id.btCompra4);
        textMoedas = findViewById(R.id.textMoedas);
        textMoedas1 = findViewById(R.id.textMoedas1);
        textMoedas2 = findViewById(R.id.textMoedas2);
        textMoedas3 = findViewById(R.id.textMoedas3);
        textMoedas4 = findViewById(R.id.textMoedas4);
        textBtCompra1 = findViewById(R.id.textBtCompra1);
        textBtCompra2 = findViewById(R.id.textBtCompra2);
        textBtCompra3 = findViewById(R.id.textBtCompra3);
        textBtCompra4 = findViewById(R.id.textBtCompra4);

        textMoedas.setText(String.valueOf(user.getCoins()));

        btSeta.setOnClickListener(v -> {
            Intent intent = new Intent(SellCoinsActivity.this, StoreActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        TEXT_BOTOES = new TextView[]{textBtCompra1, textBtCompra2, textBtCompra3, textBtCompra4};
        TEXT_QUANTIDADES = new TextView[]{textMoedas1, textMoedas2, textMoedas3, textMoedas4};

        for (int i = 0; i < TEXT_BOTOES.length; i++) {
            TEXT_BOTOES[i].setText("Comprar por R$" + String.valueOf(PRECOS[i]).replace('.', ','));
            TEXT_QUANTIDADES[i].setText(String.valueOf(QUANTIDADES[i]));
        }
    }
}