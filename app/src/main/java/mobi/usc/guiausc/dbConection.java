package mobi.usc.guiausc;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConection extends AppCompatActivity {

    private static Connection conn = null;
    private static String login = "appAndroid";
    private static String pass = "Android123123";
    private static String url = "jdbc:oracle:thin:@192.168.0.22:5500:em";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_conection);


    }


    public void onClickdbConection(View view) {
        switch (view.getId()) {
            case R.id.btnConectar:

                getConnection();

                break;
        }
    }

    public  Connection getConnection(){
        try {
            //Class.forName("oracle.jdbc.driver.OracleDriver");


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            }catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }

            try{
                conn = DriverManager.getConnection(url, login, pass);
            }catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }


            conn.setAutoCommit(false);
            if(conn != null){
                Toast.makeText(this, "Conexion exitosa", Toast.LENGTH_SHORT).show();
                System.out.println("Conexion exitosa");
            }
            else
                Toast.makeText(this, "Conexion es erronea", Toast.LENGTH_SHORT).show();
                System.out.println("Conexion es erronea");

        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return conn;
    }

    public void desconexion(){
        try {
            conn.close();

        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }



}
