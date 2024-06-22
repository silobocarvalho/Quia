package br.ufc.quixada.questions.requests;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
