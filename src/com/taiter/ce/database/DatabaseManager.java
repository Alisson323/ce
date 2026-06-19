package com.taiter.ce.database;

public class DatabaseManager {

    private boolean connected = false;

    public void connect() {
        this.connected = true;
    }

    public void disconnect() {
        this.connected = false;
    }

    public boolean isConnected() {
        return this.connected;
    }
}
