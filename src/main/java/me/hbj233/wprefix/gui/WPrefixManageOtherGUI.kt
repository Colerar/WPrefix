package me.hbj233.wprefix.gui

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementDropdown
import cn.nukkit.form.element.ElementInput
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.response.FormResponseCustom
import cn.nukkit.form.window.FormWindow
import me.hbj233.wprefix.WPrefixPlugin
import me.hbj233.wprefix.module.WPrefixModule
import moe.him188.gui.window.FormSimple
import moe.him188.gui.window.ResponsibleFormWindowCustom
import top.wetabq.easyapi.utils.color

class WPrefixManageOtherGUI(parent: FormWindow) : ResponsibleFormWindowCustom(
        "${WPrefixPlugin.title} 管理他人称号".color()
) {

    init {
        setParent(parent)
        addElement(ElementLabel("&e&l您需要进行什么操作?&r".color()))
        addElement(ElementDropdown("选择玩家", Server.getInstance().onlinePlayers.values
                .fold<Player, ArrayList<String>>(arrayListOf()) {
                    playerList, player -> playerList.add(player.name); playerList
                }.also {
                    it.add(0, "未选择")
                    it.add(1, "自己")
                }, 0))
        addElement(ElementLabel("&e&l或者...输入玩家名:&r".color()))
        addElement(ElementInput("玩家名","玩家名"))
    }

    override fun onClicked(response: FormResponseCustom, player: Player) {
        @Suppress("IMPLICIT_CAST_TO_ANY") val targetPlayer = when {
            (response.getDropdownResponse(1).elementContent.isNotBlank() &&
                    response.getDropdownResponse(1).elementContent != "未选择") -> {
                when(val playerName = response.getDropdownResponse(1).elementContent) {
                    "自己" -> player
                    else -> Server.getInstance().getPlayerExact(playerName)?:null
                }
            }
            response.getInputResponse(3).isNotBlank() -> {
                when (val playerName1 = response.getInputResponse(3)){
                    else -> Server.getInstance().getPlayerExact(playerName1)?:null
                }
            }
            else -> { null }
        }

        if (targetPlayer is Player) {
            player.showFormWindow(WPrefixManageOtherSubGUI(this, targetPlayer))
        } else {
            player.showFormWindow(object : FormSimple("","&c输入错误".color()){})
        }

    }


    override fun onClosed(player: Player) {
        player.showFormWindow(WPrefixMainGUI(player))
    }

}

class WPrefixManageOtherSubGUI (parent: FormWindow,private val p: Player) : ResponsibleFormWindowCustom(
        "${WPrefixPlugin.title} 管理玩家 ${p.name} 的称号".color()
) {

    init {
        setParent(parent)
        addElement(ElementLabel("&e&l您需要进行什么操作?&r".color()))
        addElement(ElementDropdown("操作, 请下拉选择:", mutableListOf("给予","删除")))
        addElement(ElementDropdown("要操作的称号", WPrefixModule.wprefixConfig.simpleConfig.keys.toList()))
    }

    override fun onClicked(response: FormResponseCustom, player: Player) {
        val targetPrefix =  response.getDropdownResponse(2).elementContent
        val targetConfig = WPrefixModule.wprefixPlayerConfig.safeGetData(p.name).ownPrefixName
        when(response.getDropdownResponse(1).elementContent){
            "给予" -> {
                if (!targetConfig.contains(targetPrefix)) {
                    targetConfig.add(targetPrefix)
                    player.sendMessage("&e操作成功, 给玩家 (${p.name}) 添加了此称号 ($targetPrefix) .".color())
                } else {
                    player.sendMessage("&c操作失败, 玩家已有此称号.".color())
                }
            }
            "删除" -> {
                if (targetConfig.contains(targetPrefix)){
                    targetConfig.remove(targetPrefix)
                    player.sendMessage("&c操作成功, 把玩家 (${p.name}) 的称号 ($targetPrefix) 删除了.".color())
                } else {
                    player.sendMessage("&c操作失败, 玩家没有此称号.".color())
                }
            }
            else -> { player.sendMessage("&c操作失败.")}
        }.also {
            WPrefixModule.wprefixPlayerConfig.save()
        }
    }

    override fun onClosed(player: Player) {
        player.showFormWindow(WPrefixManageOtherGUI(WPrefixMainGUI(player)))
    }


}