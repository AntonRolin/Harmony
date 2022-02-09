package com.anton.chat_application.models

import android.view.View
import com.anton.chat_application.R
import com.anton.chat_application.databinding.AddMemberUserRowBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem

class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}

class UserItem(val user: User) : BindableItem<AddMemberUserRowBinding>() {

    override fun bind(p0: AddMemberUserRowBinding, p1: Int) {
        p0.usernameTextView.text = user.username

        val targetImageView = p0.profilePictureImageview
        val uri = user.profileImageUrl
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.add_member_user_row
    }

    override fun initializeViewBinding(p0: View): AddMemberUserRowBinding {
        return AddMemberUserRowBinding.bind(p0)
    }

}