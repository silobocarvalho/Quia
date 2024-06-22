package br.ufc.quixada.questions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import br.ufc.quixada.questions.models.Question;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_enunciado;
    RadioButton rb_opcaoA, rb_opcaoB, rb_opcaoC, rb_opcaoD, rb_opcaoE;

    RadioGroup radioGroup;
    Button btn_confirmar;

    List<Question> questions;

    Question currentQuestion;
    int questionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        try {

            tv_enunciado = findViewById(R.id.tv_enunciado);

            rb_opcaoA = findViewById(R.id.rb_opcaoA);
            rb_opcaoB = findViewById(R.id.rb_opcaoB);
            rb_opcaoC = findViewById(R.id.rb_opcaoC);
            rb_opcaoD = findViewById(R.id.rb_opcaoD);
            rb_opcaoE = findViewById(R.id.rb_opcaoE);

            radioGroup = findViewById(R.id.radioGroup);

            btn_confirmar = findViewById(R.id.btn_confirmar);
            btn_confirmar.setOnClickListener(this);

            // Resgatando dados de SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("questions", Context.MODE_PRIVATE);
            String response = sharedPreferences.getString(getString(R.string.saved_questions), null);

            if (response != null) {
                Gson gson = new Gson();
                Type questionListType = new TypeToken<List<Question>>() {
                }.getType();
                questions = gson.fromJson(response, questionListType);

                loadQuestionToView();

            } else {
                Log.d("sid-tag", "Response null from sharedpref");
            }
        }catch (Exception e){
            Log.d("sid-tag", e.getMessage());
            Toast.makeText(this, "Houve um erro com sua busca, tente usar outras palavras chave.", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadQuestionToView(){

        if(questions.size() > questionCount){
            currentQuestion = questions.get(questionCount);

            Log.d("sid-tag", "Questions no QuestionActivity: " + questions);

            fillEnunciado(currentQuestion.getEnunciado());
            fillAlternatives(currentQuestion.getAlternativas().split(","));
        }else{
            Toast.makeText(this, "Parabens! Você concluiu o Quiz com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void fillEnunciado(String enunciado){
        tv_enunciado.setText(enunciado);
    }
    public void fillAlternatives(String[] alternatives){
        rb_opcaoA.setText(alternatives[0]);
        rb_opcaoB.setText(alternatives[1]);
        rb_opcaoC.setText(alternatives[2]);
        rb_opcaoD.setText(alternatives[3]);
        rb_opcaoE.setText(alternatives[4]);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_confirmar){

            String gabarito = currentQuestion.getResposta().trim();
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            String respostaUsuario = selectedRadioButton.getText().toString();

            radioGroup.clearCheck();

            boolean isCorrect = checkResposta(respostaUsuario, gabarito);
            if(isCorrect){
                //Parabeniza e vai para a próxima questao
                questionCount++;
                loadQuestionToView();
            }else{
                //Mostra porque a alternativa selecionada nao é correta e permite que o usuario tente novamente
                Toast.makeText(this, "Você errou. Tente novamente.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, WrongAnswer.class);
                intent.putExtra("pergunta", currentQuestion.getEnunciado());
                intent.putExtra("resposta", respostaUsuario);
                startActivity(intent);

            }
        }
    }


    public boolean checkResposta(String respostaUsuario, String gabarito){
        Log.d("sid-tag", "usuario: " + respostaUsuario + " | gabarito: " + gabarito);
        if(respostaUsuario.equals(gabarito)){
            Log.d("sid-tag", "Acertou!!!");
            Toast.makeText(this, "Você acertou!", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Log.d("sid-tag", "Errou :'( ");
            return false;
        }
    }
}