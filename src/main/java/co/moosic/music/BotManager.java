package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.VoiceChannel;

import javax.security.auth.login.LoginException;

class BotManager {
    private static JDA _jda;
    public static JDA get_jda(){ return _jda; }

    public static void Initialize() {
        try {
            JDABuilder builder =
                    new JDABuilder(AccountType.BOT)
                    .setToken(Config.discord_token);

            if (isNas()) {
                System.out.println("Enabling native audio sending");
                builder.setAudioSendFactory(new NativeAudioSendFactory());
            }
            _jda = builder.buildBlocking();

            System.out.println("Use this url to add me:\n" + "https://discordapp.com/oauth2/authorize?client_id=" + GetClientId() + "&scope=bot");
        } catch (LoginException | InterruptedException e) {
            System.out.println("Something went wrong while connecting to discord.");
            e.printStackTrace();
            System.exit(1);
        }

        _jda.addEventListener(new MessageHandler());
    }

    private static String GetClientId() {
        return _jda.getSelfUser().getId();
    }

    public static VoiceChannel GetVoiceChannel(String channelId){
        return _jda.getVoiceChannelById(channelId);
    }

    private static boolean isNas() {
        String os = System.getProperty("os.name");
        return (os.contains("Windows") || os.contains("Linux"))
                && !System.getProperty("os.arch").equalsIgnoreCase("arm")
                && !System.getProperty("os.arch").equalsIgnoreCase("arm-linux");

    }
}
