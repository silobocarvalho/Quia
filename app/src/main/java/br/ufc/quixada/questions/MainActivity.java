package br.ufc.quixada.questions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import br.ufc.quixada.questions.requests.ResponseCallback;
import br.ufc.quixada.questions.requests.SimpleService;


public class MainActivity extends AppCompatActivity {

    Button btnIniciar;
    AutoCompleteTextView tvPalavrasChave;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPalavrasChave = findViewById(R.id.tv_palavras_chave);

        progressBar = findViewById(R.id.progress_bar);

        btnIniciar = findViewById(R.id.btn_iniciar);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String palavrasChaves = tvPalavrasChave.getText().toString();
                Log.d("sid-tag", "Palavras chave: " + palavrasChaves);

                progressBar.setVisibility(View.VISIBLE);

                requestQuestions(palavrasChaves);
            }
        });
    }


    private void requestQuestions(String palavrasChaves){
        SimpleService model = new SimpleService();
        String query =
                "Crie um JSON com 5 questões objetivas sobre " + palavrasChaves + "com opções de A a E e a resposta em cada questao de acordo com as regras: " +
                        "As alternativas só devem possuir um valor como resposta correta e devem ser separadas por virgula AND " +
                        "A resposta correta deve estar presente nas alternativas AND " +
                        "A resposta deve conter somente o JSON AND " +
                        "O campo resposta do JSON deve conter a resposta da pergunta que está presente nas alternativas. Exemplo JSON: " +
                        "[" +
                        "{\"enunciado\": \"Qual das seguintes tags HTML é usada para criar um título de nível 1?\", \"resposta\":\"<h1>\", \"alternativas\": \"<h1>,<h2>,<h3>,<h4>,<h5>\"}," +
                        "]";

            /*
            [
            {"enunciado": "Qual das seguintes tags HTML é usada para criar um título de nível 1?","gabarito": "A","alternativas": "<h1>,<h2>,<h3>,<h4>,<h5>"},
            {"enunciado": "Qual das seguintes tags HTML é usada para criar uma lista não ordenada?","gabarito": "C","alternativas": "<ul>,<ol>,<li>,<dl>,<dt>"}
            ]
            * */

        model.getResponse(query, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                Log.d("sid-tag", "Raw response: " + response);

                progressBar.setVisibility(View.GONE);

                SharedPreferences sharedPref = getSharedPreferences("questions", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_questions), response);
                editor.commit(); //pode ser o .commit() tb para salvar logo no disco, de forma sincrona

                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);

            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("sid-tag", throwable.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}