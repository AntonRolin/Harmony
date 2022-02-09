package com.anton.chat_application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AppBase: Application() {

    override fun onCreate() {
        super.onCreate()

        //createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val messagesChannel = NotificationChannel(CHANNEL_MESSAGES_ID, "Messages", NotificationManager.IMPORTANCE_DEFAULT)
            messagesChannel.description = "Channel for messages"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(messagesChannel)
        }
    }

    companion object {
        const val CHANNEL_MESSAGES_ID = "messageschannel"
    }
}