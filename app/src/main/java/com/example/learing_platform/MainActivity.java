package com.example.learing_platform;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learing_platform.objects.User;
import com.example.learing_platform.objects.Userinfo;
import com.example.learing_platform.ui.blog.BlogFragment;
import com.example.learing_platform.ui.home.HomeFragment;
import com.example.learing_platform.ui.personal.PersonalFragment;
import com.example.learing_platform.utils.ActivityCollector;
import com.example.learing_platform.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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


public class MainActivity extends AppCompatActivity {

    private HomeFragment homefragment;
    private BlogFragment blogfragment;
    private PersonalFragment personalfragment;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Toolbar toolbar;

    private String username;
    private String password;
    private String TAG = "MainActivity";
    private UserHandler uhandler = new UserHandler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);



        //根据sharedPreferences判断是否已经登录
        SharedPreferences sharedPreferences = getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        //已经登录：渲染主界面
        if (sharedPreferences.getBoolean("isLogin", false)) {
            //先登录
            username = sharedPreferences.getString("username", "");
            password = sharedPreferences.getString("password", "");
            // 向服务器发请求
            login(username,password);
        }
        //未登录：渲染登录界面
        else {
            username = "";
            password = "";
            renderLoginActivity();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }

    //加载主页面
    private void renderMainActivity() {
        //标题栏
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView =findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        AppBarConfiguration configuration=new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this,navController,configuration);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);


    }

    //加载登录界面
    private void renderLoginActivity() {
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_pwd);
        Button loginButton = findViewById(R.id.btn_login);
        Button registerButton = findViewById(R.id.btn_register);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if(!usernameEditText.getText().toString().equals("")&&!passwordEditText.getText().toString().equals(""))
                    login(username,password);
                else
                {
                    Toast.makeText(getApplicationContext(),"用户名或密码不得为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRegister();
            }
        });
    }

    //点击注册按钮
    private void jumpToRegister() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void login(final String username, String password) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username", username);
        builder.add("password", password);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api1/login")
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
                String result = response.body().string().replaceAll("null","");
                Log.d(TAG, "onResponse: " + result);
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                try{
                    if(user.getId()>0) {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("result", "login_success");
                        data.putString("id", String.valueOf(user.getId()));
                        data.putString("username", user.getUsername());
                        data.putString("password", user.getPassword());
                        data.putSerializable("user", user);
                        msg.setData(data);
                        uhandler.sendMessage(msg);
                    }
                    else
                    {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("result", "login_failure");
                        msg.setData(data);
                        uhandler.sendMessage(msg);
                    }
                }catch(Exception e)
                {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result", "login_failure");
                    msg.setData(data);
                    uhandler.sendMessage(msg);
                }
            }
        });
    }

    private class UserHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            if(result.equals("login_success"))
            {
                //渲染主界面
                renderMainActivity();
                //保存用户名和密码在本地
                SharedPreferences sharedPreferences = getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLogin",true);
                editor.putString("id", data.getString("id"));
                editor.putString("username", data.getString("username"));
                editor.putString("password", data.getString("password"));
                editor.commit();
                Toast.makeText(getApplicationContext(),"欢迎来到Python学习平台",Toast.LENGTH_SHORT).show();

                User user = (User)data.getSerializable("user");
                Userinfo userinfo = (Userinfo)getApplication();
                userinfo.setUser(user);

            }
            else if(result.equals("login_failure"))
            {
                //提示用户名或密码错误
                Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    }
}


