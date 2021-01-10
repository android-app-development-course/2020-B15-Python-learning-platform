package com.example.learing_platform.ui.compiler;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.Userinfo;
import com.example.learing_platform.utils.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CompilerFragment extends Fragment {
    private String TAG = "CompilerFragment";
    private Button TABbtn;
    private Button Gobtn;
    private EditText CodeText;
    private TextView RunResult;

    private MyHandler handler = new MyHandler();

    private TextWatcher textWatcher = new TextWatcher() {
        int tab = 0;
        // 输入文本之前的状态
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        // 输入文本中的状态
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("hi",String.valueOf(start));


        }

        // 输入文本之后的状态
        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public CompilerFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_compiler, container, false);
        TABbtn = root.findViewById(R.id.TABbutton);
        Gobtn = root.findViewById(R.id.compiler_runbtn);
        CodeText = root.findViewById(R.id.code_editText_content);
        RunResult = root.findViewById(R.id.compiler_result);
        CodeText.addTextChangedListener(textWatcher);

        TABbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+"    ");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        Gobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        root.findViewById(R.id.compiler_addbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+"+");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_subbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+"-");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_mulbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+"*");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_divbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+"/");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_leftbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+"(");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_rightbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText(CodeText.getText()+")");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_clearbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeText.setText("");
                CodeText.setSelection(CodeText.getText().length());
            }
        });

        root.findViewById(R.id.compiler_runbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Run(CodeText.getText().toString());

            }
        });



        return root;
    }

    private void Run(String code)
    {
        OkHttpClient client = new OkHttpClient();
        Map<String,String> map = new HashMap<String,String>();
        map.put("code",code);
        final Gson gson = new Gson();
        String json = gson.toJson(map);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);

        //TODO:
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/compile/pythonInterface")
                .post(requestBody)
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
                Gson gson = new Gson();
                Map<String,Object> map = new HashMap<String,Object>();
                map = gson.fromJson(result,map.getClass());
                Log.d(TAG, "onResponse: " + result);
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("result",map.get("response").toString());
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            result = result.substring(1);
            result = result.replaceAll("<br>","\n");
            if(result.indexOf("line")>=0)
                result = result.substring(result.indexOf("line"));
            RunResult.setText(result);
        }
    }

}



