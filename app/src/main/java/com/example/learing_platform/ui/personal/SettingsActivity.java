package com.example.learing_platform.ui.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.learing_platform.MainActivity;
import com.example.learing_platform.R;
import com.example.learing_platform.utils.ActivityCollector;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityCollector.addActivity(this);



        //返回上一页
        findViewById(R.id.settings_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //关于我们
        findViewById(R.id.settings_layout_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        //修改密码
        findViewById(R.id.settings_layout_changePwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChangePwdActivity.class);
                startActivity(intent);
            }
        });


          //退出登录
          findViewById(R.id.settings_layout_logout).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  SharedPreferences sharedPreferences = getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
                  SharedPreferences.Editor editor = sharedPreferences.edit();
                  editor.putBoolean("isLogin",false);
                  editor.putString("username", "");
                  editor.putString("password", "");
                  editor.commit();
                  exit();
              }
          });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }

    private void exit()
    {
        ActivityCollector.finishAll();  // 销毁所有活动
        Intent intent1 = new Intent(this,MainActivity.class);
        startActivity(intent1);
    }


}
