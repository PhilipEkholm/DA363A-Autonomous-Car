package com.example.talebhammoude.bil;

/**
 * Created by talebhammoude on 2017-05-02.
 */



import android.app.Activity;
import android.os.AsyncTask;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


/**
 * Client klass extends AsyncTask som har background funktioner som gör det lättare att hantera trådar i bakgrunden utan att behöva manipulera trådar
 */
public class Client extends AsyncTask<Void, Void, Void> {
    private DataInputStream dis;
    private DataOutputStream dos;
    private String IpAddress;
    private int port;
    private Socket socket;
    public static Client client;
    Activity activity;
    protected String type;

    /**
     * konstruktor för client klass som har ip & port som ska kopplas till main via rätt ip och port
     * @param IpAddress
     * @param port
     */
    public  Client(String IpAddress, int port){
        this.IpAddress = IpAddress;
        this.port = port;
        client = this;
    }

    /**
     * bakgrunds metod som utför bakgrund tråden
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket(IpAddress, port);
                    setUpStreams();
                    type = dis.readUTF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        return null;
    }

    /**
     * metoden skickar meddelande till server med hjälp av strömmar, och är kopplad till main klass
     * @param type
     * @throws Exception
     */
    public void sendMessage(String type)throws Exception{
        dos.writeUTF(type);
        dos.flush();
    }



    /**
     * sätta up strömmar för att skicka och ta emot meddelande till och från server, metoden kallas i tråden som finns i doInbackground metod.
     * @throws Exception
     */
    public void setUpStreams()throws Exception{
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.dos.flush();
    }
}

