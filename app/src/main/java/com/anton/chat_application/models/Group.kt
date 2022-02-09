package com.anton.chat_application.models


import android.os.Parcelable
import android.view.View
import com.anton.chat_application.R
import com.anton.chat_application.databinding.GroupRowGroupsBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem
import kotlinx.parcelize.Parcelize


@Parcelize
class Group(val uid: String, val groupName: String, val groupImageUrl: String) : Parcelable {
    constructor() : this("", "", "")
}

class Member(val userUid: String) {
    constructor() : this("")
}

class GroupItem(val group: Group, val memberCount: Int): BindableItem<GroupRowGroupsBinding>() {

    override fun getLayout(): Int {
        return R.layout.group_row_groups
    }

    override fun bind(p0: GroupRowGroupsBinding, p1: Int) {
        p0.groupNameTextView.text = group.groupName
        Picasso.get().load(group.groupImageUrl).into(p0.groupImageCircleview)

        p0.memberCountTextView.text = memberCount.toString()
    }

    override fun initializeViewBinding(p0: View): GroupRowGroupsBinding {
        return GroupRowGroupsBinding.bind(p0)
    }
}