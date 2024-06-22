package br.ufc.quixada.questions.models;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private String enunciado;
    private String alternativas;
    private String resposta;

    public Question(String question,String rightAnswer, String alternativas ) {
        this.enunciado = question;
        this.resposta = rightAnswer;
        this.alternativas = alternativas;

    }



    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(String alternativas) {
        this.alternativas = alternativas;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }
}