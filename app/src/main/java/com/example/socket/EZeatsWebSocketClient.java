package com.example.socket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Locale;

public class EZeatsWebSocketClient extends WebSocketClient {
    private static final String TAG = "TAG_OrderWebSocketClient";
    private LocalBroadcastManager broadcastManager;

    public EZeatsWebSocketClient(URI serverUri, Context context) {
        super(serverUri, new Draft_17());
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        String text = String.format(Locale.getDefault(),
                "onOpen: Http status code = %d; status message = %s",
                handshakeData.getHttpStatus(),
                handshakeData.getHttpStatusMessage());
        Log.d(TAG, "onOpen: " + text);
    }

    @Override
    public void onMessage(String message) {
        sendMessageBroadcast(message);
        Log.d(TAG, "onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        String text = String.format(Locale.getDefault(),
                "code = %d, reason = %s, remote = %b", code, reason, remote);
        Log.d(TAG, "onClose: " + text);
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "onError: exception = " + ex.toString());
    }

    private void sendMessageBroadcast(String message) {
        SocketMessage socketMessage = new Gson().fromJson(message, SocketMessage.class);
        String type = socketMessage.getType();
        Intent intent = new Intent(type);
        intent.putExtra("socketMessage", socketMessage);
        broadcastManager.sendBroadcast(intent);
    }
}
