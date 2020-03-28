package me.hbj233.wprefix.module

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import me.hbj233.wprefix.gui.WPrefixMainGUI
import top.wetabq.easyapi.command.EasyCommand
import top.wetabq.easyapi.command.EasySubCommand

object WPrefixCommand : EasyCommand("wprefix", "WPrefix's Command"){

    init {
        this.aliases = arrayOf("wpf")

        subCommand.add(object : EasySubCommand("gui"){
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender is Player) {
                    sender.showFormWindow(WPrefixMainGUI(sender))
                }
                return true
            }

            override fun getAliases(): Array<String>? = arrayOf("g","ui")

            override fun getDescription(): String = "Open WPrefix gui."

            override fun getParameters(): Array<CommandParameter>? = null

        })
        loadCommandBase()
    }

}