package com.eternaljust.msea.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.eternaljust.msea.MainActivity
import com.eternaljust.msea.R
import java.util.*

object RemindersManager {
    private fun createNotificationsChannel(context: Context) {
        // Create the NotificationChannel
        val name = "每日签到提醒"
        val descriptionText = "定时通知打开 Msea 完成签到"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val id = context.getString(R.string.reminder_notification_channel_id_sign)
        val channel = NotificationChannel(id, name, importance)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.description = descriptionText
        channel.setSound(null, audioAttributes)
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun deleteNotificationChannel(context: Context) {
        // The id of the channel.
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val id = context.getString(R.string.reminder_notification_channel_id_sign)
        notificationManager.deleteNotificationChannel(id)
    }

    // https://blog.protein.tech/android-repeat-notification-daily-on-specific-time-c2b0f7788f93
    fun startReminder(
        context: Context,
        reminderId: Int = R.integer.reminder_alarm_request_id_1
    ) {
        createNotificationsChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val hour = SettingInfo.instance.daysignHour
        val minute = SettingInfo.instance.daysignMinute
        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        // https://developer.android.com/about/versions/12/behavior-changes-12#create-immutable-pending-intents

        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        // If the trigger time you specify is in the past, the alarm triggers immediately.
        // if soo just add one day to required calendar
        // Note: i'm also adding one min cuz if the user clicked on the notification as soon as
        // he receive it it will reschedule the alarm to fire another notification immediately
        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
    }

    fun stopReminder(
        context: Context,
        reminderId: Int = R.integer.reminder_alarm_request_id_1
    ) {
        deleteNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmManager.cancel(intent)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        val channelId = context.getString(R.string.reminder_notification_channel_id_sign)

        notificationManager.sendReminderNotification(
            applicationContext = context,
            channelId = channelId
        )
        // Remove this line if you don't want to reschedule the reminder
        RemindersManager.startReminder(context.applicationContext)
    }
}

fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
) {
    val requestCode = 0
    // Create an explicit intent for an Activity in your app
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        "msea://${RouteName.SIGN}".toUri(),
        applicationContext,
        MainActivity::class.java
    )
    val deepLinkPendingIntent: PendingIntent =
        TaskStackBuilder.create(applicationContext!!).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(requestCode, PendingIntent.FLAG_IMMUTABLE)!!
        }

    val text = "打开 Msea 签到，立即获取虫部落 Bit 奖励！"
    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.icon)
        .setContentTitle("每日签到")
        .setContentText(text)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(text)
        )
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        // Set the intent that will fire when the user taps the notification
        .setContentIntent(deepLinkPendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(applicationContext)) {
        // notificationId is a unique int for each notification that you must define
        val notificationId = 1
        notify(notificationId, builder.build())
    }
}

class BootReceiver : BroadcastReceiver() {
    /**
    * restart reminders alarms when user's device reboots
    * */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            RemindersManager.startReminder(context)
        }
    }
}