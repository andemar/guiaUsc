package mobi.usc.guiausc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

public class guiaUsc extends AppCompatActivity {

    //--------------ATRIBUTOS DE LA CLASE--------------//

    private String urlId = "http://guiausc.000webhostapp.com/web_services/buscar_evento_id.php?id_evento=";
    private String urlNombre = "http://guiausc.000webhostapp.com/web_services/buscar_evento_nombre.php?Nombre=";

    //-----------OBJETOS DE OTRAS CLASES--------------//


    //-----------ATRIBUTOS DE LOS DIALOGOS-------------//
    /**
     * Dialogo btn buscarEvento
     */
    private EditText diaNombreEvento, diaCodigoEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_usc);
    }


    //-------------------- METODO ONCLICK PARA BOTONES ------------------------//

    public void onClickGuiaUsc(View view) {
        switch (view.getId()) {
            case R.id.btnCalendario:

                Intent acHomeCale = new Intent(this, calendario.class);
                startActivity(acHomeCale);

                break;

            case R.id.btnBuscarEvento:

                //Toast.makeText(this, "BuscarEvento", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builderBuscar = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogo = inflater.inflate(R.layout.dialogo_buscar_evento, null);
                /**Obtener las referencias de los EditText*/
                diaNombreEvento = (EditText) dialogo.findViewById(R.id.diaNombreEvento);
                diaCodigoEvento = (EditText) dialogo.findViewById(R.id.diaCodigoEvento);
                /**Fin*/
                builderBuscar.setView(dialogo);
                AlertDialog ventanaBuscar = builderBuscar.create();
                ventanaBuscar.show();
                break;

            case R.id.btnEventoInsc:

                //Toast.makeText(this, "eventoIns", Toast.LENGTH_SHORT).show();

                final CharSequence[] lista = {"evento1", "evento2", "evento3", "evento4"};
                AlertDialog.Builder builderInsc = new AlertDialog.Builder(this);
                builderInsc.setTitle("Eventos inscritos");
                builderInsc.setItems(lista, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), "Has elegido la opcion: " + lista[item], Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                AlertDialog ventanaInsc = builderInsc.create();
                ventanaInsc.show();
                break;

            //Los case posteriores hacen referencia a los botones de loa cuadros de dialogo.

            case R.id.btnDiaBusNombre:

                Toast.makeText(this, "Buscar Nombre", Toast.LENGTH_SHORT).show();
                String nombreEvento = diaNombreEvento.getText().toString();

                if (nombreEvento.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toastPPError1, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Se busco el nombre", Toast.LENGTH_SHORT).show();
                    buscarEventoNombre(nombreEvento);
                }

                /** HAY QUE CERRAR EL DIALOGO, LUEGO DE HACER EL TOAST */

                break;

            case R.id.btnDiaBusCodigo:

                Toast.makeText(this, "Buscar Codigo", Toast.LENGTH_SHORT).show();
                String codigoEvento = diaCodigoEvento.getText().toString();

                if (codigoEvento.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toastPPError2, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Se busco el codigo", Toast.LENGTH_SHORT).show();
                    //Peticion de datos a la DB.
                    buscarEventoId(codigoEvento);
                }

                /** HAY QUE CERRAR EL DIALOGO, LUEGO DE HACER EL TOAST */

                break;
        }
    }


    //-------------------- METODOS ------------------------//

    private void diaBuscar() {

    }

    //----------------- Conexion a base de datos ---------------------//

    public void buscarEventoNombre(String Nombre){
        final String[] respuesta = new String[5];
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jar = new JsonArrayRequest(
                Request.Method.GET,
                urlNombre + Nombre,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject jo = response.getJSONObject(0);
                            respuesta[0] = (jo.getString("Nombre"));
                            respuesta[1] = (jo.getString("Fecha"));
                            respuesta[2] = (jo.getString("Ponente"));
                            respuesta[3] = (jo.getString("Descripcion"));
                            respuesta[4] = (jo.getString("Ubicacion"));
                            //Intent con datos, a la clase evento
                            Intent acHomeEven = new Intent(getApplicationContext(), evento.class);
                            acHomeEven.putExtra("Nombre",respuesta[0]);
                            acHomeEven.putExtra("Fecha", respuesta[1]);
                            acHomeEven.putExtra("Ponente", respuesta[2]);
                            acHomeEven.putExtra("Descripcion", respuesta[3]);
                            acHomeEven.putExtra("Ubicacion", respuesta[4]);
                            startActivity(acHomeEven);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), R.string.toastPPError3, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jar);
    }

    public void buscarEventoId(String id){
        final String[] respuesta = new String[5];
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jar = new JsonArrayRequest(
                Request.Method.GET,
                urlId + id,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject jo = response.getJSONObject(0);
                            respuesta[0] = (jo.getString("Nombre"));
                            respuesta[1] = (jo.getString("Fecha"));
                            respuesta[2] = (jo.getString("Ponente"));
                            respuesta[3] = (jo.getString("Descripcion"));
                            respuesta[4] = (jo.getString("Ubicacion"));
                            //Intent con datos, a la clase evento
                            Intent acHomeEven = new Intent(getApplicationContext(), evento.class);
                            acHomeEven.putExtra("Nombre",respuesta[0]);
                            acHomeEven.putExtra("Fecha", respuesta[1]);
                            acHomeEven.putExtra("Ponente", respuesta[2]);
                            acHomeEven.putExtra("Descripcion", respuesta[3]);
                            acHomeEven.putExtra("Ubicacion", respuesta[4]);
                            startActivity(acHomeEven);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), R.string.toastPPError4, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jar);
    }

}









//-------------NO PONER ATENCION-----------------//

/**
//Metodo para la peticion de datos a la DB
 public String[] buscarEventoId(String id) {

 final String[] respuesta = new String[5];
 RequestQueue requesQueue;
 requesQueue = Volley.newRequestQueue(this);
 JsonArrayRequest jr = new JsonArrayRequest("http://guiausc.000webhostapp.com/web_services/buscar_evento_id.php?id_evento=" + id, new Response.Listener<JSONArray>() {
 public void onResponse(JSONArray response) {
 JSONObject jo;
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
 public void onErrorResponse(VolleyError error) {
 Toast.makeText(getApplicationContext(), "F", Toast.LENGTH_SHORT).show();
 }
 }
 );

 requesQueue.add(jr);
 Toast.makeText(getApplicationContext(), "Salir del metodo: "+ respuesta[0], Toast.LENGTH_SHORT).show();



 return respuesta;
 }







 */