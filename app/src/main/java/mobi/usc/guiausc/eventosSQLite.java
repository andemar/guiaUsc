package mobi.usc.guiausc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class eventosSQLite extends SQLiteOpenHelper {

    public eventosSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * idEvento = La llave primaria y el numero del dato.
         * Nombre = Nombre del evento.
         * Fecha = La fecha del evento.
         * Ponentes = Los ponentes del evento.
         * Descripcion = La descripcion del evento.
         * Ubicacion = La ubicacion del evento.
         */
        //db.execSQL("create table eventosInscritos(idEvento String primary key,  Nombre String, Fecha String, Ponentes String, Descripcion String, Ubicacion String)");
        db.execSQL("create table eventosInscritos(" +
                " idEvento     String primary key," +
                " Nombre       String,"  +
                " Imagen       BLOB,"    +
                " Fecha        String,"  +
                " Hora         String,"  +
                " Ubicacion    String,"  +
                " Ponente      String,"  +
                " Descripcion  String,"  +
                " FechaCambio     int," +
                " HoraCambio      int," +
                " UbicacionCambio int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
