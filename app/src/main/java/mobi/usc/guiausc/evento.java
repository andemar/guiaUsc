package mobi.usc.guiausc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class evento extends AppCompatActivity {

    private TextView nombreEvento, fechaEvento, descripcionEvento;
    private Button ubicacionEvento;
    private ListView ponentesEvento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        nombreEvento = (TextView) findViewById(R.id.tvEvNombreEvento);
        fechaEvento = (TextView) findViewById(R.id.tvEvFechaEvento);
        descripcionEvento = (TextView) findViewById(R.id.tvEvDescripcion);
        ubicacionEvento = (Button) findViewById(R.id.btnEvUbicacion);
        ponentesEvento = (ListView) findViewById(R.id.lvEvPonentes);

        nombreEvento.setText(getIntent().getStringExtra("Nombre"));
        fechaEvento.setText(getIntent().getStringExtra("Fecha"));
        descripcionEvento.setText(getIntent().getStringExtra("Descripcion"));
        ubicacionEvento.setText(getIntent().getStringExtra("Ubicacion"));
        /** FALTA LA LISTA DE PONENTES */

    }
}
