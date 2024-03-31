package com.example.mychat

class Group {
    var group_name : String? = null
    var uid :String? = null
    constructor(groupnm:String,uid:String){
        this.group_name = groupnm
        this.uid = uid
    }
    constructor()
}