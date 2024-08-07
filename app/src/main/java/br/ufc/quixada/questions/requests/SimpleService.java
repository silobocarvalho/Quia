package br.ufc.quixada.questions.requests;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.concurrent.Executor;

public final class SimpleService {

        public void getResponse(String query, ResponseCallback callback) {
            GenerativeModelFutures model = getModel();

            Content content = new Content.Builder().addText(query).build();
            Executor executor = Runnable::run;

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    callback.onResponse(resultText);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                    callback.onError(throwable);
                }
            }, executor);

        }
        private GenerativeModelFutures getModel() {
            String apiKey = BuildConfig.apiKey;

            SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                    BlockThreshold.ONLY_HIGH);

            GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
            configBuilder.temperature = 0.9f;
            configBuilder.topK = 16;
            configBuilder.topP = 0.1f;
            GenerationConfig generationConfig = configBuilder.build();

            //gemini-1.5-flash <-- esse novo modelo está funcionando melhor para geracao das questoes.
            //gemini-pro <-- usado anteriormente, nao estava funcionando mto bem, gerando alternativas inválidas e somente 4 alternativas ao inves de 5
            //TO DO - CREATE BLOCK TO CHANGE MODEL IF THE QUESTIONS ARE NOT GENERATED CORRECTLY
            GenerativeModel gm = new GenerativeModel(
                    "gemini-1.5-flash",
                    apiKey,
                    generationConfig,
                    Collections.singletonList(harassmentSafety)
            );

            return GenerativeModelFutures.from(gm);
        }

}
