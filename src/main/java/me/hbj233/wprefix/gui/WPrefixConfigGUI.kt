package me.hbj233.wprefix.gui

import cn.nukkit.Player
import cn.nukkit.form.window.FormWindow
import me.hbj233.wprefix.WPrefixPlugin
import me.hbj233.wprefix.data.WPrefixData
import me.hbj233.wprefix.module.WPrefixModule
import moe.him188.gui.window.ResponsibleFormWindowSimple
import top.wetabq.easyapi.gui.ConfigGUI
import top.wetabq.easyapi.utils.color

class WPrefixConfigGUI(parent: FormWindow)  : ResponsibleFormWindowSimple(
        "${WPrefixPlugin.title} 管理称号",
        "&e&l请选择一个配置以修改或添加一个配置".color()
){

    init {
        setParent(parent)

        this.addButton("新建配置") { player ->
            val newPrefixGui = ConfigGUI(
                    simpleCodecEasyConfig = WPrefixModule.wprefixConfig,
                    obj = WPrefixModule.wprefixConfig.getDefaultValue(),
                    key = "Example",
                    guiTitle = "${WPrefixPlugin.title} 新增配置",
                    parent = this
            )
            newPrefixGui.setWPrefixConfigGUI()
            newPrefixGui.init()
            player.showFormWindow(newPrefixGui)
        }

        WPrefixModule.wprefixConfig.simpleConfig.entries.forEach {
            this.addButton(it.key) { player ->
                val changeConfigGUI = ConfigGUI(WPrefixModule.wprefixConfig,
                        obj = WPrefixModule.wprefixConfig.safeGetData(it.key),
                        key = it.key,
                        guiTitle = "${it.key}的配置修改页面",
                        parent = this)
                changeConfigGUI.setWPrefixConfigGUI()
                changeConfigGUI.init()
                player.showFormWindow(changeConfigGUI)
            }
        }

        this.addButton("&c删除配置".color()) { player ->
            player.showFormWindow(object : ResponsibleFormWindowSimple("${WPrefixPlugin.title} 配置删除页面".color(),"&e请选择一个配置以删除".color()) {
                init {
                    val targetConfig = WPrefixModule.wprefixConfig.simpleConfig
                    targetConfig.entries.forEach { it ->
                        this.addButton(it.key) { player ->
                            player.showFormWindow(object : ResponsibleFormWindowSimple("", "&c&l你确定删除 ${it.key}?".color()) {
                                init {
                                    addButton("&c&l确定".color()) { player ->
                                        WPrefixModule.wprefixPlayerConfig.simpleConfig.forEach { it2 ->
                                            it2.value.ownPrefixName.remove(it.key)
                                            it2.value.usingPrefixName.remove(it.key)
                                        }
                                        targetConfig.remove(it.key)
                                        WPrefixModule.wprefixConfig.save()
                                        player.sendMessage("${WPrefixPlugin.title} &e你成功删除了名为 ${it.key} 的配置".color())
                                    }
                                    addButton("&a&l取消".color())
                                }
                            })
                        }
                    }
                    this.addButton("返回") { player -> player.showFormWindow(WPrefixMainGUI(player)) }
                }

            })
        }
        this.addButton("返回") { player -> player.showFormWindow(WPrefixMainGUI(player)) }
    }

    override fun onClosed(player: Player) {
        player.showFormWindow(WPrefixMainGUI(player))
    }


    private fun ConfigGUI<WPrefixData>.setWPrefixConfigGUI() {
        val translateMap = linkedMapOf(
                "content" to "内容",
                "description" to "介绍",
                "position" to "位置(只能填 LEFT 或者 RIGHT)",
                "priority" to "优先级",
                "buffId" to "药水效果ID",
                "buffLevel" to "效果等级",
                "canStack" to "能否堆叠",
                "price" to "价格"
        )
        this.setTranslateMap(translateMap)
    }
}