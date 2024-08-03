package amaiice.dicebot

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.EmbedBuilder
import java.io.File


suspend fun main () {
    val kord = Kord(File("token").readText())

    kord.createGuildChatInputCommand(
        Snowflake(805795039695470592),"dice","DiceRoll"
    ) {
        string("dice","1d100, 1d10みたいに入力して下さい") {
            required = true
        }
        integer("sv","技能値/成功可否を吐いてくれます")
        boolean("if_secret", "シークレットダイスかどうか trueでシークレット化")
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val command = interaction.command
        val response = if(command.booleans["if_secret"] == true) interaction.deferEphemeralResponse() else interaction.deferPublicResponse()
        val sv = command.integers["sv"]
        val dice = command.strings["dice"]!!.split("d").map(String::toInt)

        println(dice)
        val diceEmbed = EmbedBuilder()
        diceEmbed.title = command.users.toString()
        var roll = 0

        val check1d100 = dice[0] == 1 && dice[1] == 100
        for (i in 1..dice[0])
            roll += (0..dice[1]).random()

        val skillcheck = sv!=null

        var check = ""
        var type = -1
        when(roll)  {
            in 0..5 -> {
                check = "クリティカル！(決定的成功)"
                type = 0
            }
            in 96..100 -> {
                check ="ファンブル！(致命的失敗)"
                type = 3
            }
            else -> {
                if (skillcheck) when(roll) {
                    in 6..sv!! -> {
                        check = "成功"
                        type = 1
                    }
                    in sv+1..95 -> {
                        check = "失敗"
                        type = 2
                    }
                    else -> {}
                }
            }
        }
        val embed = EmbedBuilder()
        val embedList = ArrayList<EmbedBuilder>()

        embed.color = Color (when(type) {
            0 -> java.awt.Color.YELLOW.rgb
            1 -> java.awt.Color.CYAN.rgb
            2 -> java.awt.Color.RED.rgb
            3 -> java.awt.Color.BLACK.rgb
            else -> {java.awt.Color.GREEN.rgb}
        })
        embed.author {
            icon = interaction.user.avatar!!.cdnUrl.toUrl()
            name = interaction.user.effectiveName
        }
        embed.description = "**${dice[0]}d${dice[1]}**${if(skillcheck) " 成功値:$sv" else ""} \n--> $roll\n${if(check1d100) check else ""}"

        embedList.add(embed)
        response.respond {
            embeds = embedList
        }

    }

    kord.login()
}