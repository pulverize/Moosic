package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.entities.VoiceChannel;

class MusicPlayer {
    private static AudioPlayerManager _playerManager;
    private static AudioManager _audioManager;
    private static TrackScheduler _trackScheduler;
    private static AudioPlayer _audioPlayer;

    public static void Initialize(){
        _playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(_playerManager);
        AudioSourceManagers.registerLocalSource(_playerManager);
        _playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        System.out.println("Created new player manager");

        VoiceChannel channel = BotManager.GetVoiceChannel(Config.voice_channel_id);
        if (channel == null) {
            System.out.println("Could not find the channel, make sure the ID is correct and that the bot can see it.");
            System.exit(1);
        }

        AttuneToChannel(channel);
    }

    private static void AttuneToChannel(VoiceChannel channel) {
        _audioManager = channel.getGuild().getAudioManager();
        try {
            _audioManager.openAudioConnection(channel);
            System.out.println("Joined designated voice channel " + channel.getName());
        } catch (Exception ex) {
            System.out.println("Failed to join the voice channel! " + ex.getMessage());
            System.exit(1);
        }
        _audioPlayer = _playerManager.createPlayer();
        _audioManager.setSendingHandler(new AudioPlayerSendHandler(_audioPlayer));
        _trackScheduler = new TrackScheduler(_audioPlayer, _playerManager);
        _audioPlayer.addListener(_trackScheduler);
        _audioPlayer.setVolume(Config.volume);
    }

    public static void TrySetVolume(String maybeVolume){
        Short volumeNumber;
        try{
            volumeNumber = Short.parseShort(maybeVolume);
        }catch(Exception ex){
            MessageManager.SendMessage("I didn't understand that volume. Give me a number.");
            return;
        }

        if(volumeNumber > 100 || volumeNumber < 1)
            MessageManager.SendMessage("I need a number between 1 and 100, please.");

        volumeNumber = (short)Math.ceil(volumeNumber.doubleValue() * 1.5);

        _audioPlayer.setVolume(volumeNumber);
        MessageManager.SendMessage("Volume set to " + maybeVolume);
    }

    public static AudioTrack GetPlayingTrack(){
        return _audioPlayer.getPlayingTrack();
    }
}
