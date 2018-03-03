package com.atribus.projectbus.Model;

import android.support.v4.util.Pair;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Arun  on 1/3/18
 */

public class BusRoutes {

    private static final String college_campus = "College Campus";
    public HashMap <String, Vector <Pair <String, String>>> busroute;
    public HashMap <String, String> Areasroutes;
    public HashMap <String, String> Numberroutes;


    public BusRoutes() {

        busroute = new HashMap <>();
        Areasroutes = new HashMap <>();
        Numberroutes = new HashMap <>();
        Numberroutes.put("Avadi", "11");
        Numberroutes.put("Tambram", "12");
        Numberroutes.put("Koyilambakam", "13");

        //Avadi vector
        Vector <Pair <String, String>> Avadi = new Vector <>();
        Avadi.add(Pair.create("Anna Stachu ", "5.55 am"));
        Avadi.add(Pair.create("Thirumullaivoyal", " 5.57 am"));
        Avadi.add(Pair.create("Ambattur OT", "6.00 am"));
        Avadi.add(Pair.create("Dunlop Ambethgar Stachu", "6.02 am"));
        Avadi.add(Pair.create("Collector Nagar", "6.15 am"));
        Avadi.add(Pair.create("Jai Nagar", "6.25 am"));
        Avadi.add(Pair.create(college_campus, "7.15 am"));

        busroute.put("11", Avadi);

        Areasroutes.put("11", "Avadi");
        Areasroutes.put("12", "Avadia");
        Areasroutes.put("13", "Avadi");




    }
}
