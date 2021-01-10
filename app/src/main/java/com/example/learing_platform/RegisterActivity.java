package com.example.learing_platform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learing_platform.objects.User;
import com.example.learing_platform.utils.ActivityCollector;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private String username;
    private String password;
    private String passwordagain;
    private String nickname;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordagainEditText;
    private EditText nicknameEditText;
    RegisterHandler rhandler;
    String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityCollector.addActivity(this);


        rhandler = new RegisterHandler();

        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.settings_layout_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    //TODO:发送服务器请求：注册
    private void Register(){
        usernameEditText = findViewById(R.id.register_username);
        username = usernameEditText.getText().toString();

        passwordEditText = findViewById(R.id.register_pwd);
        password = passwordEditText.getText().toString();

        passwordagainEditText = findViewById(R.id.register_pwdAgain);
        passwordagain = passwordagainEditText.getText().toString();

        nicknameEditText = findViewById(R.id.register_nickname);
        nickname = nicknameEditText.getText().toString();

        if(username.equals("") )
        {
            Toast.makeText(getApplicationContext(),"用户名不得为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(""))
        {
            Toast.makeText(getApplicationContext(),"密码不得为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(nickname.equals(""))
        {
            Toast.makeText(getApplicationContext(),"昵称不得为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordagain))
        {
            Toast.makeText(getApplicationContext(),"第二次输入的密码与第一次不相同",Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username", username);
        builder.add("password", password);
        builder.add("nickname",nickname);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api1/registered")
                .post(builder.build())
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "onResponse: " + result);
                try{
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("id", Integer.parseInt(result));
                    msg.setData(data);
                    rhandler.sendMessage(msg);
                }catch (Exception e) {

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }

    private class RegisterHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int id = data.getInt("id");
            if(id>0)
            {
                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                usernameEditText.setText("");
                passwordEditText.setText("");
                passwordagainEditText.setText("");
                nicknameEditText.setText("");
            }
            else if(id == 0)
            {
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"该用户名已存在",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    }

}
