package mobi.usc.guiausc;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class evento extends AppCompatActivity {

    //----------NOMBRE DE LAS FACULTADES--------------//
    private String cieBasicas = "Ciencias basicas";
    private String cieEconomicas = "Ciencias economicas";
    private String coSocial = "Comunicacion social";
    private String derecho = "Derecho";
    private String ingenieria = "Ingenieria";
    private String lenguas = "Lenguas";
    private String salud = "Salud";

    //---------ATRIBUTOS DEL LAYOUT--------------//
    private TextView    nombreEvento, fechaEvento, horaEvento, descripcionEvento;
    private Button      btnUbicacionEvento, btnInscribirse, btnEliminar;
    private ListView    ponentesEvento;
    private ImageView   imagenEvento;
    //---------ATRIBUTOS DE LA CLASE-------------//

    private String  urlNombre = "http://guiausc.000webhostapp.com/web_services/buscar_evento_nombre.php?Nombre=";
    private int     datos;
    private String  nombreEventoIntent;

    //-----------ATRIBUTOS DE SQLITE-------------//

    private String          NOMBRE_DB = "eventosInscritos";
    private String          NOMBRE_TABLA = "eventosInscritos";
    private eventosSQLite   conexion;


    //---------------CONSTRUCTOR-----------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Bloquear las pantallas de forma vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        nombreEvento = (TextView) findViewById(R.id.tvEvNombreEvento);
        imagenEvento = (ImageView) findViewById(R.id.ivEvImagenEvento);
        fechaEvento = (TextView) findViewById(R.id.tvEvFechaEvento);
        horaEvento = (TextView) findViewById(R.id.tvEvHora);
        descripcionEvento = (TextView) findViewById(R.id.tvEvDescripcion);
        btnUbicacionEvento = (Button) findViewById(R.id.btnEvUbicacion);
        ponentesEvento = (ListView) findViewById(R.id.lvEvPonentes);
        btnEliminar = (Button) findViewById(R.id.btnEvElimintar);
        btnInscribirse = (Button) findViewById(R.id.btnEvInscribirse);
        //datos: Variable que expecifica por donde se ingresaron los valores, WEB o SQLite.
        datos = getIntent().getIntExtra("datos",-1);
        nombreEventoIntent = getIntent().getStringExtra("Nombre");

        if(datos == 0 || datos == 1){
            //Ocultar el boton eliminar y mostrar el de inscribirse.
            btnEliminar.setVisibility(View.GONE);
            btnInscribirse.setVisibility(View.VISIBLE);
            //Datos de entrada, mediante DB.
            datosDB();
        }else if(datos == 2){
            //Ocultar el boton inscribirse y mostrar el de eliminar.
            btnEliminar.setVisibility(View.VISIBLE);
            btnInscribirse.setVisibility(View.GONE);
            //Se envia el nombre, que se extrajo del intent extra, cuando la persona elige el evento.
            datosSQLite(nombreEventoIntent);
        }else
            Toast.makeText(getApplicationContext(), "Hubo un error.", Toast.LENGTH_SHORT).show();

    }


    public void onClickEvento(View view) {
        switch (view.getId()) {

            case R.id.btnEvInscribirse:

                //Toast.makeText(getApplicationContext(), "btnInscribirse", Toast.LENGTH_SHORT).show();

                conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
                SQLiteDatabase dbInsc = conexion.getReadableDatabase();

                Cursor existe = dbInsc.rawQuery("SELECT * FROM "+NOMBRE_TABLA+" WHERE Nombre ='"+nombreEvento.getText().toString()+"'", null);

                if(existe.getCount() <= 0){
                    //Se crea el nuevo evento
                    //LISTA DE ATRIBUTOS: idEvento String primary key, nombre String, Imagen BLOB, fecha String, Ponentes String, Descripcion String, Ubicacion String

                    ContentValues registro = new ContentValues();
                    registro.put("idEvento", dbInsc.rawQuery("SELECT * FROM "+ NOMBRE_TABLA, null).getCount() + 1);
                    registro.put("Nombre",nombreEvento.getText().toString());

                    /**
                     * Para almacenar la imagen, se extrae el bitmap del ImageView
                     * Luego se convierte a un array de byte, mediante byteArrayOutputStream
                     * y se almacena en una variable byte[] que sera ingresada al SQLite
                     * y se limpia el outputStream.

                    Bitmap bitmap = ((BitmapDrawable)imagenEvento.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //CUIDADO CON LAS IMAGENES PNG !!!!!!
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imagen = stream.toByteArray();
                    registro.put("Imagen", imagen);

                    FIN*/
                    registro.put("Facultad",    getIntent().getStringExtra("Facultad"));
                    registro.put("Fecha",       fechaEvento.getText().toString());
                    registro.put("Hora",        horaEvento.getText().toString());
                    registro.put("Ubicacion",   btnUbicacionEvento.getText().toString());
                    registro.put("Ponente",     getIntent().getStringExtra("Ponente"));
                    registro.put("Descripcion", descripcionEvento.getText().toString());
                    //Valores que identifican si un valor cambio. 0-No ha cambiado, 1-Si ha cambiado.
                    registro.put("FechaCambio", 0);
                    registro.put("HoraCambio",  0);
                    registro.put("UbicacionCambio", 0);
                    //Se insertan los datos al SQLite
                    dbInsc.insert(NOMBRE_TABLA, null, registro);
                    Toast.makeText(this, R.string.toastPEAlerta1, Toast.LENGTH_LONG).show();
                    dbInsc.close();
                    //bitmap.recycle();
                    Intent acEvenHome = new Intent(getApplicationContext(), guiaUsc.class);
                    startActivity(acEvenHome);

                }else
                    Toast.makeText(this, R.string.toastPEError1,Toast.LENGTH_LONG).show();

            break;

            case R.id.btnEvUbicacion:

                    Toast.makeText(this, "Entro al mapa", Toast.LENGTH_SHORT).show();
                    Intent acMapa = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(acMapa);

            break;

            case R.id.btnEvElimintar:

                conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
                SQLiteDatabase dbElim = conexion.getReadableDatabase();

                int cantidad = dbElim.delete(NOMBRE_TABLA, "Nombre='"+nombreEvento.getText().toString()+"'", null);
                if(cantidad >= 1){
                    Toast.makeText(this, R.string.toastPEAlerta2, Toast.LENGTH_LONG).show();
                    Intent acEvenHome = new Intent(getApplicationContext(), guiaUsc.class);
                    startActivity(acEvenHome);
                }else
                    Toast.makeText(this, R.string.toastPPError6, Toast.LENGTH_SHORT).show();

            break;

            case R.id.btnEvCancelar:
                //Toast.makeText(getApplicationContext(), "BtnCancelar", Toast.LENGTH_SHORT).show();
                Intent acEvenHome = new Intent(getApplicationContext(), guiaUsc.class);
                startActivity(acEvenHome);
            break;
        }
    }

    public void datosDB(){
        nombreEvento.setText(getIntent().getStringExtra("Nombre"));

        /**IMAGEN
        String urlImagen = getIntent().getStringExtra("Imagen");
        if(urlImagen.isEmpty()){
            urlImagen = "Error";
        }
        Picasso.get()
                .load(urlImagen)
                //.resize(100,100)
                .error(R.drawable.ic_launcher_background)
                .into(imagenEvento);*/

        //Imagen del evento dependiendo de la facultad.
        String facultad = getIntent().getStringExtra("Facultad");
        if(facultad.equals(cieBasicas)){
            imagenEvento.setImageResource(R.drawable.ciencias_basicas);
        }else if(facultad.equals(cieEconomicas)){
            imagenEvento.setImageResource(R.drawable.ciencias_economicas);
        }else if(facultad.equals(coSocial)){
            imagenEvento.setImageResource(R.drawable.comunicacion_social);
        }else if(facultad.equals(derecho)){
            imagenEvento.setImageResource(R.drawable.derecho);
        }else if(facultad.equals(ingenieria)){
            imagenEvento.setImageResource(R.drawable.ingenieria);
        }else if(facultad.equals(lenguas)){
            imagenEvento.setImageResource(R.drawable.lenguas);
        }else if(facultad.equals(salud)){
            imagenEvento.setImageResource(R.drawable.salud);
        }

        horaEvento.setText(getIntent().getStringExtra("Hora"));
        fechaEvento.setText(getIntent().getStringExtra("Fecha"));

        /** LISTA DE PONENTES */
        //Obtengo un arreglo con los ponentes. (Cada uno separado por 'coma', y eliminando los espacios en blanco"
        String[] listaPonente = getIntent().getStringExtra("Ponente").split(",");
        ArrayAdapter<String> nombres = new ArrayAdapter<String>(this, R.layout.list_ponentes_evento,listaPonente);
        ponentesEvento.setAdapter(nombres);


        descripcionEvento.setText(getIntent().getStringExtra("Descripcion"));
        btnUbicacionEvento.setText(getIntent().getStringExtra("Ubicacion"));

    }

    public void datosSQLite(String nombre){
        conexion = new eventosSQLite(this, NOMBRE_DB, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
                                        //SELECT Imagen, Fecha....
        Cursor fila = db.rawQuery("SELECT Facultad, Fecha, Hora, Ubicacion, Ponente, Descripcion, FechaCambio, HoraCambio, UbicacionCambio FROM "+NOMBRE_TABLA+" WHERE Nombre ='"+nombre+"'", null);

        if(fila.moveToFirst()){
            nombreEvento.setText(nombre);
            /**IMAGEN
             * Para extraer la imagen de SQLite es necesario un arreglo de bye
             * Se extrame mediante getBlob
             * Se convierte de byte a bitmap, mediante bitmapFactory....
             * y el bitmap resultante se inserta al ImageView
             *
             * byte[] imagen = fila.getBlob(0);
             * Bitmap bmp = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
             * imagenEvento.setImageBitmap(bmp);
            FIN*/
            //Dependiendo la facultad, es su imagen.
            String facultad = fila.getString(0);
            if(facultad.equals(cieBasicas)){
                imagenEvento.setImageResource(R.drawable.ciencias_basicas);
            }else if(facultad.equals(cieEconomicas)){
                imagenEvento.setImageResource(R.drawable.ciencias_economicas);
            }else if(facultad.equals(coSocial)){
                imagenEvento.setImageResource(R.drawable.comunicacion_social);
            }else if(facultad.equals(derecho)){
                imagenEvento.setImageResource(R.drawable.derecho);
            }else if(facultad.equals(ingenieria)){
                imagenEvento.setImageResource(R.drawable.ingenieria);
            }else if(facultad.equals(lenguas)){
                imagenEvento.setImageResource(R.drawable.lenguas);
            }else if(facultad.equals(salud)){
                imagenEvento.setImageResource(R.drawable.salud);
            }

            fechaEvento.setText(fila.getString(1));
            horaEvento.setText(fila.getString(2));
            btnUbicacionEvento.setText(fila.getString(3));
            /** LISTA DE PONENTES */
            //Obtengo un arreglo con los ponentes. (Cada uno separado por 'coma', y eliminando los espacios en blanco"
            String[] listaPonente = fila.getString(4).split(",");
            ArrayAdapter<String> nombres = new ArrayAdapter<String>(this, R.layout.list_ponentes_evento,listaPonente);
            ponentesEvento.setAdapter(nombres);
            /**FIN*/
            descripcionEvento.setText(fila.getString(5));

            //COMPROBACION - CAMBIAR DE COLOR A VERDE, CUANDO ALGUNO DE LOS COMPROBANTES SEA 1.
            int fechaCambio = fila.getInt(6);
            int horaCambio = fila.getInt(7);
            int ubicacionCambio = fila.getInt(8);

            if(fechaCambio == 1){
                fechaEvento.setBackgroundColor(getResources().getColor(R.color.colorCambio));
            }

            if(horaCambio == 1){
                horaEvento.setBackgroundColor(getResources().getColor(R.color.colorCambio));
            }

            if(ubicacionCambio == 1){
                btnUbicacionEvento.setBackgroundColor(getResources().getColor(R.color.colorCambio));
            }

        }else
            Toast.makeText(this, "No se encontro el evento en SQLite",Toast.LENGTH_SHORT).show();
    }
}