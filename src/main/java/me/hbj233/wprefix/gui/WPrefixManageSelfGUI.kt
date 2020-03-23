package me.hbj233.wprefix.gui

import cn.nukkit.Player
import cn.nukkit.form.element.ElementToggle
import cn.nukkit.form.response.FormResponseCustom
import cn.nukkit.form.window.FormWindow
import me.hbj233.wprefix.WPrefixPlugin
import me.hbj233.wprefix.module.WPrefixModule
import moe.him188.gui.window.ResponsibleFormWindowCustom
import moe.him188.gui.window.ResponsibleFormWindowSimple
import top.wetabq.easyapi.utils.color

class WPrefixManageSelfGUI(parent: FormWindow, player: Player) : ResponsibleFormWindowSimple(
        "${WPrefixPlugin.title} 管理自己称号".color(),
        "&a请从以下选择一个进行操作.&r".color()
) {

    init {
        setParent(parent)
        val config = WPrefixModule.wprefixPlayerConfig.safeGetData(player.name)
        if (config.usingPrefixName.isEmpty() && config.ownPrefixName.isEmpty()){
            this.addButton("&c&l很遗憾, 你当前没有称号.".color())
        }
        config.usingPrefixName.forEach {
            this.addButton("${it}&r- &a正在使用&r".color()) { player ->
                player.showFormWindow(WPrefixManageSelfSubGUI(this, it, true))
            }
        }
        val configCopied = config.ownPrefixName.toMutableList()
        configCopied.removeIf { config.usingPrefixName.contains(it) }
        configCopied.forEach {
            this.addButton("${it}&r&7- 未使用&r".color()) { player ->
                player.showFormWindow(WPrefixManageSelfSubGUI(this, it, false))
            }
        }
        this.addButton("返回") { p -> goBack(p) }
    }

    override fun onClosed(player: Player) {
        goBack(player)
    }

}

class WPrefixManageSelfSubGUI(parent: FormWindow, private val prefix : String, defaultBoolean: Boolean) : ResponsibleFormWindowCustom(
        "${WPrefixPlugin.title} $prefix 称号面板".color()
){

    init {
        setParent(parent)
        this.addElement(ElementToggle("&e是否使用".color(), defaultBoolean))
    }

    override fun onClicked(response: FormResponseCustom, player: Player) {
        if (response.getToggleResponse(0)) {
            WPrefixModule.wprefixPlayerConfig.safeGetData(player.name).usingPrefixName.add(prefix)
        } else {
            WPrefixModule.wprefixPlayerConfig.safeGetData(player.name).usingPrefixName.remove(prefix)
        }
        WPrefixModule.wprefixPlayerConfig.save()
    }

    override fun onClosed(player: Player) {
        goBack(player)
    }
}
