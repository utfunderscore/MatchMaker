package com.readutf.matchmaker;

import com.readutf.matchmaker.queue.QueueEventHandler;
import com.readutf.matchmaker.queue.QueueListener;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Retrofit;

@Getter
public class ErosApiWrapper {

    private final Retrofit retrofit;

    private @Setter QueueEventHandler queueEventHandler;

    private ErosApiWrapper(String apiURL, String webSocketUrl) {
        this.retrofit = new Retrofit.Builder().baseUrl(apiURL).build();

        new QueueListener(webSocketUrl, queueEventHandler).connect();
    }

    public static Builder builder(String apiURL, String webSocketUrl) {
        return new Builder(apiURL, webSocketUrl);
    }

    public static class Builder {

        private final ErosApiWrapper erosApiWrapper;

        private Builder(String apiURL, String webSocketUrl) {
            erosApiWrapper = new ErosApiWrapper(apiURL, webSocketUrl);
        }

        public Builder queueEventHandler(QueueEventHandler queueEventHandler) {
            erosApiWrapper.setQueueEventHandler(queueEventHandler);
            return this;
        }

        public ErosApiWrapper build() {
            return erosApiWrapper;
        }

    }
}
