package mz.ac.bxd.project.sms_listener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketService extends Service {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final String SERVER_IP = "192.168.129.33"; // IP do servidor
    private static final int SERVER_PORT = 5000; // Porta do servidor
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        // Inicia a thread de comunicação com o servidor
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Conecte-se ao servidor
                    socket = new Socket(SERVER_IP, SERVER_PORT);

                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Envia log inicial ao servidor
                    sendLog("Aplicativo conectado ao servidor.");

                    String serverMessage;
                    while (isRunning && (serverMessage = in.readLine()) != null) {
                        // Processa comandos recebidos do servidor
                        processCommand(serverMessage);
                    }

                } catch (Exception e) {
                    Log.e("SocketService", "Erro na conexão ao servidor", e);
                } finally {
                    stopSelf(); // Encerra o serviço quando a comunicação for interrompida
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retorna o comportamento do serviço quando ele for reiniciado
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            Log.e("SocketService", "Erro ao fechar a conexão", e);
        }
    }

    // Método para processar comandos recebidos do servidor
    private void processCommand(String command) {
        Log.d("SocketService", "Comando recebido: " + command);
        if (command.equals("STOP")) {
            // Parar funcionalidade do aplicativo
            Log.d("SocketService", "Parando funcionalidades.");
        } else if (command.equals("START")) {
            // Retomar funcionalidades do aplicativo
            Log.d("SocketService", "Iniciando funcionalidades.");
        } else {
            sendLog("Comando desconhecido: " + command);
        }
    }

    // Método para enviar logs ao servidor
    private void sendLog(String log) {
        if (out != null) {
            out.println(log);
            Log.d("SocketService", "Log enviado ao servidor: " + log);
        }
    }
}
