package com.example.superjohn.emotionsensingproject;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class FeedBackActivity extends ActionBarActivity {


    int hostPort = 8000;

    private ServerSocket serverSocket;

    //private Handler updateConversationHandler;

    Thread serverThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fullscreen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        Intent intent = getIntent();
        //String serverIp = intent.getStringExtra(ConnectionSetting.EXTRA_SERVERIP);
        //int serverport = intent.getIntExtra(ConnectionSetting.EXTRA_SERVERPORT, 6000);
        //hostPort = intent.getIntExtra(ConnectionSetting.EXTRA_HOSTPORT, 8000);


        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();


    }

    protected void onStop(){
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable{

        @Override
        public void run() {
            Socket socket = null;
            try{
                serverSocket = new ServerSocket(hostPort);
            } catch (IOException e){
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()){
                try {
                    socket = serverSocket.accept();
                    System.out.println("Connected");
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    }

    class CommunicationThread implements Runnable{

        private Socket socket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket){
            this.socket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(
                        this.socket.getInputStream()));

            } catch (IOException e){
                e.printStackTrace();
            }

        }

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()){

                try{
                    String read = input.readLine();
                    System.out.println("6666666666");
                    TextView textView = (TextView)findViewById(R.id.feedback);
                    textView.setText(read);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable{

        private String msg;

        public updateUIThread(String message){
            msg = message;
        }

        @Override
        public void run() {
            TextView textView = (TextView)findViewById(R.id.feedback);
            textView.setText(msg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
