package mobi.usc.guiausc;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

    private eventosSQLite conexion;
    private String          NOMBRE_DB = "eventosInscritos";
    private String          NOMBRE_TABLA = "eventosInscritos";

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

                final Intent acHomeCale = new Intent(this, calendario.class);
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

                String[] nombres;
                int i = 0;

                conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
                SQLiteDatabase dbInsc = conexion.getReadableDatabase();

                Cursor fila = dbInsc.rawQuery("SELECT Nombre from "+NOMBRE_TABLA, null);

                nombres = new String[fila.getCount()];

                if(fila.moveToFirst()){
                    nombres[i] = fila.getString(0);
                    i++;
                }
                while(fila.moveToNext()){
                    nombres[i] = fila.getString(0);
                    i++;
                }
                dbInsc.close();

                final CharSequence[] lista = nombres;
                AlertDialog.Builder builderInsc = new AlertDialog.Builder(this);
                builderInsc.setTitle("Eventos inscritos");
                builderInsc.setItems(lista, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        //Toast.makeText(getApplicationContext(), "Has elegido la opcion: " + lista[item], Toast.LENGTH_SHORT).show();

                        //Enviar el intent con el nombre a evento.
                        Intent acHomeEv = new Intent(getApplicationContext(), evento.class);
                        //Se ingresa el numero 2, para indicar que los datos son de la lista.
                        acHomeEv.putExtra("datos", 2);
                        acHomeEv.putExtra("Nombre", lista[item]);
                        startActivity(acHomeEv);
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
        final String[] respuesta = new String[7];
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
                            respuesta[1] = (jo.getString("Imagen"));
                            respuesta[2] = (jo.getString("Fecha"));
                            respuesta[3] = (jo.getString("Hora"));
                            respuesta[4] = (jo.getString("Ponente"));
                            respuesta[5] = (jo.getString("Descripcion"));
                            respuesta[6] = (jo.getString("Ubicacion"));
                            //Intent con datos, a la clase evento
                            Intent acHomeEven = new Intent(getApplicationContext(), evento.class);
                            //Datos = 0, significa que los datos de entrada, son traidos de la DB.
                            acHomeEven.putExtra("datos", 0);
                            acHomeEven.putExtra("Nombre",       respuesta[0]);
                            acHomeEven.putExtra("Imagen",       respuesta[1]);
                            acHomeEven.putExtra("Fecha",        respuesta[2]);
                            acHomeEven.putExtra("Hora",         respuesta[3]);
                            acHomeEven.putExtra("Ponente",      respuesta[4]);
                            acHomeEven.putExtra("Descripcion",  respuesta[5]);
                            acHomeEven.putExtra("Ubicacion",    respuesta[6]);
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
        final String[] respuesta = new String[7];
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
                            respuesta[1] = (jo.getString("Imagen"));
                            respuesta[2] = (jo.getString("Fecha"));
                            respuesta[3] = (jo.getString("Hora"));
                            respuesta[4] = (jo.getString("Ponente"));
                            respuesta[5] = (jo.getString("Descripcion"));
                            respuesta[6] = (jo.getString("Ubicacion"));
                            //Intent con datos, a la clase evento
                            Intent acHomeEven = new Intent(getApplicationContext(), evento.class);
                            acHomeEven.putExtra("datos", 1);
                            acHomeEven.putExtra("Nombre",       respuesta[0]);
                            acHomeEven.putExtra("Imagen",       respuesta[1]);
                            acHomeEven.putExtra("Fecha",        respuesta[2]);
                            acHomeEven.putExtra("Hora",         respuesta[3]);
                            acHomeEven.putExtra("Ponente",      respuesta[4]);
                            acHomeEven.putExtra("Descripcion",  respuesta[5]);
                            acHomeEven.putExtra("Ubicacion",    respuesta[6]);
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