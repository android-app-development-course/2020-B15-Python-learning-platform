package com.example.learing_platform.ui.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learing_platform.MainActivity;
import com.example.learing_platform.R;
import com.example.learing_platform.objects.Knowledge;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.objects.Userinfo;
import com.example.learing_platform.utils.ActivityCollector;
import com.example.learing_platform.utils.home.CardAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditInfoActivity extends AppCompatActivity {

    String TAG ="EditInfoActivity";

    EditinfoHandler handler = new EditinfoHandler();

    private String nickname;
    private String introduction;
    private String email;
    private String sex="";
    private int age;
    private String identity;
    private String school;
    private String vocation;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ActivityCollector.addActivity(this);

        user = ((Userinfo)getApplication()).getUser();

        //显示原始信息
        getPersonal();

        findViewById(R.id.editInfo_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回上一页
                finish();
            }
        });



        findViewById(R.id.editInfo_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = ((TextView)findViewById(R.id.editInfo_et_nickName)).getText().toString();
                introduction = ((TextView)findViewById(R.id.editInfo_et_signature)).getText().toString();
                email = ((TextView)findViewById(R.id.editInfo_et_email)).getText().toString();
                if(!((TextView)findViewById(R.id.editInfo_et_age)).getText().toString().isEmpty())
                    age = Integer.parseInt(((TextView)findViewById(R.id.editInfo_et_age)).getText().toString());
                else
                    age = 0;
                identity = ((TextView)findViewById(R.id.editInfo_et_identity)).getText().toString();
                school = ((TextView)findViewById(R.id.editInfo_et_school)).getText().toString();
                vocation = ((TextView)findViewById(R.id.editInfo_et_vocation)).getText().toString();
                RadioGroup radgroup = (RadioGroup)findViewById(R.id.editInfo_rd_sex);
                for(int i=0;i<radgroup.getChildCount();i++)
                {
                    RadioButton rd = (RadioButton) radgroup.getChildAt(i);
                    if(rd.isChecked())
                    {
                        sex = rd.getText().toString();
                    }
                }
                submitChange();
                setResult(1);
//                finish();
            }
        });

    }

    //TODO: 提交更改到服务器
    private void submitChange() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username",((Userinfo)getApplication()).getUser().getUsername());
        builder.add("nickname", nickname);
        builder.add("introduction",introduction);
        builder.add("age",String.valueOf(age));
        builder.add("sex",sex);
        builder.add("email",email);
        builder.add("identity",identity);
        builder.add("vocation",vocation);
        builder.add("school",school);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api1/updateInfo")
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
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("result",result);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }

    //获取全局变量
    private void getPersonal() {

        ((TextView)findViewById(R.id.editInfo_et_nickName)).setText(user.getNickname());
        ((TextView)findViewById(R.id.editInfo_et_signature)).setText(user.getIntroduction());
        ((TextView)findViewById(R.id.editInfo_et_email)).setText(user.getEmail());
        if(user.getAge()<=0)
            ((TextView)findViewById(R.id.editInfo_et_age)).setText("");
        else
            ((TextView)findViewById(R.id.editInfo_et_age)).setText(user.getAge()+"");
        if(user.getSex().equals("男"))
            ((RadioButton)findViewById(R.id.editInfo_rbtn_man)).setChecked(true);
        else
            ((RadioButton)findViewById(R.id.editInfo_rbtn_woman)).setChecked(true);
        ((TextView)findViewById(R.id.editInfo_et_identity)).setText(user.getIdentity());
        ((TextView)findViewById(R.id.editInfo_et_school)).setText(user.getSchool());
        ((TextView)findViewById(R.id.editInfo_et_vocation)).setText(user.getVocation());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }


    private class EditinfoHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int result = 0;
            try{
                result = Integer.parseInt(data.getString("result"));
            }
            catch(Exception e){
                result = -1;
            }
            finally {
                if(result>0)
                {
                    Toast.makeText(getApplicationContext(),"修改个人信息成功",Toast.LENGTH_SHORT).show();

                    user.setNickname(nickname);
                    user.setAge(age);
                    user.setSex(sex);
                    user.setEmail(email);
                    user.setIdentity(identity);
                    user.setIntroduction(introduction);
                    user.setSchool(school);
                    user.setVocation(vocation);
                    ((Userinfo)getApplication()).setUser(user);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"修改个人信息失败",Toast.LENGTH_SHORT).show();
                }
            }
            super.handleMessage(msg);
        }
    }

}
