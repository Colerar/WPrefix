package me.hbj233.wprefix.gui

import cn.nukkit.Player
import me.hbj233.wprefix.WPrefixPlugin
import moe.him188.gui.window.ResponsibleFormWindowSimple
import top.wetabq.easyapi.utils.color

class WPrefixMainGUI(private val player: Player) : ResponsibleFormWindowSimple(
        "${WPrefixPlugin.title} 主面板".color(),
        "&e&l您需要进行什么操作?".color()
) {

    companion object {
        const val ROOT_WPREFIX = "wprefix."
        const val PERMISSION_GUI = ROOT_WPREFIX + "gui."
        const val PERMISSION_GUI_MANAGESELF = PERMISSION_GUI + "manageself"
        const val PERMISSION_GUI_SHOP = PERMISSION_GUI + "shop"
        const val PERMISSION_GUI_DASHBOARD = PERMISSION_GUI + "dashboard"
        const val PERMISSION_GUI_CONFIG_PREFIX = PERMISSION_GUI + "configprefix"
    }

    init {
        addButton("管理自己称号") { player -> player.showFormWindow(WPrefixManageSelfGUI(this, player)) }
        addButton("称号商店") { player -> player.showFormWindow(WPrefixShopGUI(this))}
        if (player.isOp) {
            addButton("管理他人称号") { player -> run { if (player.isOp) player.showFormWindow(WPrefixManageOtherGUI(this)) } }
            addButton("配置称号") { player -> run { if (player.isOp) player.showFormWindow(WPrefixConfigGUI(this)) } }
        }
    }

}