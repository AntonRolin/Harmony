package com.anton.chat_application

import android.view.View
import com.anton.chat_application.databinding.GroupRowGroupsBinding
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem

class Models {

    class User(val uid: String, val username: String, val profileImageUrl: String)

    class Group(val groupName: String, val groupImageUrl: String, val subjects: ArrayList<Subject> = arrayListOf()){
        constructor() : this("", "", arrayListOf())
    }

    class Subject(val subjectName: String)

    class GroupItem(val group: Group): BindableItem<GroupRowGroupsBinding>() {

        override fun getLayout(): Int {
            return R.layout.group_row_groups
        }

        override fun bind(p0: GroupRowGroupsBinding, p1: Int) {
            p0.groupNameTextView.text = group.groupName
            Picasso.get().load(group.groupImageUrl).into(p0.groupImageCircleview)
        }

        override fun initializeViewBinding(p0: View): GroupRowGroupsBinding {
            return GroupRowGroupsBinding.bind(p0)
        }
    }

}