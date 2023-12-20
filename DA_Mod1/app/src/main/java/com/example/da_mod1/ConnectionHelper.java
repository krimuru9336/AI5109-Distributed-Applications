package com.example.da_mod1;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    @SuppressLint("NewApi")
    public static Connection connectionClass(){
        Connection conn = null;
        String ip = "192.168.0.29", port ="1433", username="sa", password="sa", databasename = "DA_Aniq";

        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + databasename + ";User=" + username + ";password=" + password + ";";
            conn = DriverManager.getConnection(connectionUrl);
        }
        catch (Exception e){
            Log.e("Error", e.getMessage());
        }

        return conn;
    }
}
