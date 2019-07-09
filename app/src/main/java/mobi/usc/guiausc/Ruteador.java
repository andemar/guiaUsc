package mobi.usc.guiausc;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Ruteador {


    private Grafo<Salon> grafo;

    private ArrayList<Salon> salones;

    private LatLng coordInicio;


    public Ruteador(){

        salones = new ArrayList<Salon>();

        Salon salon1 = new Salon(0, 1, 3.4033533223764105, -76.54694003839177, "1");
        salones.add(salon1);

        Salon salon2 =
                new Salon(0, 1, 3.4035225122490176, -76.54717928498673, "2");
        salones.add(salon2);

        Salon salon3 =
                new Salon(0, 1, 3.4033391049830253, -76.54721080094265, "3");
        salones.add(salon3);

        Salon salon4 =
                new Salon(2, 1, 3.4034207680765682, -76.5473192458303, "4");
        salones.add(salon4);

        Salon salon5 =
                new Salon(0, 1, 3.403721120092692, -76.5473655139358, "5");
        salones.add(salon5);

        Salon salon6 =
                new Salon(0, 1, 3.403767569512464, -76.54800568060932, "6");
        salones.add(salon6);

        Salon salon7 =
                new Salon(0, 1, 3.403490450561741, -76.54810693399963, "7");
        salones.add(salon7);

        Salon salon8 =
                new Salon(0, 1, 3.4031825405230083, -76.54806066589413, "2115");
        salones.add(salon8);

        Salon salon9 =
                new Salon(2, 1, 3.403158073650246, -76.5473643213893, "9");
        salones.add(salon9);

        Salon salon10 =
                new Salon(2, 1, 3.4034800922428783, -76.54785864290494, "10");
        salones.add(salon10);

        Salon salon11 =
                new Salon(2, 2, 3.4034497629720835, -76.5475485631244, "11");
        salones.add(salon11);

        Salon salon12 =
                new Salon(2, 2, 3.403625137783652, -76.54764579320118, "2205");
        salones.add(salon12);

        Salon salon13 =
                new Salon(2, 2, 3.403438925026339, -76.54738236218691, "13");
        salones.add(salon13);

        Salon salon14 =
                new Salon(2, 2, 3.403438925026339, -76.54738236218691, "14");
        salones.add(salon14);

        Salon salon15 =
                new Salon(2, 2, 3.403276268205102, -76.54750473797321, "15");
        salones.add(salon15);

        Salon salon16 =
                new Salon(2, 2, 3.403276268205102, -76.54750473797321, "15");
        salones.add(salon16);

        Salon salon17 =
                new Salon(2, 2, 3.4034800922428783, -76.54785864290494, "17");
        salones.add(salon17);

        grafo = new Grafo<Salon>();

        grafo.addVertex(salon1);

        grafo.addVertex(salon2);

        grafo.addVertex(salon3);

        grafo.addVertex(salon4);

        grafo.addVertex(salon5);

        grafo.addVertex(salon6);

        grafo.addVertex(salon7);

        grafo.addVertex(salon8);

        grafo.addVertex(salon9);

        grafo.addVertex(salon10);

        grafo.addVertex(salon11);

        grafo.addVertex(salon12);

        grafo.addVertex(salon13);

        grafo.addVertex(salon14);

        grafo.addVertex(salon15);

        grafo.addVertex(salon16);

        grafo.addVertex(salon17);

        grafo.addEdge(salon1, salon3);

        grafo.addEdge(salon1, salon2);

        grafo.addEdge(salon2, salon4);

        grafo.addEdge(salon3, salon4);

        grafo.addEdge(salon4, salon6);

        grafo.addEdge(salon4, salon10);

        grafo.addEdge(salon4, salon5);

        grafo.addEdge(salon6, salon7);

        grafo.addEdge(salon10, salon9);

        grafo.addEdge(salon5, salon8);

        grafo.addEdge(salon7, salon9);

        grafo.addEdge(salon8, salon9);

        grafo.addEdge(salon10, salon17);

        grafo.addEdge(salon17, salon12);

        grafo.addEdge(salon12, salon11);

        grafo.addEdge(salon11, salon13);

        grafo.addEdge(salon13, salon14);

        grafo.addEdge(salon14, salon15);

        grafo.addEdge(salon10, salon16);

        grafo.addEdge(salon16, salon13);
    }

    public ArrayList<Salon> enrutar(Salon destino, Salon actual) {

        ArrayList<Salon> listaAbierta = new ArrayList<Salon>();
        ArrayList<Salon> listaCerrada = new ArrayList<Salon>();
        ArrayList<Salon> vecinos;

        Salon vecino;

        actual.setG(0);

        listaAbierta.add(actual);

        while(!listaAbierta.isEmpty()) {

            actual = menorPeso(listaAbierta);
            listaAbierta.remove(actual);
            listaCerrada.add(actual);

            if(actual.getCodigo().equals(destino.getCodigo())) {

                return getRuta(actual);

            }

            vecinos = getVecinos(actual);



            for (int i = 0; i < vecinos.size(); i++) {

                if(!listaCerrada.contains(vecinos.get(i))) {

                    vecino = vecinos.get(i);

                    vecino.segtPadre(actual);

                    vecino.setG(distanciaG(vecino, actual));

                    vecino.setH(distanciaH(vecino, destino));

                    vecino.setF(vecino.getG() + vecino.getH());

                    if(listaAbierta.contains(vecinos.get(i))) {

                        if(vecino.getG()>vecinos.get(i).getG());

                        continue;

                    }

                    listaAbierta.add(vecino);

                }
            }



        }

        return null;

    }

    private ArrayList<Salon> getRuta(Salon actual) {

        ArrayList<Salon> ruta = new ArrayList<Salon>();

        while(actual!= null) {

            ruta.add(actual);
            actual = actual.getPadre();
        }

        return ruta;
    }


    private double distanciaH(Salon vecino, Salon destino) {

        double dist =0;

        double latVecino = vecino.getLatitud();
        double longVecino = vecino.getLongitud();
        double latDestino = destino.getLatitud();
        double longDestino = destino.getLongitud();

        if(vecino == destino) {
            return 0;
        }
        else {

            dist = Math.sqrt((Math.pow((latVecino - latDestino), 2) + Math.pow((longVecino - longDestino), 2)));

            if(vecino.getBloque()!=destino.getBloque()) {
                dist = dist + 20;
            }

            dist += Math.abs(vecino.getPiso() - destino.getPiso())*5;

        }

        return dist;
    }

    private double distanciaG(Salon vecino, Salon actual) {

        double dis = actual.getG();

        double latVecino = actual.getLatitud();
        double longVecino = actual.getLongitud();
        double latActual = actual.getLatitud();
        double longActual = actual.getLongitud();

        dis += Math.sqrt((Math.pow((latVecino - latActual), 2) + Math.pow((longVecino - longActual), 2)));

        return dis;
    }

    public ArrayList<Salon> getVecinos(Salon salon) {

        ArrayList<Salon> salones = new ArrayList<Salon>();

        Iterable<Salon> vecinos = grafo.getNeighbors(salon);

        for(Salon s : vecinos) {

            salones.add(s);

        }

        return salones;


    }

    public Salon menorPeso(ArrayList<Salon> salones) {

        Salon menor = salones.get(0);

        for (int i = 1; i < salones.size(); i++) {

            if(salones.get(i).getF()<menor.getF()) {
                menor = salones.get(i);
            }

        }

        return menor;

    }

    public Salon buscarSalon(String codigo){

        for (int i=0; i<salones.size(); i++){

            if (salones.get(i).getCodigo().equals(codigo)){
                return salones.get(i);
            }

        }

        return  null;


    }

    public Salon masCercano(LatLng punto){

        double dist = Math.sqrt((Math.pow((punto.latitude - salones.get(0).latitud), 2) + Math.pow((punto.longitude - salones.get(0).longitud), 2)));;

        int indice = 0;

        double ndist;

        for(int i=1; i<salones.size(); i++){

            ndist = Math.sqrt((Math.pow((punto.latitude - salones.get(i).latitud), 2) + Math.pow((punto.longitude - salones.get(i).longitud), 2)));

            if (ndist<dist){
                dist = ndist;
                indice = i;
            }

        }

        return salones.get(indice);

    }

}

