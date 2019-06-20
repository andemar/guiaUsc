package mobi.usc.guiausc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class calendario extends AppCompatActivity {

    //---------ATRIBUTOS DEL LAYOUT--------------//

    private Spinner spFacultad;

    //--------------ATRIBUTOS DE LA CLASE--------------//

    private String urlTodos = "http://guiausc.000webhostapp.com/web_services/buscar_evento_todos.php";
    private String urlFacultad = "http://guiausc.000webhostapp.com/web_services/buscar_evento_facultad.php?Facultad=";
    private String urlNombre = "http://guiausc.000webhostapp.com/web_services/buscar_evento_nombre.php?Nombre=";

    //-----------OBJETOS DE OTRAS CLASES--------------//

    private eventosSQLite   conexion;
    private String          NOMBRE_DB       = "eventosInscritos";
    private String          NOMBRE_TABLA    = "eventosInscritos";
    private String[]        facultades      = {"Todo", "Ciencias básicas", "Ciencias económicas", "Comunicación social", "Derecho", "Ingeniería", "Lenguas", "Salud"};
    private ListView        lvEventos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Bloquear las pantallas de forma vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        /** Lista de facultates*/
        spFacultad = (Spinner) findViewById(R.id.spFacultadesCalendario);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sp_facultades_calendario, facultades);
        spFacultad.setAdapter(adapter);
        /**FIN*/
        lvEventos = (ListView) findViewById(R.id.lvCalendarioEventos);
        lvEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buscarNombreLista(position);
            }
        });
        //Se inicializa la lista, con la todalidad de eventos.
        listaInicial();
    }



    public void onClickCalendario(View view){
        switch (view.getId()){

            case R.id.btnCalendarioBuscar:

                String seleccionFacultad = spFacultad.getSelectedItem().toString();
                //Toast.makeText(this, "Seleccione: " + seleccionFacultad,Toast.LENGTH_SHORT).show();
                /**Si el usuario selecciona la primera opcion TOD0, se obtendran la totalidad e ventos.
                 * De lo contrario, se buscara dependiendo la facultad que el usuario haya escodigo.
                 */
                if(spFacultad.getSelectedItem().equals(facultades[0])){
                    listaInicial();
                }else{
                    listaPorFacultad();
                }
                break;

            }
    }



    //Metodo que obtiene la totalidad de eventos
    public void listaInicial(){

        //Toast.makeText(this, "Entre a tod0", Toast.LENGTH_SHORT).show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jar = new JsonArrayRequest(
                Request.Method.GET,
                urlTodos,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int i = 0;
                        String[] eventos = new String[response.length()];
                        String temp = "";
                        while (i < response.length()){

                            try {
                                JSONObject jo = response.getJSONObject(i);
                                temp += jo.getString("Nombre");
                                temp += "   -   ";
                                temp += jo.getString("Fecha");
                                temp += "   -   ";
                                temp += jo.getString("Hora");
                                eventos[i] = temp;
                                temp = "";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                        //Toast.makeText(getApplicationContext(), "Ya busque", Toast.LENGTH_LONG).show();
                        ArrayAdapter<String> listaEventos = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_ponentes_evento,eventos);
                        lvEventos.setAdapter(listaEventos);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Linea: 213: " + R.string.toastPPError3, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jar);
    }




    //Metodo que obtiene los eventos por la facultad elegida por el usaurio.
    public void listaPorFacultad(){

       // Toast.makeText(this, "Entre a facultad", Toast.LENGTH_SHORT).show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jar = new JsonArrayRequest(
                Request.Method.GET,
                urlFacultad+spFacultad.getSelectedItem().toString().replace(" ", "%20"),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int i = 0;
                        String[] eventos = new String[response.length()];
                        String temp = "";
                        while (i < response.length()){

                            try {
                                JSONObject jo = response.getJSONObject(i);
                                temp += jo.getString("Nombre");
                                temp += "   -   ";
                                temp += jo.getString("Fecha");
                                temp += "   -   ";
                                temp += jo.getString("Hora");
                                eventos[i] = temp;
                                temp = "";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                        //Toast.makeText(getApplicationContext(), "Ya busque", Toast.LENGTH_LONG).show();
                        ArrayAdapter<String> listaEventos = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_ponentes_evento,eventos);
                        lvEventos.setAdapter(listaEventos);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), R.string.toastPCError2, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jar);

    }



    public void buscarNombreLista(int posicion){

        String[] evento = lvEventos.getItemAtPosition(posicion).toString().split("-");
        String nombre = evento[0].trim();
        //Extraigo el nombre del evento seleccionado por el usuario de la lista.
        //Toast.makeText(this, "Seleccionaste: " + nombre, Toast.LENGTH_SHORT).show();

        final String[] respuesta = new String[7];
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jar = new JsonArrayRequest(
                Request.Method.GET,
                urlNombre + nombre.replace(" ", "%20"),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject jo = response.getJSONObject(0);
                            respuesta[0] = (jo.getString("Nombre"));
                            //respuesta[1] = (jo.getString("Imagen"));
                            respuesta[1] = (jo.getString("Fecha"));
                            respuesta[2] = (jo.getString("Hora"));
                            respuesta[3] = (jo.getString("Ponente"));
                            respuesta[4] = (jo.getString("Descripcion"));
                            respuesta[5] = (jo.getString("Ubicacion"));
                            respuesta[6] = (jo.getString("Facultad"));
                            //Intent con datos, a la clase evento
                            Intent acHomeEven = new Intent(getApplicationContext(), evento.class);
                            //Datos = 0, significa que los datos de entrada, son traidos de la DB.
                            acHomeEven.putExtra("datos", 0);
                            acHomeEven.putExtra("Nombre",       respuesta[0]);
                            //acHomeEven.putExtra("Imagen",       respuesta[1]);
                            acHomeEven.putExtra("Fecha",        respuesta[1]);
                            acHomeEven.putExtra("Hora",         respuesta[2]);
                            acHomeEven.putExtra("Ponente",      respuesta[3]);
                            acHomeEven.putExtra("Descripcion",  respuesta[4]);
                            acHomeEven.putExtra("Ubicacion",    respuesta[5]);
                            acHomeEven.putExtra("Facultad",     respuesta[6]);
                            startActivity(acHomeEven);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Linea: 213: " + R.string.toastPPError3, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jar);

    }




}
