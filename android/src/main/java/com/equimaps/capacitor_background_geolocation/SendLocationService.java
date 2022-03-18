package com.equimaps.capacitor_background_geolocation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.io.IOException;

public class SendLocationService {

    private Location location;
    private String deviceId = "SIN-INFO";
    private String deviceNetwork = "SIN-INFO";
    private Context context;

    public static String token;
    public static String url;
    public static int idRuta;

    public SendLocationService(Location location,Context context){
        this.location = location;
        this.context = context;
        config();
    }

    private void config(){
        try{
            deviceId = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            boolean isWiFi = nInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if(isWiFi) {
                deviceNetwork = "wifi";
            }else{
                deviceNetwork = "cellular";
            }
        }
        catch (Exception e){
            System.out.println("Error al config: "+e.toString());
        }
    }

    public void SendLocationToServer(){
        try {
            System.out.println("Enviando localización... token:"+token);
            OkHttpClient client = new OkHttpClient();
            String json = "{"
                            +"\"lat\":"+location.getLatitude()+","
                            +"\"lon\":"+location.getLongitude()+","
                            +"\"velocidad\":"+Math.round(location.getSpeed())+","
                            +"\"direccion\":"+Math.round(location.getBearing())+","
                            +"\"time\":"+location.getTime()+","
                            +"\"idRuta\":"+SendLocationService.idRuta
                          +"}";
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization","Bearer "+SendLocationService.token)
                    .header("device-id",deviceId)
                    .header("device-network",deviceNetwork)
                    .post(body)
                    .build();

            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(final Call call, IOException e) {
                            // Error
                            System.out.println("Error al enviar localización");
                            System.out.println(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            //String res = response.body().string();
                            if(response.isSuccessful()){
                                System.out.println("Localización enviada al server de Q!");
                            }
                            else{
                                System.out.println("Localización enviar fallida, codigo: "+response.code());
                            }

                            response.close();
                        }
                    });
        } catch (Exception e) {
            System.out.println("Crash al enviar localización.");
            e.printStackTrace();
        }
    }
}
