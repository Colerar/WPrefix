package me.hbj233.wprefix

import cn.nukkit.plugin.PluginBase
import me.hbj233.wprefix.module.WPrefixModule
import top.wetabq.easyapi.module.EasyAPIModuleManager
import top.wetabq.easyapi.utils.color

class WPrefixPlugin : PluginBase() {

    override fun onEnable() {
        instance = this
        EasyAPIModuleManager.register(WPrefixModule)
    }

    companion object {
        lateinit var instance : WPrefixPlugin
        var title = "&e&l[&bW&cPrefix&e]&r".color()
    }
}