package mobi.usc.guiausc;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.Layout;
        import android.view.LayoutInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

public class guiaUsc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia_usc);
    }





    //-------------------- METODO ONCLICK PARA BOTONES ------------------------//

    public void onClickGuiaUsc(View view){
      switch (view.getId()){
          case R.id.btnCalendario:

              Intent acHomeCale = new Intent(this, calendario.class);
              startActivity(acHomeCale);

              break;

          case R.id.btnBuscarEvento:

             //Toast.makeText(this, "BuscarEvento", Toast.LENGTH_SHORT).show();

              AlertDialog.Builder builder = new AlertDialog.Builder(this);
              LayoutInflater inflater = getLayoutInflater();
              View dialogo = inflater.inflate(R.layout.dialogo_buscar_evento, null);
              builder.setView(dialogo);
              Button btnBuscar   = (Button) dialogo.findViewById(R.id.btnDiaBusBuscar);
              //Button btnCancelar = (Button) dialogo.findViewById(R.id.btnDiaBusCancelar);

              AlertDialog ventanaBuscar = builder.create();
              ventanaBuscar.show();
              break;

          case R.id.btnEventoInsc:

              Toast.makeText(this, "eventoIns", Toast.LENGTH_SHORT).show();

              break;

          //Los case posteriores hacen referencia a los botones de loa cuadros de dialogo.

          case R.id.btnDiaBusBuscar:

              Toast.makeText(this, "Buscar evento", Toast.LENGTH_SHORT).show();
              /** HAY QUE CERRAR EL DIALOGO, LUEGO DE HACER EL TOAST */

              break;
      }
    }


    //------------------- PARTE GRAFICA -----------------------//


    /**public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.btCalendario){
            Intent i = new Intent(this, calendario.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    //----------------- PRUEBAS ---------------------//


}
