package com.accubits.reltime.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.accubits.reltime.R
import com.accubits.reltime.activity.notification.NotificationActivity
import com.accubits.reltime.activity.notification.NotificationDetailsActivity
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.DataNotificationModel
import com.accubits.reltime.views.home.ContainerActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

private const val TAG = "ReltimeFirebaseMessaging"
@AndroidEntryPoint
class ReltimeFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            var data :DataNotificationModel?
            val gson = Gson()
            val jsonData = remoteMessage.data["notificationData"].toString()
            data = gson.fromJson(jsonData, DataNotificationModel::class.java)
         if(data.type==1){
             sendNotification(data.notificationTitle!!,data.notificationMessage!!,data,data.type);
         }else{
             sendNotification(data.notificationTitle!!,data.notificationMessage!!,null,data.type);
         }
             }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification?.body)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "onNewToken: $token")
        preferenceManager.setFCMToken(token)
    }

    private fun sendNotification(aContentTitle: String, aContentText: String, aData: DataNotificationModel?,aType :Int?) {

        val uniqueId = getRandomNumber()
        var intent : Intent?
        var backIntent : Intent?
        var homeIntent : Intent?
        val pendingIntent :PendingIntent?
        homeIntent = Intent(this, ContainerActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        backIntent = Intent(this, NotificationActivity::class.java)

        if(aData==null){
            if(aType==6 || !preferenceManager.getMomic() || !preferenceManager.getMpin()){
                pendingIntent = PendingIntent.getActivity(
                    this,
                    uniqueId,
                    Intent(),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
                )
            }else{
                intent = Intent(this, NotificationActivity::class.java)
                pendingIntent = PendingIntent.getActivities(
                    this,
                    uniqueId,
                    arrayOf(homeIntent, intent),
                    PendingIntent.FLAG_IMMUTABLE or  PendingIntent.FLAG_ONE_SHOT
                )
            }

        }else{
            intent = Intent(this, NotificationDetailsActivity::class.java)
            intent.putExtra("notification", aData)
            pendingIntent = PendingIntent.getActivities(
                this,
                uniqueId,
                arrayOf(homeIntent,backIntent, intent),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
        }


        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        applicationContext.resources, R.mipmap.ic_launcher
                    )
                )
                .setContentTitle(aContentTitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText(aContentText))
                .setContentText(aContentText)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSilent(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.shouldShowLights()
            channel.shouldVibrate()
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(uniqueId, notificationBuilder.build())
    }

    fun getRandomNumber(): Int {
        val min = 1
        val max = 100
        val random = Random().nextInt(max - min + 1) + min
        val date = "" + Calendar.getInstance().time.time
        val n = if (date.length >= 4) date.substring(date.length - 4) else ""
        val uniqueId = (random.toString() + n).toInt()
        return uniqueId
    }
}