package com.qycx;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qycx.base.guiShare;

//    [Activity(Label = "Activity_notification")]
    public class Activity_notification extends BaseActivity
    {
        private static final String TAG = Activity_notification.class.getSimpleName();
        //
        public static String NOTIFICATION_ID = "NOTIFICATION_ID";
        public static String STR_cmd = "cmd";

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            //  create your application here
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent=getIntent();
            manager.cancel(intent.getIntExtra(NOTIFICATION_ID, -1));
            int iCmd = intent.getIntExtra(STR_cmd, 0);
            guiShare.GuiShare.pf_gui_notify_doCmd.call(iCmd);
            Log.d(TAG,"Activity_notification.onCreate, finish");
            finish();
        }

        //
        public static PendingIntent getDismissIntent(int notificationId, int iCmd, MainActivity context)
        {
            Intent intent = new Intent(context,Activity_notification.class);
            //intent.setFlags(ActivityFlags..NewTask | ActivityFlags.ClearTask);
            intent.putExtra(NOTIFICATION_ID, notificationId);
            intent.putExtra(STR_cmd, iCmd);
            PendingIntent dismissIntent = null;

            try
            {
                dismissIntent = PendingIntent.getActivity(context, iCmd, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                dismissIntent = null;
            }
            return dismissIntent;


        }
}
