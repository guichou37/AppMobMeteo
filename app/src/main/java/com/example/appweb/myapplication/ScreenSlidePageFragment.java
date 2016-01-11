package com.example.appweb.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScreenSlidePageFragment extends Fragment {

    private int day;
    private double latitude;
    private double longitude;
    TextView latitudeset;
    TextView longitudeset;
    TextView meteoset;
    JSONObject result_json;

    public static ScreenSlidePageFragment newInstance(int p, double l1, double l2){
        ScreenSlidePageFragment sspf = new ScreenSlidePageFragment(); //Crée une nouvelle instance de la classe
        Bundle args = new Bundle(); //Crée un bundle
        args.putInt("day", p); //Place l'int dans le bundle
        args.putDouble("latitude", l1);
        args.putDouble("longitude", l2);
        sspf.setArguments(args); //Envoie le bundle dans l'instance, ouvert juste après dans le onCreate
        return sspf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = getArguments().getInt("day"); //Récupération du paramètre stocké dans le bundle
        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //Création de la vue
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_slide, container, false);

        latitudeset = (TextView) rootView.findViewById(R.id.mLatitudeText);
        longitudeset = (TextView) rootView.findViewById(R.id.mLongitudeText);
        meteoset = (TextView) rootView.findViewById(R.id.meteo);

        //Vérification de la connexion
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            HTTPrequests req = new HTTPrequests();
            req.execute(); //Lancement de la requête
        } else {
            alerte("Pas de connexion");
        }
        return rootView;
    }

    //region OpenWeatherMap

    private class HTTPrequests extends AsyncTask<Void, Integer, String> { //L'AsyncTask est nécessaire pour lancer les requêtes en fond.

        String response;

        @Override
        protected String doInBackground(Void... params) {
            String request = "http://api.openweathermap.org/data/2.5/forecast?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&APPID=d63e568fa914f6354620fe3481c3921c";

            try {
                response = sendGet(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                result_json = new JSONObject(result);
                JSONArray list_json = result_json.getJSONArray("list");
                JSONObject today = list_json.getJSONObject(day*8);
                JSONArray weather_json = today.getJSONArray("weather");
                JSONObject weather_json0 = weather_json.getJSONObject(0);
                meteoset.setText(weather_json0.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            latitudeset.setText(String.valueOf(latitude));
            longitudeset.setText(String.valueOf(longitude));
        }

        private String sendGet(String url) throws Exception {

            StringBuffer chaine = new StringBuffer("");
            try {
                URL address = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) address.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = rd.readLine()) != null) {
                    chaine.append(line);
                }

            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }

            return chaine.toString();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("debug","vue"+day+"detruite");
    }

    //endregion

    private void alerte (String alerttext){
        AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
        alertDialog.setTitle("Alerte");
        alertDialog.setMessage(alerttext);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}