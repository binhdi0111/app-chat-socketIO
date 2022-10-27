package com.binhdi0111.bka.nodejssocketio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity{
    private Socket mSocket;
    ListView lvUser,lvChat;
    ImageButton btnAdd,btnSend;
    EditText edtChat;
    ArrayList<String> arrayList,arrayListChat;
    ArrayAdapter adapter,adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Anhxa();
        try {
            mSocket = IO.socket("http://192.168.1.165:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
//        mSocket.on("server-send-data",onRetrieveData);
        mSocket.on("server-send-result",onRetrieveResurl);
        mSocket.on("server-send-user",onListUser);
        mSocket.on("server-send-chat",onListChat);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
        lvUser.setAdapter(adapter);
        arrayListChat = new ArrayList<>();
        adapterChat = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,arrayListChat);
        lvChat.setAdapter(adapterChat);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtChat.getText().toString().trim().length() > 0){
                    mSocket.emit("client-register-user",edtChat.getText().toString().trim());
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtChat.getText().toString().trim().length() > 0){
                    mSocket.emit("client-send-chat",edtChat.getText().toString().trim());
                }
            }
        });

    }
    private Emitter.Listener onRetrieveResurl = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        boolean exits = object.getBoolean("ketqua");
                        if(exits){
                            Toast.makeText(MainActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(MainActivity.this, "đăng kí thành công", Toast.LENGTH_LONG).show();
                        }                    
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                
            });
        }
    };

    private Emitter.Listener onListUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray array = object.getJSONArray("danhsach");
                        arrayList.clear();
                        for (int i = 0;i < array.length();i++){
                            String username = array.getString(i);
                            arrayList.add(username);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onListChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String chat = object.getString("chatcontent");
                        arrayListChat.add(chat);
                        adapterChat.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private void Anhxa(){
        lvUser = (ListView) findViewById(R.id.listviewUser);
        lvChat = (ListView) findViewById(R.id.listviewChat);
        edtChat = (EditText) findViewById(R.id.editTextContent);
        btnAdd = (ImageButton) findViewById(R.id.add);
        btnSend = (ImageButton) findViewById(R.id.send);
    }

}