package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.entities.VoiceChannel;

class MusicPlayer {
    private static AudioPlayerManager _playerManager;

    public AudioPlayerManager get_playerManager() {
        return _playerManager;
    }

    public static void set_playerManager(AudioPlayerManager apm) {
        AudioSourceManagers.registerRemoteSources(apm);
        AudioSourceManagers.registerLocalSource(apm);
        apm.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        MusicPlayer._playerManager = apm;
    }

    private static AudioManager _audioManager;

    public static AudioManager get_audioManager() {
        return _audioManager;
    }

    private static void set_audioManager(AudioManager am) {
        MusicPlayer._audioManager = am;
    }

    private static TrackScheduler _trackScheduler;

    public static TrackScheduler get_trackScheduler() {
        return _trackScheduler;
    }

    private static void set_trackScheduler(TrackScheduler ts) {
        MusicPlayer._trackScheduler = ts;
    }

    private static AudioPlayer _audioPlayer;

    public static AudioPlayer get_audioPlayer() {
        return _audioPlayer;
    }

    private static void set_audioPlayer(AudioPlayer ap) {
        _audioPlayer = ap;
        _audioManager.setSendingHandler(new AudioPlayerSendHandler(ap));
    }

    public static void Initialize(){
        _playerManager = new DefaultAudioPlayerManager();
    }

    public static void AttuneToChannel(VoiceChannel channel) {
        set_audioManager(channel.getGuild().getAudioManager());
        try {
            _audioManager.openAudioConnection(channel);
            System.out.println("Joined designated voice channel " + channel.getName());
        } catch (Exception ex) {
            System.out.println("Failed to join the voice channel! " + ex.getMessage());
            System.exit(1);
        }
        set_audioPlayer(_playerManager.createPlayer());
        set_trackScheduler(new TrackScheduler(_audioPlayer, _playerManager));
        _audioPlayer.addListener(_trackScheduler);
        _audioPlayer.setVolume(Config.volume);
    }

    public static AudioTrack GetPlayingTrack(){
        return _audioPlayer.getPlayingTrack();
    }
}
