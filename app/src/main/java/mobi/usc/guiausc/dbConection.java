package mobi.usc.guiausc;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class dbConection extends AppCompatActivity {

    private TextView idEvento, nombreEvento, descripEvento, lugarEvento, fechaEvento, ponenteEvento;
    private EditText numeroEvento;
    private String urlId = "http://guiausc.000webhostapp.com/web_services/buscar_evento.php?id_evento=";

    private RequestQueue requesQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_conection);

        idEvento        = (TextView) findViewById(R.id.tvDbIdEvento);
        nombreEvento    = (TextView) findViewById(R.id.tvDbNombreEv);
        fechaEvento     = (TextView) findViewById(R.id.tvDbFecha);
        ponenteEvento   = (TextView) findViewById(R.id.tvDbPonenteEvento);
        descripEvento   = (TextView) findViewById(R.id.tvDbDescrip);
        lugarEvento     = (TextView) findViewById(R.id.tvDbLugar);
        numeroEvento    = (EditText) findViewById(R.id.etDbNumeroColum);
    }


    public void onClickdbConection(View view) {
        switch (view.getId()) {
            case R.id.btnDbConectar:

                //Toast.makeText(this, "Entre a conectar", Toast.LENGTH_SHORT).show();
                testInsercion("http://guiausc.000webhostapp.com/web_services/agregar_evento.php");

                break;

            case R.id.btnBuscarEvento:
                //Toast.makeText(this, "Entre a buscar", Toast.LENGTH_SHORT).show();
                //testBuscar("http://guiausc.000webhostapp.com/web_services/buscar_evento.php?id_evento="+ numeroEvento.getText());
                buscarEventoId(Integer.parseInt(numeroEvento.getText().toString()));

                break;
        }
    }



    private void testInsercion(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Insercion exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "F", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_evento", "1");
                parametros.put("Nombre", "Test1");
                parametros.put("Fecha", "11/01/2019");
                parametros.put("Ponente", "TestPonente");
                parametros.put("Descripcion", "TestDes");
                parametros.put("Lugar", "TestLugar");
                return parametros;
            }
        };
        requesQueue = Volley.newRequestQueue(this);
        requesQueue.add(stringRequest);
    }

    private void testBuscar (String URL){

        JsonArrayRequest jr = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo = null;
                for (int i = 0; i < response.length(); i++) {
                    try {

                        jo = response.getJSONObject(i);
                        idEvento.setText(jo.getString("id_evento"));
                        nombreEvento.setText(jo.getString("Nombre"));
                        fechaEvento.setText(jo.getString("Fecha"));
                        ponenteEvento.setText(jo.getString("Ponente"));
                        descripEvento.setText(jo.getString("Descripcion"));
                        lugarEvento.setText(jo.getString("Lugar"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "F", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requesQueue = Volley.newRequestQueue(this);
        requesQueue.add(jr);
    }


    public String[] buscarEventoId(int id){
        final String[] respuesta = new String[5];

        JsonArrayRequest jr = new JsonArrayRequest(urlId + id, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo = null;
                try {
                    jo = response.getJSONObject(0);
                    respuesta[0] = (jo.getString("Nombre"));
                    respuesta[1] = (jo.getString("Fecha"));
                    respuesta[2] = (jo.getString("Ponente"));
                    respuesta[3] = (jo.getString("Descripcion"));
                    respuesta[4] = (jo.getString("Lugar"));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "F", Toast.LENGTH_SHORT).show();
            }
        }
        );

        requesQueue = Volley.newRequestQueue(this);
        requesQueue.add(jr);
        return respuesta;
    }

    public String[] buscarEventoNombre(String nombre){





        return null;
    }

























}