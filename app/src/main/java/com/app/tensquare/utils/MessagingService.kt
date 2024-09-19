package com.app.tensquare.utils;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import com.app.tensquare.activity.SplashActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.tensquare.BuildConfig
import com.app.tensquare.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


const val channelId = "fcm_default_channel"
const val channelName = BuildConfig.APPLICATION_ID

open class MessagingService : FirebaseMessagingService() {
    var count: Int = 0
    private val PREFS_NAME = "login"
    private val DEVICE_TOKEN = "DEVICE_TOKEN"

    companion object {
        const val TAG: String = "MyFirebaseMsgService"
    }

    override fun onNewToken(p0: String) {

        super.onNewToken(p0)
        //prefs.setDeviceToken(p0)
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getString(DEVICE_TOKEN, "")?.let { Log.e("User_Token P0", it) }
        val editor = prefs.edit()
        editor.putString(DEVICE_TOKEN, p0)
        editor.apply()

        prefs.getString(DEVICE_TOKEN, "")?.let { Log.e("User_Token P1", it) }
    }


    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage) {
        Log.e("onMessageReceived: ", remoteMessage.notification.toString())
        Log.e("onMessageReceived: ", remoteMessage.data.toString())
        Log.e("onMessageReceived: ", remoteMessage.data["message"].toString())
        remoteMessage?.let { message ->
            message.data["message"]?.let {
                Log.i("onMessageReceived:", it)
            }
            //count++
            if (remoteMessage.notification != null) {
                generateNotification(remoteMessage)
            }


        }
    }


    fun generateNotification(messageBody: RemoteMessage) {
        val notificationIntent = Intent(this, SplashActivity::class.java)
        notificationIntent.putExtra("notificationType" , "notificationType")
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

//        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.notification_icon_app_2)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logo_notification_11)

        /*val notificationSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)*/


        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setSmallIcon(R.drawable.notification_icon_app_2)
//                .setLargeIcon(largeIcon)
                .setContentTitle(messageBody.notification?.title)
                //.setContentTitle(messageBody.data["title"])
                .setContentText(messageBody.notification?.body)
                //.setContentText(messageBody.data["message"])
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                //.setSound(notificationSoundUri)
                .setOnlyAlertOnce(true)
//                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder!!.setSmallIcon(R.drawable.logo_notification_11)
            notificationBuilder!!.color = this.resources.getColor(R.color.colorPrimary)
        } else {
            notificationBuilder!!.setSmallIcon(R.drawable.logo_notification_11)
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.color = ContextCompat.getColor(this, R.color.red)
//        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = setupChannels(/*notificationManager*/)
            if (notificationChannel != null) {
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        val notificationID = Random().nextInt(3000)
        notificationManager.notify(notificationID, notificationBuilder.build())
        /*try {
            val notification: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

    }


    fun setupChannels(/*notificationManager: NotificationManager?*/): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val adminChannelDescription = "Device to devie notification"
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            //adminChannel.description = adminChannelDescription
            //adminChannel.enableLights(true)
            //adminChannel.lightColor = Color.RED
            //adminChannel.enableVibration(true)
            //notificationManager?.createNotificationChannel(adminChannel)
            return notificationChannel
        }
        return null

    }


}