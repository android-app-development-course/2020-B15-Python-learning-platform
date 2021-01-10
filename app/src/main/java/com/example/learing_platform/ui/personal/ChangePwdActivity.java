package com.example.learing_platform.ui.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.objects.Userinfo;
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

public class ChangePwdActivity extends AppCompatActivity {
    private String username;
    private String password;
    private String oldpwd;
    private String newpwd;
    private String newpwdagain;

    String TAG = "ChangePwdActivity";

    PwdHandler handler = new PwdHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ActivityCollector.addActivity(this);

        findViewById(R.id.settings_layout_changePwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ((Userinfo)getApplication()).getUser().getUsername();
                password = ((Userinfo)getApplication()).getUser().getPassword();
                oldpwd = ((EditText)findViewById(R.id.change_et_oldpwd)).getText().toString();
                if(!password.equals(oldpwd))
                {
                    Toast.makeText(getApplicationContext(),"输入的旧密码错误",Toast.LENGTH_SHORT).show();
                    return;
                }

                newpwd = ((EditText)findViewById(R.id.change_et_newpwd)).getText().toString();
                newpwdagain = ((EditText)findViewById(R.id.change_et_newpwdAgain)).getText().toString();
                if(newpwd.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"输入的新密码不得为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newpwd.equals(newpwdagain))
                {
                    Toast.makeText(getApplicationContext(),"两次输入的新密码不相同",Toast.LENGTH_SHORT).show();
                    return;
                }
                changePwd();
            }
        });

        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //TODO:发送服务器请求：修改密码
    private void changePwd(){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username", username);
        builder.add("new_password", newpwd);
        builder.add("old_password", password);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api1/updatePassword")
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
                if(Integer.parseInt(result)>0)
                {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result", "change_success");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                else
                {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result", "change_failure");
                    msg.setData(data);
                    handler.sendMessage(msg);
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

    private class PwdHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            if(result.equals("change_success"))
            {
                //更改全局变量
                User user = ((Userinfo)getApplication()).getUser();
                user.setPassword(newpwd);
                ((Userinfo)getApplication()).setUser(user);

                //更改保存在本地的密码
                SharedPreferences sharedPreferences = getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("password", newpwd);
                editor.commit();
                Toast.makeText(getApplicationContext(),"修改密码成功",Toast.LENGTH_SHORT).show();

                ((EditText)findViewById(R.id.change_et_oldpwd)).setText("");
                ((EditText)findViewById(R.id.change_et_newpwd)).setText("");
                ((EditText)findViewById(R.id.change_et_newpwdAgain)).setText("");
            }
            else
            {
                Toast.makeText(getApplicationContext(),"修改密码失败",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    }
}
