package br.ufc.quixada.questions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import br.ufc.quixada.questions.requests.ResponseCallback;
import br.ufc.quixada.questions.requests.SimpleService;
import br.ufc.quixada.questions.utils.FileUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnIniciar;
    AutoCompleteTextView tvPalavrasChave;

    ProgressBar progressBar;

    String palavrasChaves = "";

    String goodJson = "";
    String rawQuery = "";

    String lastResponse = "";

    int counterRequestGeminiRegenerateData = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPalavrasChave = findViewById(R.id.tv_palavras_chave);

        progressBar = findViewById(R.id.progress_bar);

        btnIniciar = findViewById(R.id.btn_iniciar);
        btnIniciar.setOnClickListener(this);

        goodJson = FileUtil.readFileFromRaw(this, R.raw.good_gemini_json);
        rawQuery = FileUtil.readFileFromRaw(this, R.raw.query);
    }


    private void requestQuestions(String query){
        SimpleService model = new SimpleService();
            /*
            [
            {"enunciado": "Qual das seguintes tags HTML é usada para criar um título de nível 1?","gabarito": "A","alternativas": "<h1>,<h2>,<h3>,<h4>,<h5>"},
            {"enunciado": "Qual das seguintes tags HTML é usada para criar uma lista não ordenada?","gabarito": "C","alternativas": "<ul>,<ol>,<li>,<dl>,<dt>"}
            ]
            * */

        String updatedQuery = rawQuery.replace("key-palavra-chave", palavrasChaves);
        Log.d("sid-tag", "Raw Updated QUERY: " + updatedQuery);
        model.getResponse(updatedQuery, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                Log.d("sid-tag", "Raw response: " + response);

                lastResponse = response;

                //validateReceivedData(response);

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

    private void validateReceivedData(String response) {
        counterRequestGeminiRegenerateData++;

        if(counterRequestGeminiRegenerateData > 1){
            Log.d("sid-tag", "Gemini não está entendendo o pedido, insistir vai exceder a quota.");
            return;
        }

        try {
            // Tenta converter a string para um JsonArray
            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();

            // Verifica se o JsonArray tem o formato esperado
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                JsonElement alternativas = jsonObject.get("alternativas");
                int quantidadeAlternativas = alternativas.getAsString().split(",").length;
                if(quantidadeAlternativas != 5){
                    Log.d("sid-tag","Formato inesperado: alternativas com: " + quantidadeAlternativas + " opções.");
                    String newQuery = "De acordo com nossa última pergunta, a quantidade de alternativas não está de acordo com o que foi pedido, tenha certeza que a quantidade de itens dentro do array \"alternativas\" possui 5 opções e somente uma delas esteja correta." + " Last Response: " + lastResponse;
                    Log.d("sid-tag", "Tentativa de reformular o JSON recebido com erro: " + newQuery);
                    requestQuestions(newQuery);
                    break;
                }
                /*
                if (!jsonObject.has("enunciado") || !jsonObject.has("resposta") || !jsonObject.has("alternativas")) {
                    Log.d("sid-tag","Formato inesperado: falta de campo necessário.");
                    String newQuery = "O JSON não foi gerado no formato especificado, por favor garanta que esteja neste formato: " + goodJson;
                    Log.d("sid-tag", "Tentativa de reformular o JSON recebido com erro: " + newQuery);
                    requestQuestions(newQuery + createGeminiQuery(palavrasChaves));
                    break;
                }
                */

            }
            System.out.println("JSON está no formato correto.");
        } catch (Error e) {
            System.err.println("Erro desconhecido na validacao das questoes: " + e.getMessage());
        }
    }

    public String createGeminiQuery(String palavrasChaves){
        return
                "Crie um JSON com 5 questões objetivas sobre " + palavrasChaves + "com opções de A a E e a resposta em cada questao de acordo com as regras: " +
                        "Somente UMA alternativa deve ser a CORRETA AND devem ser separadas por virgula AND " +
                        "A resposta correta deve estar presente nas alternativas AND " +
                        "A resposta deve conter somente o JSON AND " +
                        "O campo resposta do JSON deve conter a resposta da pergunta que está presente nas alternativas. Exemplo JSON: " +
                        "[" +
                        "{\"enunciado\": \"Qual das seguintes tags HTML é usada para criar um título de nível 1?\", \"resposta\":\"<h1>\", \"alternativas\": \"<h1>,<h2>,<h3>,<h4>,<h5>\"}," +
                        "]";
    }

    @Override
    public void onClick(View v) {
        if(v == btnIniciar){
            this.palavrasChaves = tvPalavrasChave.getText().toString();
            Log.d("sid-tag", "Palavras chave: " + this.palavrasChaves);

            progressBar.setVisibility(View.VISIBLE);


            requestQuestions(createGeminiQuery(palavrasChaves));
        }
    }
}