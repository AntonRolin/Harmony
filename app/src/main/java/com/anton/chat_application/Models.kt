package com.anton.chat_application

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class Models {

    class User(val uid: String, val username: String, val profileImageUrl: String)

    class Group(val groupName: String, val groupImageUrl: String, val subjects: ArrayList<Subject> = arrayListOf())

    class Subject(val subjectName: String)

    class GroupItem: Item<GroupieViewHolder>() {
        override fun bind(p0: GroupieViewHolder, p1: Int) {
            TODO("Not yet implemented")
        }

        override fun getLayout(): Int {
            return R.layout.group_row_groups
        }
    }

}