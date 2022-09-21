package org.example.Server;

import org.example.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private final AtomicBoolean session = new AtomicBoolean();
    private final ServerSocket server;
    private final List<User> users = new CopyOnWriteArrayList<>();

    public Server(int port) throws IOException {
        server = new ServerSocket(port);
        setSession(true);
    }

    public void start() {
        System.out.println("Server started successfully");

        startListenerAsync();

        waitUntilSessionEnds();

        System.out.println("Server closed successfully");
    }

    private void startListenerAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                startListener();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void startListener() throws IOException {
        while (Thread.activeCount() <= 100) {
            var socket = server.accept();
            users.add(new User(this, socket));
        }
    }

    private void waitUntilSessionEnds() {
        while (getSession()) waitFor5s();
    }

    private void waitFor5s() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSession(boolean session) {
        this.session.set(session);
    }

    public boolean getSession() {
        return session.get();
    }
}
