package br.ufc.quixada.questions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.ufc.quixada.questions.R;
import br.ufc.quixada.questions.requests.ResponseCallback;
import br.ufc.quixada.questions.requests.SimpleService;

public class WrongAnswer extends AppCompatActivity {

    EditText et_justificativa_resposta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answer);

        et_justificativa_resposta = findViewById(R.id.et_justificativa_resposta);

        et_justificativa_resposta.setText("Esta resposta está incorreta pois...");

        Intent intent = getIntent();
        String pergunta = intent.getStringExtra("pergunta");
        String resposta = intent.getStringExtra("resposta");

        requestJustificativa(pergunta, resposta);

    }


    private void requestJustificativa(String pergunta, String resposta){
        SimpleService model = new SimpleService();
        String query =
                "Explique em poucas linhas porque para a pergunta - " + pergunta + " - a resposta - " + resposta + " - não está correta";

        model.getResponse(query, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                Log.d("sid-tag", "Raw response: " + response);
                et_justificativa_resposta.setText(response);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("sid-tag", throwable.getMessage());
            }
        });
    }
}