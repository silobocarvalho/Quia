package br.ufc.quixada.questions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CongratulationsActivity extends AppCompatActivity {

    Button btn_estudarNovamente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);

        btn_estudarNovamente = findViewById(R.id.btn_estudar_novamente);

        btn_estudarNovamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CongratulationsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}