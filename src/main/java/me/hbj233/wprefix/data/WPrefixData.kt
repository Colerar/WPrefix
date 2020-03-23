package me.hbj233.wprefix.data

import top.wetabq.easyapi.utils.color

data class WPrefixData(
        val content : String,
        val description : String,
        val position : String,
        //数字越高越靠前
        val priority : Int,
        val canStack : Boolean,
        val price : Int
) {
    init {
        content.color()
    }
}