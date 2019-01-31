package mobi.usc.guiausc;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class guiaUsc extends AppCompatActivity {

    //--------------ATRIBUTOS DE LA CLASE--------------//

    private String urlId = "http://guiausc.000webhostapp.com/web_services/buscar_evento_id.php?id_evento=";
    private String urlNombre = "http://guiausc.000webhostapp.com/web_services/buscar_evento_nombre.php?Nombre=";

    //-----------OBJETOS DE OTRAS CLASES--------------//

    private eventosSQLite   conexion;
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
        //Metodo que elimina eventos, que hayan pasado.
        comprobacionEliminar();
        //Metodo que comprueba si han ocurrido cambios en los eventos.
        comprobacionCambio();
        /** ALERTA PARA DECIR QUE UN EVENTO INSCRITO, YA SE VA A REALIZAR, 1 DIA DE POR MEDIO */
    }


    //-------------------- METODO ONCLICK PARA BOTONES ------------------------//

    public void onClickGuiaUsc(View view) {
        switch (view.getId()) {
            case R.id.btnCalendario:

                if(networkStatus() == true){
                    final Intent acHomeCale = new Intent(this, calendario.class);
                    startActivity(acHomeCale);
                }else
                    Toast.makeText(this, R.string.toastPPError5,Toast.LENGTH_LONG).show();
                break;

            case R.id.btnBuscarEvento:

                //Toast.makeText(this, "BuscarEvento", Toast.LENGTH_SHORT).show();

                if(networkStatus() == true){
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
                }else
                    Toast.makeText(this, R.string.toastPPError5,Toast.LENGTH_LONG).show();

                break;

            case R.id.btnEventoInsc:

                //Toast.makeText(this, "eventoIns", Toast.LENGTH_SHORT).show();
                String[] nombres = getNombreEventos();

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

                //Toast.makeText(this, "Buscar Nombre", Toast.LENGTH_SHORT).show();
                String nombreEvento = diaNombreEvento.getText().toString();

                if (nombreEvento.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toastPPError1, Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(getApplicationContext(), "Se busco el nombre", Toast.LENGTH_SHORT).show();
                    buscarEventoNombre(nombreEvento);
                }

                /** HAY QUE CERRAR EL DIALOGO, LUEGO DE HACER EL TOAST */

                break;

            case R.id.btnDiaBusCodigo:

                //Toast.makeText(this, "Buscar Codigo", Toast.LENGTH_SHORT).show();
                String codigoEvento = diaCodigoEvento.getText().toString();

                if (codigoEvento.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.toastPPError2, Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(getApplicationContext(), "Se busco el codigo", Toast.LENGTH_SHORT).show();
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
                urlNombre + Nombre.replace(" ", "%20"),
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
                        Toast.makeText(getApplicationContext(), "Linea: 213: " + R.string.toastPPError3, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jar);
    }

    public String[] comprobarEventoNombre(final String nombre){
        final String[] respuesta = new String[5];
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
                            respuesta[0] = (jo.getString("Fecha"));
                            respuesta[1] = (jo.getString("Hora"));
                            respuesta[2] = (jo.getString("Ubicacion"));
                            respuesta[3] = (jo.getString("Ponente"));
                            respuesta[4] = (jo.getString("Descripcion"));
                            comprobacionCambioPorEvento(nombre, respuesta);
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
        return respuesta;
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

    //-------------------METODOS PARA SQLITE-------------------------//
    private String[] getNombreEventos(){

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

        return nombres;
    }

    //NFH = Nombre, Fecha, Hora.
    private String[] getNFHEvento(){

        String[] NFH;

        int i = 0;

        conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
        SQLiteDatabase dbInsc = conexion.getReadableDatabase();

        Cursor fila = dbInsc.rawQuery("SELECT Nombre, Fecha, Hora from "+NOMBRE_TABLA, null);

        NFH = new String[fila.getCount()*3];

        if(fila.moveToFirst()){
            NFH[i] = fila.getString(0);
            i++;
            NFH[i] = fila.getString(1);
            i++;
            NFH[i] = fila.getString(2);
            i++;
        }
        while(fila.moveToNext()){
            NFH[i] = fila.getString(0);
            i++;
            NFH[i] = fila.getString(1);
            i++;
            NFH[i] = fila.getString(2);
            i++;
        }
        dbInsc.close();

        return NFH;
    }


    private void eliminarEventoNombre(String nombre){
        conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
        SQLiteDatabase eliminar = conexion.getWritableDatabase();

        if(nombre.isEmpty()){
            Toast.makeText(this, R.string.toastPPError6, Toast.LENGTH_LONG).show();
        }else{
            eliminar.delete(NOMBRE_TABLA, "Nombre="+"'"+nombre+"'", null);
        }

        conexion.close();
    }

    //----------------COMPROBACIONES DE DATOS--------------------//

    private boolean networkStatus(){

        boolean resultado = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            resultado = true;
        } else{
        }

        return resultado;
    }




    /**Metodo que comprueba si un evento ya paso, y lo elimina automaticamente.
     *NFH = NOMBRE, FECHA, HORA.
     */
    private void comprobacionEliminar(){

        String[] listaEventosNFH = getNFHEvento();

        if(listaEventosNFH.length == 0){

        }else{

            String nombreEvento;
            // 0.Dia 1.Mes 2.Anio
            String[] fechaEvento;
            // 0. xx:yy 1. am/pm
            String[] horaEventoTotal;

            int anioEvento;
            int mesEvento;
            int diaEvento;
            int diaNocheEvento;
            int horaEvento;
            //int minutosEvento;

            Date date = new Date();
            int anioActual = Integer.parseInt(new SimpleDateFormat("y").format(date));
            int mesActual = Integer.parseInt(new SimpleDateFormat("M").format(date));
            int diaActual = Integer.parseInt(new SimpleDateFormat("d").format(date));
            //Si la hora es PM, la variable contendra un 1, de caso contrario 0;
            int diaNocheActual = new SimpleDateFormat("a").format(date).equals("PM") ? 1 : 0;
            int horaActual = Integer.parseInt(new SimpleDateFormat("h").format(date));
            //int minutosActual = Integer.parseInt(new SimpleDateFormat("m").format(date));

            /**
            Toast.makeText(this, "anio: " + anioActual, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "mes: " + mesActual, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "dia: " + diaActual, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "am/pm: " + diaNocheActual, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "hora: " + horaActual, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "minutos: " + minutosActual, Toast.LENGTH_SHORT).show();
            */

            for(int i = 0; i < listaEventosNFH.length; i=+3){
                nombreEvento = listaEventosNFH[i];
                fechaEvento = listaEventosNFH[i+1].split("/");
                horaEventoTotal = listaEventosNFH[i+2].split(" ");

                anioEvento = Integer.parseInt(fechaEvento[2]);
                mesEvento = Integer.parseInt(fechaEvento[1]);
                diaEvento = Integer.parseInt(fechaEvento[0]);
                //Si la hora es PM, la variable contendra un 1, de caso contrario 0;
                diaNocheEvento = horaEventoTotal[1].equalsIgnoreCase("PM") ? 1 : 0;
                String[] horaMinuto = horaEventoTotal[0].split(":");
                //Se suma 3 hora al evento, para que el evento no se elimine cuando comience.
                horaEvento = Integer.parseInt(horaMinuto[0])+1;
                //minutosEvento = Integer.parseInt(horaMinuto[1]);

                /**
                 Toast.makeText(this, "anioEvento: " + anioEvento, Toast.LENGTH_SHORT).show();
                 Toast.makeText(this, "mesEvento: " + mesEvento, Toast.LENGTH_SHORT).show();
                 Toast.makeText(this, "diaEvento: " + diaEvento, Toast.LENGTH_SHORT).show();
                 Toast.makeText(this, "am/pmEvento: " + diaNocheEvento, Toast.LENGTH_SHORT).show();
                 Toast.makeText(this, "horaEvento: " + horaEvento, Toast.LENGTH_SHORT).show();
                 //Toast.makeText(this, "minutosEvento: " + minutosEvento, Toast.LENGTH_SHORT).show();
                 */

                if(anioEvento-anioActual > 0){
                    //Toast.makeText(this, "Break por anio - " + anioEvento + " - " + anioActual, Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(anioEvento-anioActual < 0){
                    //Toast.makeText(this, "Eliminar por anio - " + anioEvento + " - " + anioActual, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "El evento "+ nombreEvento + " fue eliminado de eventos inscritos, debido a que ya se realizó.", Toast.LENGTH_LONG).show();
                    eliminarEventoNombre(nombreEvento);
                }
                else if(mesEvento-mesActual > 0){
                    //Toast.makeText(this, "Break por mes - " + mesEvento + " - " + mesActual, Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(mesEvento-mesActual < 0){
                    //Toast.makeText(this, "Eliminar por mes - " + mesEvento + " - " + mesActual, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "El evento "+ nombreEvento + " fue eliminado de eventos inscritos, debido a que ya se realizó.", Toast.LENGTH_LONG).show();
                    eliminarEventoNombre(nombreEvento);
                }
                else if(diaEvento-diaActual > 0){
                    //Toast.makeText(this, "break por dia - " + diaEvento + " - " +diaActual, Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(diaEvento-diaActual < 0){
                    //Toast.makeText(this, "Eliminar por dia - " + diaEvento + " - " +diaActual, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "El evento "+ nombreEvento + " fue eliminado de eventos inscritos, debido a que ya se realizó.", Toast.LENGTH_LONG).show();
                    eliminarEventoNombre(nombreEvento);
                }
                else if(diaNocheEvento-diaNocheActual > 0){
                    //Toast.makeText(this, "Break por diaNoche - " + diaNocheEvento + " - " + diaNocheActual, Toast.LENGTH_SHORT).show();
                    break;
                }
                else if((diaNocheEvento-diaNocheActual) < 0){
                    //Toast.makeText(this, "Eliminar por diaNoche - " + diaNocheEvento + " - " + diaNocheActual, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "El evento "+ nombreEvento + " fue eliminado de eventos inscritos, debido a que ya se realizó.", Toast.LENGTH_LONG).show();
                    eliminarEventoNombre(nombreEvento);
                }
                else if(horaEvento-horaActual > 0){
                    //Toast.makeText(this, "break por hora - " + horaEvento + " - " + horaActual, Toast.LENGTH_SHORT).show();
                    break;
                }
                else if(horaEvento-horaActual < 0){
                    //Toast.makeText(this, "Eliminar por hora - " + horaEvento + " - " + horaActual, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "El evento "+ nombreEvento + " fue eliminado de eventos inscritos, debido a que ya se realizó.", Toast.LENGTH_LONG).show();
                    eliminarEventoNombre(nombreEvento);
                }

            }
        }
    }





    /**Metodo que obtiene los datos de la lista y comprueba si el usuario tiene eventos inscritos y conexion a internet.*/
    private void comprobacionCambio(){

        String[] listaEventos = getNombreEventos();

        if(listaEventos.length == 0){
            //Toast.makeText(this, "No hay eventos inscritos", Toast.LENGTH_SHORT).show();
        }
        else if(networkStatus() == false){
            //No comprueba los cambios llegado el caso el usuario no tenga conexion a internet.
        }
        else{
            //Toast.makeText(this, "La lista de evento contiene: " + listaEventos.length + " eventos", Toast.LENGTH_LONG).show();

            for(int i = 0; i < listaEventos.length; i++){
                comprobarEventoNombre(listaEventos[i]);
            }
        }
    }





    /** Metodo que comprueba los datos de SQLite con el servidor.
        Llegado el caso sean diferentes, se cambia los datos en el SQLite.
        0.Fecha, 1.Hora, 2. Ubicacion 3.Ponente, 4.Descripcion*/
    private void comprobacionCambioPorEvento(String nombreEvento, String[] dbWeb){
            //Contenedor de los cambios.
            ContentValues cambios = new ContentValues();
            String respuestaToast = "";

            //Valores almacenados en SQLite, en el dispositivo.
            conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
            SQLiteDatabase consulta = conexion.getReadableDatabase();
            SQLiteDatabase escritura = conexion.getWritableDatabase();
            Cursor sqlite = consulta.rawQuery("SELECT Fecha, Hora, Ubicacion, Ponente, Descripcion FROM "+NOMBRE_TABLA+" WHERE Nombre ='"+nombreEvento+"'", null);
            sqlite.moveToFirst();

            /** Variables para almacenar los diferentes datos de local y web */
            String fechaLocal = sqlite.getString(0);
            String horaLocal = sqlite.getString(1);
            String ubicacionLocal = sqlite.getString(2);
            String ponenteLocal = sqlite.getString(3);
            String descripcionLocal = sqlite.getString(4);
            /***/
            String fechaWeb = dbWeb[0];
            String horaWeb = dbWeb[1];
            String ubicacionWeb = dbWeb[2];
            String ponenteWeb = dbWeb[3];
            String descripcionWeb = dbWeb[4];
            /**FIN*/

            //Este if compara las Fechas.
            if(!fechaLocal.equals(fechaWeb)){
                cambios.put("Fecha", fechaWeb);
                cambios.put("FechaCambio", 1);
                respuestaToast+= "Fecha ";
            }

            if(!horaLocal.equals(horaWeb)){
                cambios.put("Hora", horaWeb);
                cambios.put("HoraCambio", 1);
                respuestaToast+= " Hora ";
            }

            if(!ubicacionLocal.equals(ubicacionWeb)){
                cambios.put("Ubicacion", ubicacionWeb);
                cambios.put("UbicacionCambio", 1);
                respuestaToast+= " Ubicacion ";
            }

            if(!ponenteLocal.equals(ponenteWeb)){
                cambios.put("Ponente", ponenteWeb);
            }

            if(!descripcionLocal.equals(descripcionWeb)){
                cambios.put("Descripcion", descripcionWeb);
            }

            if(cambios.size() != 0){
                escritura.update(NOMBRE_TABLA,cambios,"Nombre=?", new String[] {nombreEvento});
            }

            if(!respuestaToast.isEmpty()){
                Toast.makeText(this, "El evento " + nombreEvento + " a cambiado su: " + respuestaToast, Toast.LENGTH_LONG).show();
            }

            consulta.close();
            escritura.close();
    }

}



























//-------------NO PONER ATENCION-----------------//