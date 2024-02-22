package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.server.ServerService;
import com.readutf.matchmaker.shared.api.ApiResponse;
import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.shared.queue.serverfilter.ServerFilterData;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.Query;
import retrofit2.http.GET;
import retrofit2.http.PUT;

import java.util.List;

public interface QueueService {

    @GET("queue/filter")
    Call<ApiResponse<List<ServerFilterData>>> getFilters();

    @PUT("queue/filter")
    Call<ApiResponse<ServerFilterData>> createFilter(@Query("name") String name, @Query("typeId") String typeId, @Query("parameters") List<String> parameters);

    @DELETE("queue/filter")
    Call<ApiResponse<Void>> deleteFilter(String filterId);

    @GET("queue/list")
    Call<ApiResponse<List<Queue>>> getQueueList();

    @GET("queue")
    Call<ApiResponse<Queue>> getQueue(@Query("queueName") String queueName);

    @PUT("queue")
    Call<ApiResponse<Queue>> createQueue(@Query("queueName") String queueName,
                                         @Query("matchMakerId") String matchMakerId,
                                         @Query("filterId") String filterId,
                                         @Query("maxTeamSize") int maxTeamSize,
                                         @Query("minTeamSize") int minTeamSize,
                                         @Query("numberOfTeams") int numberOfTeams);

    @DELETE("queue")
    Call<ApiResponse<Boolean>> deleteQueue(String queueName);

    @PUT("queue/join")
    Call<ApiResponse<Void>> joinQueue(@Query("queueName") String queueName, @Query("playerId") String playerId);

    static QueueService builder(Retrofit retrofit) {
        return retrofit.create(QueueService.class);
    }


}
