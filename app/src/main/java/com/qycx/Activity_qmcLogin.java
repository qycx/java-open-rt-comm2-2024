package com.qycx;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qycx.base.ctxQyMc;
import com.qycx.base.mySaveSecret;
import com.qycx.base.qyMcLogon;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.showInfo_open;
import com.qycx.base.util.Log;
import com.qycx.base.util.StringUtils;
import com.qycx.base.util.ToastUtils;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//    [Activity(Label = "Activity_qmcLogin")]
public class Activity_qmcLogin extends qmcBaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    //    private static final String TAG = Activity_qmcLogin.class.getSimpleName();
    private static int REQUEST_CODE_STORAGE_PERMISSION = 10;
    //

    private static int RC_CAMERA_PERM = 123;
    EditText edit_server;
    EditText edit_usrName;
    EditText edit_passwd;
    TextView btn_login;
    TextView change_passwd;
    LinearLayout linear_layout;
    Context mContext;
    AppSettingsDialog appSettingsDialog;
    //
    AlertDialog m_alert = null;
    String[] mPermissionList = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };
    SharedPreferences sp;

    //
    private void isPermissionOk() {
        if (hasStoragePermission()) {
            startLogin();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    startLogin();
                }
            } else {
                // Ask for one permission
                EasyPermissions.requestPermissions(
                        this,
                        getResources().getString(R.string.permission_content_storage_first),
                        REQUEST_CODE_STORAGE_PERMISSION,
                        mPermissionList);
            }
        }
    }

    //判断手机有没有存储权限
    private boolean hasStoragePermission() {
        return EasyPermissions.hasPermissions(this, mPermissionList);
    }


    //授权结果，分发下去
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        int[] gr = new int[grantResults.length];
        for (int i = 0; i < gr.length; i++) {
            gr[i] = (int) grantResults[i];
        }
        //
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, gr, this);
    }

    @Override
    public void onPermissionsGranted(int p0, List<String> p1) {
        startLogin();
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int p0, List<String> p1) {
        //Log.i(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        //    ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        //   GuiShare.pf_PostMessage(pQyMc.gui.hMainWnd, Consts.CONST_qyWm_postComm, Consts.CONST_qyWmParam_login_canceled, 0);

        //
        if (EasyPermissions.somePermissionPermanentlyDenied(this, p1)) {
            AppSettingsDialog.Builder buider = new AppSettingsDialog.Builder(this);
            buider.setRationale(getResources().getString(R.string.permission_content_storage));
            appSettingsDialog = buider.build();
            appSettingsDialog.show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  create your application here
        setContentView(R.layout.layout_qmclogin);
        sp = getSharedPreferences("SP", MODE_PRIVATE);

        Log.d("qmcLogin: onCreate called");

        mContext = this;

        edit_server = (EditText) findViewById(R.id.editText_server);
        edit_usrName = (EditText) findViewById(R.id.editText_usrName);
        edit_passwd = (EditText) findViewById(R.id.editText_passwd);
        btn_login = (TextView) findViewById(R.id.button_login);
        change_passwd = (TextView) findViewById(R.id.change_passwd);
        change_passwd.setOnClickListener(this);
        if (btn_login != null) {
            btn_login.setOnClickListener(this);
        }

        String str_server = "", str_name = "", str_passwd = "";
        // qyFuncs_Droid.readLoginInfo( str_server,  str_name,  str_passwd);
        //
        String servInitVal  =  "103.85.174.150";
        String usrNameInitVal  =  "yuwm04";
        String pwdInitVal  =  "123456";


        servInitVal="119.57.97.99";
        usrNameInitVal="yuwm";
        pwdInitVal="123456";


        /*
        servInitVal="10.0.1.115";
        usrNameInitVal="ywm4";
        pwdInitVal="123";
         */

        //
        str_server = sp.getString("str_server", servInitVal);
        str_name = sp.getString("str_name", usrNameInitVal);
        str_passwd = sp.getString("str_passwd", pwdInitVal);
        edit_server.setText(str_server);
        edit_usrName.setText(str_name);
        edit_passwd.setText(str_passwd);
        return;
    }


    @Override
    protected void onDestroy() {
        if (m_alert != null) {
            m_alert.dismiss();
        }
        super.onDestroy();
        Log.d("Activity_qmcLogin  OnDestroy");
    }

    @Override
    public void onClick(View v) {

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.d("qmcLogin.onClick err:pQyMc is null");
            return;
        }
        final int id = v.getId();
        if (id == R.id.button_login) {
            Log.d("登录按钮被点击");
            isPermissionOk();
        } else if (id == R.id.button_close) {
            if (m_alert != null) {
                m_alert.dismiss();
                m_alert = null;
            }
        } else if (id == R.id.change_passwd) {
            Intent intent = new Intent(this, Activity_changePassword.class);
            intent.putExtra("params", "");
            startActivity(intent);
        }
    }

    //开始进行登录
    private void startLogin() {
        String str;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.d("qmcLogin.onClick err:pQyMc is null");
            return;
        }
        String str_server = edit_server.getText().toString();
        String str_usrName = edit_usrName.getText().toString();
        String str_passwd = edit_passwd.getText().toString();
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("str_server", str_server);
        edit.putString("str_name", str_usrName);
        edit.putString("str_passwd", str_passwd);
        edit.apply();

        if (pQyMc.bLogon) return;

        if (!StringUtils.isValidIPAddress(str_server)) {
            ToastUtils.showToast("服务器地址不合法");
            return;
        }

        if (0 != qyMcLogon.qyFuncs.doMcClientLogonOK(null, str_server, str_usrName, str_passwd)) {
            displayHint(this, "登录失败", "服务器地址错，或者用户名密码错");
            return;
        }

        Log.d("Activity_qmcLogin: logon ok");
        showInfo_open.qyFuncs.showInfo_open("qmcLogin.logon ok");

        //
        mySaveSecret.qyFuncs_Droid.saveLoginInfo(str_server, str_usrName, str_passwd);
        doFinish("after isPermissionOk");
    }

    public void displayHint(View.OnClickListener lsn, String title, String hint) {
        AlertDialog.Builder builder = null;

        //
        if (m_alert != null) return;

        //
        builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_custom = inflater.inflate(R.layout.layout_alert1, null, false);
        builder.setView(view_custom);
        builder.setCancelable(false);
        m_alert = builder.create();
        Window wnd = m_alert.getWindow();
        wnd.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        //this.WindowManager.LayoutParams lp = wnd.Attributes;

        TextView txt_title = (TextView) view_custom.findViewById(R.id.textView_title);
        if (txt_title != null) {
            txt_title.setText(title);
        }

        TextView txt_hint = view_custom.findViewById(R.id.textView_hint);
        if (txt_hint != null) {
            txt_hint.setText(hint);
        }

        //
        Button btn_close = (Button) view_custom.findViewById(R.id.button_close);
        if (btn_close != null) btn_close.setOnClickListener(lsn);

        m_alert.show();
    }

    @Override
    public void onBackPressed() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        Log.d("qmcLogin. onBackPressed");
        super.onBackPressed();
    }
}

