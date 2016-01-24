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
import android.widget.ImageView;
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
import java.util.Calendar;

public class ScreenSlidePageFragment extends Fragment {

    private int day;
    private double latitude;
    private double longitude;
    TextView cityView;
    TextView dayView;
    ImageView iconView;
    //region Views
    ImageView icon0;
    ImageView icon3;
    ImageView icon6;
    ImageView icon9;
    ImageView icon12;
    ImageView icon15;
    ImageView icon18;
    ImageView icon21;
    //endregion
    JSONObject result_json;
    String iconsrc;

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
        //region Call Views
        cityView = (TextView) rootView.findViewById(R.id.cityView);
        iconView = (ImageView) rootView.findViewById(R.id.iconView);
        dayView = (TextView) rootView.findViewById(R.id.dayView);
        icon0 = (ImageView) rootView.findViewById(R.id.img0);
        icon3 = (ImageView) rootView.findViewById(R.id.img3);
        icon6 = (ImageView) rootView.findViewById(R.id.img6);
        icon9 = (ImageView) rootView.findViewById(R.id.img9);
        icon12 = (ImageView) rootView.findViewById(R.id.img12);
        icon15 = (ImageView) rootView.findViewById(R.id.img15);
        icon18 = (ImageView) rootView.findViewById(R.id.img18);
        icon21 = (ImageView) rootView.findViewById(R.id.img21);
        //endregion
        switch (day)
        {
            case 0 : dayView.setText("Aujourd'hui");
            break;
            case 1 : dayView.setText("Demain");
                break;
            case 2 : dayView.setText("Après-demain");
                break;
            default:
            break;
        }

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


                Calendar c = Calendar.getInstance();
                int hours = c.get(Calendar.HOUR_OF_DAY);
                int decalage = (int) Math.floor(hours / 3);
                int d = (8-decalage);

            try {
                result_json = new JSONObject(result);
                JSONObject city = result_json.getJSONObject("city");
                cityView.setText(city.getString("name"));
                setIcons(iconView,result_json, day*8);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

                if (day==0)
                {
                    int i=8;
                    while(d!=0)
                    {
                        d=d-1;
                        switch (i){
                            case 8 : setIcons(icon21,result_json,i-1-decalage);
                                break;
                            case 7 : setIcons(icon18,result_json,i-1-decalage);
                                break;
                            case 6 : setIcons(icon15,result_json,i-1-decalage);
                                break;
                            case 5 : setIcons(icon12,result_json,i-1-decalage);
                                break;
                            case 4 : setIcons(icon9,result_json,i-1-decalage);
                                break;
                            case 3 : setIcons(icon6,result_json,i-1-decalage);
                                break;
                            case 2 : setIcons(icon3,result_json,i-1-decalage);
                                break;
                            case 1 : setIcons(icon0,result_json,i-1-decalage);
                                break;
                        }
                        i--;

                    }

                    }

                else if (day!=0)
                {
                    int i=8;
                    while(i!=0)
                    {
                        switch (i){
                            case 8 : setIcons(icon21,result_json,i-1+day*8-decalage);
                                break;
                            case 7 : setIcons(icon18,result_json,i-1+day*8-decalage);
                                break;
                            case 6 : setIcons(icon15,result_json,i-1+day*8-decalage);
                                break;
                            case 5 : setIcons(icon12,result_json,i-1+day*8-decalage);
                                break;
                            case 4 : setIcons(icon9,result_json,i-1+day*8-decalage);
                                break;
                            case 3 : setIcons(icon6,result_json,i-1+day*8-decalage);
                                break;
                            case 2 : setIcons(icon3,result_json,i-1+day*8-decalage);
                                break;
                            case 1 : setIcons(icon0,result_json,i-1+day*8-decalage);
                                break;
                        }
                        i--;
                    }

                }

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
                String line="";
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
        Log.d("debug", "vue" + day + "detruite");
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

    public void setIcons(ImageView imageView , JSONObject json, int n){
        if(n>24){imageView.setImageResource(R.drawable.inone);return;}
        String src = null;
        try {
            JSONArray list_json = json.getJSONArray("list");
            JSONObject today = list_json.getJSONObject(n);
            JSONArray weather_json = today.getJSONArray("weather");
            JSONObject weather_json0 = weather_json.getJSONObject(0);
            src = weather_json0.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch(src){
            case "01d" : imageView.setImageResource(R.drawable.i01d);
                break;
            case "01n" : imageView.setImageResource(R.drawable.i01n);
                break;
            case "02d" : imageView.setImageResource(R.drawable.i02d);
                break;
            case "02n" : imageView.setImageResource(R.drawable.i02n);
                break;
            case "03d" : imageView.setImageResource(R.drawable.i03d);
                break;
            case "03n" : imageView.setImageResource(R.drawable.i03n);
                break;
            case "04d" : imageView.setImageResource(R.drawable.i04d);
                break;
            case "04n" : imageView.setImageResource(R.drawable.i04n);
                break;
            case "09d" : imageView.setImageResource(R.drawable.i09d);
                break;
            case "09n" : imageView.setImageResource(R.drawable.i09n);
                break;
            case "10d" : imageView.setImageResource(R.drawable.i10d);
                break;
            case "10n" : imageView.setImageResource(R.drawable.i10n);
                break;
            case "11d" : imageView.setImageResource(R.drawable.i11d);
                break;
            case "11n" : imageView.setImageResource(R.drawable.i11n);
                break;
            case "13d" : imageView.setImageResource(R.drawable.i13d);
                break;
            case "13n" : imageView.setImageResource(R.drawable.i13n);
                break;
            case "50d" : imageView.setImageResource(R.drawable.i50d);
                break;
            case "50n" : imageView.setImageResource(R.drawable.i50n);
                break;
            default : imageView.setImageResource(R.drawable.inone);
                break;
        }
    }

}
