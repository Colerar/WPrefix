package me.hbj233.wprefix.gui

import cn.nukkit.Player
import cn.nukkit.form.element.ElementDropdown
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.ElementToggle
import cn.nukkit.form.response.FormResponseCustom
import cn.nukkit.form.window.FormWindow
import cn.nukkit.potion.Effect
import me.hbj233.wprefix.WPrefixPlugin
import me.hbj233.wprefix.data.LEFT
import me.hbj233.wprefix.data.PlayerWPrefixData
import me.hbj233.wprefix.data.RIGHT
import me.hbj233.wprefix.data.WPrefixData
import me.hbj233.wprefix.module.WPrefixModule
import moe.him188.gui.window.ResponsibleFormWindowCustom
import top.wetabq.easyapi.utils.color

class WPrefixShopGUI(parent: FormWindow) : ResponsibleFormWindowCustom(
        "${WPrefixPlugin.title}称号商店".color()
) {

    init {
        setParent(parent)
        addElement(ElementDropdown("&a选择要购买称号".color(),
                WPrefixModule.wprefixConfig.simpleConfig.keys.toMutableList().also {
                    it.add(0, "请选择")
                }, 0))
    }

    override fun onClicked(response: FormResponseCustom, player: Player) {
        val targetPlayerData = WPrefixModule.wprefixPlayerConfig.safeGetData(player.name)
        val response0Content = response.getDropdownResponse(0).elementContent
        val targetPrefixData = if (WPrefixModule.wprefixConfig.simpleConfig.containsKey(response0Content)){
                when(response0Content){
                "请选择" -> null
                else -> {
                    WPrefixModule.wprefixConfig.safeGetData(response0Content)
                }
            }
        } else {
            null
        }

        if (targetPrefixData != null){
            player.showFormWindow(
                    WPrefixShopSubGUI(this, targetPlayerData, targetPrefixData, response.getDropdownResponse(0).elementContent)
            )
        }
    }

    override fun onClosed(player: Player) {
        player.showFormWindow(parent)
    }

}

class WPrefixShopSubGUI(parent: FormWindow,
                        private val targetPlayerData: PlayerWPrefixData,
                        private val targetWPrefixData: WPrefixData,
                        private val targetWPrefixKey: String) : ResponsibleFormWindowCustom(
        "${WPrefixPlugin.title}称号商店".color()) {

    init {
        setParent(parent)
        addElement(ElementLabel(targetWPrefixData.content.color()))
        addElement(ElementLabel("&r&7介绍:\n&e&l${targetWPrefixData.description}".color()))
        if (targetWPrefixData.position == LEFT){
            addElement(ElementLabel("&r&7位置:&e&l左".color()))
        } else if (targetWPrefixData.position == RIGHT){
            addElement(ElementLabel("&r&7位置:&e&l右".color()))
        }
        addElement(ElementLabel("&r&7优先级:${targetWPrefixData.priority}".color()))
        if (targetWPrefixData.canStack) {
            addElement(ElementLabel("&r&7能否堆叠:&e&l能&r".color()))
        } else {
            addElement(ElementLabel("&r&7能否堆叠:&c&l不能&r".color()))
        }
        addElement(ElementLabel("&r&7价格:&e&l${targetWPrefixData.price}".color()))
        val effectName = if (targetWPrefixData.buffId != 0){
            Effect.getEffect(targetWPrefixData.buffId)?.name
        } else { "无" }
        addElement(ElementLabel("&r&7药水效果:&e&l$effectName".color()))
        addElement(ElementLabel("&r&7药水等级:&e&l${targetWPrefixData.buffLevel}".color()))
        addElement(ElementToggle("确定购买",false))
        addElement(ElementToggle("购买后是否立刻佩戴?",false))

    }

    override fun onClicked(response: FormResponseCustom, player: Player) {
        if (response.getToggleResponse(8)){
            targetPlayerData.ownPrefixName.add(targetWPrefixKey)
            WPrefixModule.economyAPI.getMoney(player)?.let {
                if ((it >= targetWPrefixData.price)){
                    WPrefixModule.economyAPI.reduceMoney(player, targetWPrefixData.price.toDouble())
                    if (targetPlayerData.ownPrefixName.contains(targetWPrefixKey)){
                        targetPlayerData.ownPrefixName.add(targetWPrefixKey)
                    } else {
                        player.sendMessage("${WPrefixPlugin.title}&c&l您已经拥有了此称号 (${targetWPrefixData.price}).".color())
                    }
                    var successMsg: String = "${WPrefixPlugin.title} &e你成功花费 ${targetWPrefixData.price} 购买了 $targetWPrefixKey 称号".color()
                    if (response.getToggleResponse(9)){
                        targetPlayerData.usingPrefixName.add(targetWPrefixKey)
                        successMsg += "&e 并成功地佩戴了它.".color()
                    }
                    player.sendMessage(successMsg)
                } else {
                    player.sendMessage("${WPrefixPlugin.title}&c&l您拥有的货币 ($it) 不足以购买此称号 (${targetWPrefixData.price}).".color())
                }
            }
        } else {
            player.sendMessage("${WPrefixPlugin.title}&c&l你取消了本次购买.".color())
        }
    }

    override fun onClosed(player: Player) {
        goBack(player)
    }

}