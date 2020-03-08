package com.example.main;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.socket.EZeatsWebSocketClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class Common {
    public static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    public static final String REGEX_EMAIL = "^\\w+((-\\w+)|(.\\w+))*@[A-Za-z0-9]+((\\.|\\-)[A-Za-z0-9]+)*\\.[A-Za-z]+$";
    public static final String EMPLOYEE_PREFRENCE = "employee";
    public static EZeatsWebSocketClient eZeatsWebSocketClient;

    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void connectSocketServer(Context context, String employeeType) {
        URI uri = null;
        try {
            uri = new URI(Url.SOCKET_URI + employeeType);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (eZeatsWebSocketClient == null) {
            eZeatsWebSocketClient = new EZeatsWebSocketClient(uri, context);
            eZeatsWebSocketClient.connect();
        }
    }

    public static void disconnectSocketServer() {
        if (eZeatsWebSocketClient != null) {
            eZeatsWebSocketClient.close();
            eZeatsWebSocketClient = null;
        }
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
