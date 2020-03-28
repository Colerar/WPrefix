package me.hbj233.wprefix.util

import cn.nukkit.Player
import me.hbj233.wprefix.WPrefixPlugin
import me.hbj233.wprefix.data.LEFT
import me.hbj233.wprefix.data.RIGHT
import me.hbj233.wprefix.data.WPrefixData
import me.hbj233.wprefix.module.WPrefixModule
import me.hbj233.wprefix.module.WPrefixModule.WPREFIX_PREFIX_PLACEHOLDER
import me.hbj233.wprefix.module.WPrefixModule.wprefixPlayerConfig
import top.wetabq.easyapi.utils.color

fun getFormatWPrefix(message : String, playerName: String): String {
    var prefix = ""
    val wplayerConfig = wprefixPlayerConfig.safeGetData(playerName)

    if (wplayerConfig.usingPrefixName.isNotEmpty()) {
        val targetPlayer =  WPrefixPlugin.instance.server.getPlayer(playerName)
        if (targetPlayer is Player){
            val wprefixCollection: MutableCollection<WPrefixData> = mutableListOf()

            if (wplayerConfig.usingPrefixName.isNotEmpty()){
                wplayerConfig.usingPrefixName.forEach {
                    val wprefixConfig = WPrefixModule.wprefixConfig.safeGetData(it)
                    wprefixCollection.add(wprefixConfig)
                }

                wprefixCollection.sortedBy { it.priority }

                wprefixCollection.forEach {
                    val prefixContent = it.content.color()
                    if (it.canStack) {
                        when (it.position) {
                            LEFT -> prefix = prefixContent + prefix
                            RIGHT -> prefix += prefixContent
                        }
                    } else if (wprefixCollection.size == 1 && !it.canStack) {
                        when (it.position) {
                            LEFT -> prefix = prefixContent + prefix
                            RIGHT -> prefix += prefixContent
                        }
                    } else targetPlayer.sendMessage("${WPrefixPlugin.title}&c 您佩戴了一个不可堆叠的称号 (${it.content}&r), 因此其无法显示.".color())
                }
            }
        }
    }

    return message.replace(WPREFIX_PREFIX_PLACEHOLDER, prefix)
}