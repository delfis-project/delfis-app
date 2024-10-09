package goldenage.delfis.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import goldenage.delfis.app.R;

public class ErrorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Button backButton = findViewById(R.id.button);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
