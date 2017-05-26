package com.example.talebhammoude.bil;

/**
 * Created by talebhammoude on 2017-05-02.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Main activity kör hela applikation och har id som refererar till layouten för buttons osv, och är kopplad till client klass
 */
public class MainActivity extends AppCompatActivity {
    Client client;
    Button forwardbtn, backwardbtn, leftbtn, rightbtn, breakbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forwardbtn =  (Button) findViewById(R.id.forwardBtn);
        forwardbtn.setOnClickListener(new ButtonListner());
        backwardbtn = (Button)findViewById(R.id.backwardBtn);
        backwardbtn.setOnClickListener(new ButtonListner());
        leftbtn = (Button) findViewById(R.id.leftBtn);
        leftbtn.setOnClickListener(new ButtonListner());
        rightbtn = (Button) findViewById(R.id.rightBtn);
        rightbtn.setOnClickListener(new ButtonListner());
        breakbtn = (Button) findViewById(R.id.brakeBtn);
        breakbtn.setOnClickListener(new ButtonListner());


        client = new Client("192.168.20.164", 8001);
        client.activity = this;
        client.execute();

    }

    /**
     * ButtonListener klass den har en switch-case i onclick metoden som skickar en olik sträng vid varje click med hjälp av sendmessage metoden som finns i client klass
     */
    private class ButtonListner implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.forwardBtn:

                    try {
                        client.sendMessage( "1");
                        System.out.println("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.backwardBtn:
                    try {
                        client.sendMessage("2");
                        System.out.println("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.leftBtn:
                    try {
                        client.sendMessage("4");
                        System.out.println("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.rightBtn:
                    try{
                        client.sendMessage("3");
                        System.out.println("1");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.brakeBtn:
                    try{
                        client.sendMessage("0");
                        System.out.println("1");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }
}
