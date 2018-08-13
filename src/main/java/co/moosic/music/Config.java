package co.moosic.music;

import com.kaaz.configuration.ConfigurationOption;

class Config {
    @ConfigurationOption
    public static final String discord_token = "your-discord-token";

    @ConfigurationOption
    public static final String command_prefix = "!";

    @ConfigurationOption
    public static final int volume = 30;

    @ConfigurationOption
    public static final String voice_channel_id = "channel-id";
}
