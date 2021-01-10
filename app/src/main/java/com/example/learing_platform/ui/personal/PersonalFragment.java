package com.example.learing_platform.ui.personal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.learing_platform.R;
import com.example.learing_platform.objects.Knowledge;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.objects.Userinfo;
import com.example.learing_platform.utils.home.CardAdapter;
import com.example.learing_platform.utils.personal.PhotoPopupWindow;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 个人页面
 */
public class PersonalFragment extends Fragment {

//    protected Handler handler = new PersonalFragment.MyHandler();
    private User user;
    private ImageView portrait;
    private PhotoPopupWindow mPhotoPopupWindow;
    private String TAG = "PersonalFragment";
    private View root;
    private PortraitHandler handler = new PortraitHandler();




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        //加载菜单栏
        final Toolbar toolbar = root.findViewById(R.id.personal_toolbar);
        toolbar.inflateMenu(R.menu.personal_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_editInfo:
                        Intent intent1 = new Intent(getActivity(), EditInfoActivity.class);
                        startActivityForResult(intent1,1);
                        break;
                    case R.id.option_settings:
                        Intent intent2 = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        portrait = root.findViewById(R.id.header_image);
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoPopupWindow = new PhotoPopupWindow(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //没有授权进行权限申请
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }else {
                            selectPic();
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                View rootView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.fragment_personal, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        update(root);
        this.root = root;
        return root;
    }

    private void selectPic()
    {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,0);

    }



    private void uploadpic(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>1000) { //循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        byte[] bytes = baos.toByteArray();
        String picture= Base64.encodeToString(bytes, Base64.DEFAULT).replace(" ","");
        OkHttpClient client = new OkHttpClient();
        Map<String,String> map = new HashMap<>();
        map.put("base64",picture);
        map.put("id",String.valueOf(user.getId()));
        String jsonStr = new Gson().toJson(map);
        RequestBody body = FormBody.create(MediaType.parse("application/json;charset=utf-8"), jsonStr);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api1/updateAvatar")
                .post(body)
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
                    data.putString("result", result);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }catch (Exception e) {

                }
            }
        });
    }

    private class PortraitHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Glide.with(getActivity()).load(result).into(portrait);
            super.handleMessage(msg);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPic();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //选择图片后回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1)
        {
            update(this.root);
        }
        if(resultCode== Activity.RESULT_OK && requestCode == 0)
        {
            Bitmap bitmap1 = null;
            Uri photoUri1 = data.getData();
            try {
                bitmap1 = BitmapFactory.decodeFileDescriptor(getActivity().getContentResolver().openFileDescriptor(photoUri1,"r").getFileDescriptor());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(bitmap1!=null)
            {
                uploadpic(bitmap1);
            }
            mPhotoPopupWindow.dismiss();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void update(final View root) {
        //加载ViewPager
        ViewPager viewPager = root.findViewById(R.id.personal_viewPager);
        viewPager.setAdapter(new PersonalPageFragmentAdapter(getChildFragmentManager()));

        //加载tab
        TabLayout tabLayout = root.findViewById(R.id.tablayout_personal);
        tabLayout.addTab(tabLayout.newTab().setText("帖子"));
        tabLayout.addTab(tabLayout.newTab().setText("历史"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setupWithViewPager(viewPager);

        //加载个人信息
        user = ((Userinfo) getActivity().getApplication()).getUser();
        ((TextView) root.findViewById(R.id.tv_nickname)).setText(user.getNickname());
        if (user.getIntroduction()!=null&&!user.getIntroduction().isEmpty()) {
            ((TextView) root.findViewById(R.id.tv_signature)).setText(user.getIntroduction());
        }
        if (user.getAvatar()!=null)
        {
            if(!user.getAvatar().isEmpty())
            {
                Glide.with(getActivity()).load(user.getAvatar()).into(portrait);
            }
        }
    }



}
