package me.hbj233.wprefix.module

import cn.nukkit.event.player.PlayerChatEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.potion.Effect
import cn.nukkit.scheduler.PluginTask
import me.hbj233.wprefix.WPrefixPlugin
import me.hbj233.wprefix.WPrefixPlugin.Companion.title
import me.hbj233.wprefix.data.LEFT
import me.hbj233.wprefix.data.PlayerWPrefixData
import me.hbj233.wprefix.data.WPrefixData
import me.hbj233.wprefix.util.getFormatWPrefix
import top.wetabq.easyapi.EasyAPI
import top.wetabq.easyapi.api.defaults.*
import top.wetabq.easyapi.config.defaults.SimpleConfigEntry
import top.wetabq.easyapi.config.encoder.advance.SimpleCodecEasyConfig
import top.wetabq.easyapi.listener.AsyncListener
import top.wetabq.easyapi.module.ModuleInfo
import top.wetabq.easyapi.module.ModuleVersion
import top.wetabq.easyapi.module.SimpleEasyAPIModule
import top.wetabq.easyapi.module.defaults.ChatNameTagFormatModule

object WPrefixModule : SimpleEasyAPIModule() {

    private const val MODULE_NAME = "WPrefix"
    private const val AUTHOR = "HBJ233"
    private const val TITLE = "title"
    private const val TITLE_FORMAT = "%wprefix_title%"
    const val WPREFIX_PREFIX_PLACEHOLDER = "%wprefix_prefix%"

    val economyAPI = EconomyAPI

    const val SIMPLE_CONFIG = "wprefixSimpleConfig"
    const val WPREFIX_CONFIG_NAME = "wprefixConfig"
    const val WPREFIX_PREFIX_CONFIG_NAME = "wprefixPrefixConfig"
    const val WPREFIX_PLAYER_CONFIG_NAME = "wprefixPlayerConfig"
//    const val WPREFIX_LISTENER_NAME = "wprefixListener"
    const val WPREFIX_TASK_NAME = "wprefixTask"
    const val WPREFIX_COMMAND_NAME = "wprefixCommand"
    const val WPREFIX_FORMAT = "wprefixFormat"

    lateinit var wprefixConfig : SimpleCodecEasyConfig<WPrefixData>
    lateinit var wprefixPlayerConfig : SimpleCodecEasyConfig<PlayerWPrefixData>


    override fun getModuleInfo(): ModuleInfo = ModuleInfo(
            WPrefixPlugin.instance,
            MODULE_NAME,
            AUTHOR,
            ModuleVersion(1,0,0)
    )

    override fun moduleRegister() {

        val simpleConfig = this.registerAPI(SIMPLE_CONFIG, SimpleConfigAPI(WPrefixPlugin.instance))
                .add(SimpleConfigEntry(TITLE, title))

        title = simpleConfig.getPathValue(TITLE) as String? ?: title

        val eapiChatConfig = ChatNameTagFormatModule.getIntegrateAPI(ChatNameTagFormatModule.CHAT_CONFIG) as SimpleConfigAPI
        val nameTagFormat = eapiChatConfig.getPathValue(ChatNameTagFormatModule.NAME_TAG_FORMAT_PATH) as String
        if (!nameTagFormat.contains(WPREFIX_PREFIX_PLACEHOLDER)) {
            eapiChatConfig.setPathValue(SimpleConfigEntry(ChatNameTagFormatModule.NAME_TAG_FORMAT_PATH, "$WPREFIX_PREFIX_PLACEHOLDER$nameTagFormat"))
        }

        MessageFormatAPI.registerSimpleFormatter(object : SimpleMessageFormatter{
            override fun format(message: String): String = message.replace(TITLE_FORMAT,title)
        })

        MessageFormatAPI.registerFormatter(WPREFIX_FORMAT, PlayerChatEvent::class.java,
                object : MessageFormatter<PlayerChatEvent>{
                    override fun format(message: String, data: PlayerChatEvent): String =
                            getFormatWPrefix(message, data.player)
                })

        wprefixConfig = object : SimpleCodecEasyConfig<WPrefixData>(
                WPREFIX_PREFIX_CONFIG_NAME, WPrefixPlugin.instance, WPrefixData::class.java,
                WPrefixData("""&a[2020]""","This is a example.", LEFT, 0, canStack = true,buffId = 0, buffLevel = 1, price = 2020)
        ){}
        wprefixConfig.init()

        wprefixPlayerConfig = object : SimpleCodecEasyConfig<PlayerWPrefixData>(
                WPREFIX_PLAYER_CONFIG_NAME, WPrefixPlugin.instance, PlayerWPrefixData::class.java,
                PlayerWPrefixData(ownPrefixName = mutableListOf(), usingPrefixName = mutableListOf())
        ){}
        wprefixPlayerConfig.init()

        this.registerAPI(WPREFIX_CONFIG_NAME, ConfigAPI())
                .add(wprefixConfig)
                .add(wprefixPlayerConfig)

        this.registerAPI(WPREFIX_COMMAND_NAME, CommandAPI())
                .add(WPrefixCommand)

        AsyncListenerAPI.add(object : AsyncListener {

            override fun onPlayerJoinEvent(event: PlayerJoinEvent) {
                if (!wprefixPlayerConfig.simpleConfig.containsKey(event.player.name)) {
                    wprefixPlayerConfig.simpleConfig[event.player.name] = wprefixPlayerConfig.getDefaultValue()
                }
            }

        })

        SimplePluginTaskAPI.delayRepeating(40,1,object : (PluginTask<EasyAPI>, Int) -> Unit {
            override fun invoke(p1: PluginTask<EasyAPI>, p2: Int) {
                val playerCollection = WPrefixPlugin.instance.server.onlinePlayers.values
                val targetPlayerConfig  = wprefixPlayerConfig
                playerCollection.forEach { player ->
                    targetPlayerConfig.safeGetData(player.name).usingPrefixName.forEach {
                        val targetPrefixConfig = wprefixConfig.safeGetData(it)
                        val buffId = targetPrefixConfig.buffId
                        val buffLevel = targetPrefixConfig.buffLevel

                        if (buffId != 0){
                            val effect = Effect.getEffect(buffId)
                            if (effect != null) {
                                effect.amplifier = buffLevel
                                effect.duration = 40
                                player.addEffect(effect)
                            }
                        }

                    }
                }
            }

        })


    }

    override fun moduleDisable() {}

}