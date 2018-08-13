package co.moosic.music;

import com.kaaz.configuration.ConfigurationBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import javax.security.auth.login.LoginException;
import java.io.File;


class Login {
    static JDA Jda;

    public static void main(String args[]) {
        try {
            new ConfigurationBuilder(Config.class, new File("bot.cfg")).build(true);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        try {
            if (isNas()) {
                System.out.println("Enabling native audio sending");
                Jda = new JDABuilder(AccountType.BOT)
                        .setToken(Config.discord_token)
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .buildBlocking();
            } else {
                Jda = new JDABuilder(AccountType.BOT)
                        .setToken(Config.discord_token)
                        .buildBlocking();
            }
            Jda.addEventListener(new MessageHandler());
            System.out.println("Use this url to add me:\n" + "https://discordapp.com/oauth2/authorize?client_id=" + Jda.getSelfUser().getId() + "&scope=bot");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        MusicPlayer.Initialize();
        System.out.println("Created new player manager");
        VoiceChannel channel = Jda.getVoiceChannelById(Config.voice_channel_id);
        if (channel == null) {
            System.out.println("Could not find the channel, make sure the ID is correct and that the bot can see it.");
            System.exit(1);
        }
        MusicPlayer.AttuneToChannel(channel);
    }

    private static boolean isNas() {
        String os = System.getProperty("os.name");
        return (os.contains("Windows") || os.contains("Linux"))
                && !System.getProperty("os.arch").equalsIgnoreCase("arm")
                && !System.getProperty("os.arch").equalsIgnoreCase("arm-linux");

    }
}
