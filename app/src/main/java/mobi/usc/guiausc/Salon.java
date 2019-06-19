package mobi.usc.guiausc;

import com.google.android.gms.maps.model.LatLng;

public class Salon {

    int bloque;

    int piso;

    double latitud;

    double longitud;

    LatLng coordenadas;

    String codigo;

    Salon padre;

    double g;

    double h;

    double f;

    public Salon (int nBloque, int nPiso, double nLatitud, double nLongitud, String nCodigo){

        bloque = nBloque;

        piso = nPiso;

        latitud = nLatitud;

        longitud = nLongitud;

        codigo = nCodigo;

        g = 0;

        padre = null;

        coordenadas = new LatLng(nLatitud,nLongitud);

    }

    public  int getBloque(){

        return bloque;
    }

    public int getPiso() {
        return piso;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getCodigo() {
        return codigo;
    }

    public Salon getPadre() {
        return padre;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public double getF() {
        return f;
    }

    public void segtPadre(Salon nPadre) {

        padre = nPadre;
    }

    public void setG(double nPeso) {

        g = nPeso;
    }

    public void setF(double nPeso) {

        f = nPeso;
    }
    public void setH(double nPeso) {

        h = nPeso;
    }

}
