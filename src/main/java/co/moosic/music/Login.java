package co.moosic.music;

import com.kaaz.configuration.ConfigurationBuilder;

import java.io.File;


class Login {

    public static void main(String args[]) {
        try {
            new ConfigurationBuilder(
                    Config.class,
                    new File("bot.cfg")
            ).build(true);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        BotManager.Initialize();
        MusicPlayer.Initialize();
    }
}
