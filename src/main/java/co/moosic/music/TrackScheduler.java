package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final AudioPlayerManager playerManager;
    private final List<String> _playlist = new ArrayList<>();
    private final Random RANDOM = new Random();

    TrackScheduler(AudioPlayer player, AudioPlayerManager playerManager) {
        try (Scanner scanner = new Scanner(new File("songs.txt"))) {
            while (scanner.hasNextLine()) {
                _playlist.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find songs.txt Playing Bassnectar.");
            _playlist.add("https://www.youtube.com/playlist?list=PLx5V38jft_n-BoMo9Pf-3QlubIK7mMBb4");
        }
        this.playerManager = playerManager;
        this.player = player;
        processTracks();
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    private void processTracks() {
        System.out.println("Processing " + _playlist.size() + " songs");
        for (String song : new ArrayList<>(_playlist)) {
            if (isPlaylist(song)) {
                parsePlaylist(song);
            }
        }
        if (_playlist.isEmpty()) {
            System.out.println("No supported songs found!");
            System.exit(1);
        }
        System.out.println(_playlist.size() + " songs loaded, starting");
    }

    private boolean isPlaylist(String song) {
        for (Pattern pattern : Patterns.validTrackPatterns) {
            if (pattern.matcher(song).matches()) {
                return Patterns.playlistEmbeddedPattern.matcher(song).find() || Patterns.mixEmbeddedPattern.matcher(song).find();
            }
        }
        return true;
    }

    private void parsePlaylist(String song) {
        System.out.println("Found a playlist, parsing");
        try {
            playerManager.loadItem(song, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    _playlist.remove(song);
                    for (AudioTrack tr : playlist.getTracks()) {
                        String uri = tr.getInfo().uri;
                        if (!_playlist.contains(uri)) _playlist.add(uri);
                    }
                    System.out.println("Parsed playlist with " + playlist.getTracks().size() + " songs");
                }

                @Override
                public void noMatches() {
                    _playlist.remove(song);
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    _playlist.remove(song);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        nextTrack();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        nextTrack();
    }

    private String getRandomSong() {
        synchronized (RANDOM) {
            return _playlist.get(RANDOM.nextInt(_playlist.size()));
        }
    }

    private void loadTrack(String randomSong) {
        playerManager.loadItem(randomSong, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                System.out.println("Loaded! " + track.getInfo().title);
                player.startTrack(track, false);
                BotManager.SetPlaying(track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                nextTrack();
            }

            @Override
            public void noMatches() {
                nextTrack();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                nextTrack();
            }
        });
    }

    public void nextTrack() {
        String randomSong = getRandomSong();
        loadTrack(randomSong);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}