package com.example.alejandro.buscadorvideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    LinearLayout contenedorVideos;
    EditText palabra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contenedorVideos = findViewById(R.id.contenedorVideos);
        palabra = findViewById(R.id.palabra);


        palabra.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                BuscarVideos(null);
                return false;
            }
        });
    }

    public String construirURL() {
        String KEY = getResources().getString(R.string.key);

        return "https://www.googleapis.com/youtube/v3/search?" +
                "key=" + KEY +
                "&part=snippet&type=video" +
                "&q=" + palabra.getText();
    }


    public void BuscarVideos(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, construirURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray listaVideos = json.getJSONArray("items");
                            int totalResultados = json.getJSONObject("pageInfo").getInt("resultsPerPage");

                            Toast.makeText(MainActivity.this, totalResultados +" videos encontrados." , Toast.LENGTH_LONG).show();
                            contenedorVideos.removeAllViews();

                            for (int i = 0; i < totalResultados; i++) {
                                String videoId = listaVideos.getJSONObject(i).getJSONObject("id").getString("videoId");

                                String video =
                                        "<!DOCTYPE html>\n" +
                                                "<html lang=\"es\">\n" +
                                                "<head>\n" +
                                                "    <meta charset=\"UTF-8\">\n" +
                                                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                                                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                                                "    <style>\n" +
                                                "        .videoContenedor {\n" +
                                                "            width: 100%;\n" +
                                                "            padding-bottom: calc((9 / 16) * 100%);\n" +
                                                "\n" +
                                                "            position: relative;\n" +
                                                "            height: 0;\n" +
                                                "            overflow: hidden;\n" +
                                                "        }\n" +
                                                "        \n" +
                                                "        .videoContenedor iframe {\n" +
                                                "            position: absolute;\n" +
                                                "            top: 0;\n" +
                                                "            left: 0;\n" +
                                                "            width: 100%;\n" +
                                                "            height: 100%;\n" +
                                                "        }\n" +
                                                "    </style>\n" +
                                                "</head>\n" +
                                                "<body>\n" +
                                                "    <div class=\"videoContenedor\">" +
                                                "<iframe src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" gesture=\"media\" allow=\"encrypted-media\" allowfullscreen></iframe>" +
                                                "    </div>\n" +
                                                "</body>\n" +
                                                "</html>";

                                WebView webView = new WebView(MainActivity.this);
                                WebSettings webSettings = webView.getSettings();
                                webSettings.setJavaScriptEnabled(true);
                                webView.loadData(video, "text/html", "utf-8");
                                contenedorVideos.addView(webView);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);

    }
}
