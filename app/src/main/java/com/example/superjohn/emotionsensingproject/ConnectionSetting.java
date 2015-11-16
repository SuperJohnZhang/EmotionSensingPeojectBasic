package com.example.superjohn.emotionsensingproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.text.format.Formatter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class ConnectionSetting extends ActionBarActivity {
    static final int LOCAL_PORT = 6000;
    public final static String EXTRA_SERVERIP =  "com.example.superjohn.emotionsensingproject.SERVERIP";
    public final static String EXTRA_SERVERPORT =  "com.example.superjohn.emotionsensingproject.SERVERPORT";
    public final static String EXTRA_HOSTIP =  "com.example.superjohn.emotionsensingproject.HOSTIP";
    public final static String EXTRA_HOSTPORT =  "com.example.superjohn.emotionsensingproject.HOSTPORT";
    private String hostInfo;

    // connection part
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fullscreen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_setting);
        String localIp = getLocalIp();
        int locolPort = getLocalPort();
        hostInfo = localIp + "\t" + locolPort;

        // print the content in the editfield
        TextView textView = (TextView) findViewById(R.id.Locol_Info);
        textView.setTextSize(30);
        textView.setText("The locol ip is: " + localIp + '\n');
        textView.append("The default local port is: " + locolPort + "\n");

        // connect button
        Button connectButton = (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new ClientThread()).start();
            }
        });


        // send Button
        Button sendButton = (Button) findViewById(R.id.sendButtion);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send the image
                try {
                    if (socket != null){
                        // first step is to get the image
                        /*
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        System.out.println(CaremaActivity.imgPathName);
                        Bitmap bitmap = BitmapFactory.decodeFile(CaremaActivity.imgPathName, options);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
                        System.out.println(stream.size());
                        byte[] size = ByteBuffer.allocate(4).putInt(stream.size()).array();
                        byte[] byteArray = stream.toByteArray();

                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(size);
                        System.out.println(byteArray.length);
                        outputStream.write(byteArray);
                        outputStream.flush();
                        outputStream.close();
                        */

                        File myFile = new File(CaremaActivity.imgPathName);
                        byte[] mybytearray = new byte[(int)myFile.length()];
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        bis.read(mybytearray, 0, mybytearray.length);
                        System.out.println("finish packing");
                        OutputStream os = socket.getOutputStream();
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();
                        socket.close();
                    }
                } catch (UnknownHostException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }



            }
        });

        // nextButton
        Button nextButton = (Button)findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // start another work to get the image back
                /*
                try{
                    InputStream inputStream = socket.getInputStream();
                    byte[] img = new byte[6022386];
                    FileOutputStream fos = new FileOutputStream(CaremaActivity.imgPathName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int byteRead = inputStream.read(img, 0, img.length);
                    int current = byteRead;
                    do{
                        byteRead = inputStream.read(img, current, img.length-current);
                        if (byteRead>=0)
                            current += byteRead;
                    }while(byteRead>-1);

                    bos.write(img, 0, current);
                    bos.flush();
                    bos.close();


                }catch (Exception e){
                    e.printStackTrace();
                }







                String serverIp = getServerIp();
                int serverPort = getServerPort();

                // start another intent to show the feedback
                Intent intent = new Intent(ConnectionSetting.this, FeedBackActivity.class);
                intent.putExtra(EXTRA_SERVERIP, serverIp);
                intent.putExtra(EXTRA_SERVERPORT, serverPort);
                intent.putExtra(EXTRA_HOSTIP, getLocalIp());
                intent.putExtra(EXTRA_HOSTPORT, getLocalPort());
                //startActivity(intent);
                */
            }
        });
    }

    private String getServerIp(){
        EditText editText = (EditText)findViewById(R.id.server_ip_address);
        String ip = editText.getText().toString();
        return ip;
    }

    private int getServerPort(){
        EditText editText = (EditText)findViewById(R.id.server_port);
        int port = Integer.parseInt(editText.getText().toString());
        return port;
    }

    private String getLocalIp(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    private int getLocalPort(){
        return LOCAL_PORT;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection_setting, menu);
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

    // ClientThread
    class ClientThread implements Runnable {

        public void run(){
            try{
                System.out.println("start to create");
                InetAddress serverAddr = InetAddress.getByName(getServerIp());
                socket = new Socket(serverAddr,getServerPort());
                System.out.println("created");
            }catch (UnknownHostException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
