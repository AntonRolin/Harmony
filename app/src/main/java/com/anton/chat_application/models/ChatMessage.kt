package com.anton.chat_application.models

import android.os.Parcelable
import android.view.View
import com.anton.chat_application.R
import com.anton.chat_application.databinding.UserFromRowChatlogBinding
import com.anton.chat_application.databinding.UserToRowChatlogBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class ChatMessage(val id: String, val text: String, val fromId: String, val username: String, val userProfileImageUrl: String, val timestamp: Long) : Parcelable {
    constructor() : this("", "", "", "", "", -1)
}

class ChatToItem(private val chatMessage: ChatMessage) : BindableItem<UserToRowChatlogBinding>() {
    override fun bind(p0: UserToRowChatlogBinding, p1: Int) {
        p0.messageTextviewTo.text = chatMessage.text
        p0.usernameTextViewTo.text = chatMessage.username

        val uri = chatMessage.userProfileImageUrl
        val targetImageView = p0.profileimageImageviewTo
        Picasso.get().load(uri).into(targetImageView)

        val date = Date(chatMessage.timestamp)
        val format = SimpleDateFormat("dd/MM - HH:mm")
        p0.timestampTextViewTo.text = format.format(date).toString()
    }

    override fun getLayout(): Int {
        return R.layout.user_to_row_chatlog
    }

    override fun initializeViewBinding(p0: View): UserToRowChatlogBinding {
        return UserToRowChatlogBinding.bind(p0)
    }

}

class ChatFromItem(private val chatMessage: ChatMessage) : BindableItem<UserFromRowChatlogBinding>() {
    override fun bind(p0: UserFromRowChatlogBinding, p1: Int) {
        p0.messageTextviewFrom.text = chatMessage.text
        p0.usernameTextViewFrom.text = chatMessage.username

        val uri = chatMessage.userProfileImageUrl
        val targetImageView = p0.profileimageImageviewFrom
        Picasso.get().load(uri).into(targetImageView)

        val date = Date(chatMessage.timestamp)
        val format = SimpleDateFormat("dd/MM - HH:mm")
        p0.timestampTextViewFrom.text = format.format(date).toString()
    }

    override fun getLayout(): Int {
        return R.layout.user_from_row_chatlog
    }

    override fun initializeViewBinding(p0: View): UserFromRowChatlogBinding {
        return UserFromRowChatlogBinding.bind(p0)
    }

}