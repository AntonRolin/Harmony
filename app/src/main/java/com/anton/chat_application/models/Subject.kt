package com.anton.chat_application.models

import android.os.Parcelable
import android.view.View
import com.anton.chat_application.R
import com.anton.chat_application.databinding.SubjectRowSubjectsBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Subject(val uid: String, val subjectName: String, val groupUid: String) : Parcelable {
    constructor() : this("", "", "")
}

class SubjectItem(val subject: Subject, var latestMessage: ChatMessage): BindableItem<SubjectRowSubjectsBinding>(){

    override fun getLayout(): Int {
        return R.layout.subject_row_subjects
    }

    override fun bind(p0: SubjectRowSubjectsBinding, p1: Int) {
        p0.subjectNameTextView.text = subject.subjectName

        if (latestMessage.id != "-1") {
            p0.usernamePreviewSubjectsTextView.text = latestMessage.username

            if (latestMessage.text.length >= 34)
                p0.latestMessagePreviewTextView.text = latestMessage.text.slice(IntRange(0, 34)) + "..."
            else
                p0.latestMessagePreviewTextView.text = latestMessage.text

            val date = Date(latestMessage.timestamp)
            val format = SimpleDateFormat("EEE - HH:mm")
            p0.timestampPreviewSubjectsTextView.text = format.format(date).toString()

            val uri = latestMessage.userProfileImageUrl
            val targetImageView = p0.profileImagePreviewSubjectsImageView
            Picasso.get().load(uri).into(targetImageView)

        }
        else {
            p0.usernamePreviewSubjectsTextView.visibility = View.INVISIBLE
            p0.latestMessagePreviewTextView.visibility = View.INVISIBLE
            p0.profileImagePreviewSubjectsImageView.visibility = View.INVISIBLE
            p0.timestampPreviewSubjectsTextView.visibility = View.INVISIBLE
        }
    }

    override fun initializeViewBinding(p0: View): SubjectRowSubjectsBinding {
        return SubjectRowSubjectsBinding.bind(p0)
    }
}