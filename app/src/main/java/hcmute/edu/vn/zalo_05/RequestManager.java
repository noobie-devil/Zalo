package hcmute.edu.vn.zalo_05;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

import hcmute.edu.vn.zalo_05.RequestDto.CreateRoomRequest;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class RequestManager {
    Context context;
    String accessToken;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.stringee.com/v1/room2/")
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    OkHttpClient okHttpClient = new OkHttpClient();

    public OkHttpClient getOkHttpClient() {
        try {
            boolean config = okHttpClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.addHeader("X-STRINGEE-AUTH", accessToken);
                    return chain.proceed(requestBuilder.build());
                }
            });
            if(config) {
                return okHttpClient;
            }
        } catch (Exception e) {
            Log.d("RequestManager", e.getMessage());
            return null;
        }

        return null;
    }

    public RequestManager(Context context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
    }

    public void createRoom(OnRequestListener listener, String name, String uniqueName) {
        CallStringeeAPI callStringeeAPI = retrofit.create(CallStringeeAPI.class);
        Call<CreateRoomResponse> call = callStringeeAPI.callCreateRoom(new CreateRoomRequest(name, uniqueName));

        try {
            call.enqueue(new Callback<CreateRoomResponse>() {
                @Override
                public void onResponse(Call<CreateRoomResponse> call, retrofit2.Response<CreateRoomResponse> response) {
                    if(!response.isSuccessful()) {
                        Toast.makeText(context, "Error when try to create room!!", Toast.LENGTH_SHORT).show();
                    }
                    listener.onSuccess(response.body());
                }

                @Override
                public void onFailure(Call<CreateRoomResponse> call, Throwable t) {
                    listener.onError("Request Failed!");
                }
            });
        } catch (Exception e) {
            Log.d("RequestManager", e.getMessage());
        }
    }

    public interface CallStringeeAPI {

        @POST("create")
        Call<CreateRoomResponse> callCreateRoom(@Body CreateRoomRequest request);
    }

    public static class CreateRoomResponse {
        private int r;
        private String msg;

        public CreateRoomResponse() {
        }

        public CreateRoomResponse(int r, String msg) {
            this.r = r;
            this.msg = msg;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public interface OnRequestListener {
        void onSuccess(Object obj);
        void onError(String message);
    }
}
