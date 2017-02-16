package event;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is allowing others to register a reaction on new uploads by certain YouTubers.
 */
public final class YouTubeUploadWatchDog {
    
    // TODO Idea: Mostly static, the Thread handles every 30 minutes all requests that come in with static methods.
    
    private static final int INTERVAL_IN_MINUTES = 30;
    private static final int INTERVAL_IN_MS = INTERVAL_IN_MINUTES * 60 * 1000;
    
    public static final String API_KEY = "API-YT_TOKEN";    // Do NOT use as actual API key! This is the key for the Config! 
    
    private static YouTube youtube;
    private static String API_TOKEN;                        // Do USE this as the API key! This is the key for accessing the YouTube Data API!
    
    public static void init(final String APIToken) {
        if (youtube != null) return;
        
        playlists = new ConcurrentHashMap<>();
        YouTubeUploadWatchDog.API_TOKEN = APIToken;
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), (HttpRequest r) -> {}).setApplicationName("Oragon").build();
    }
    
    private static class PlaylistInfoContainer  {   // TODO Is this serializable? Make sure to test (Maybe needs to implement Serializable. Not sure at all.)
        private long latestVideoDate;
        private String serverId;
        private String textChannelId;
        
        public PlaylistInfoContainer(String serverId, String textChannelId, long latestVideoDate) {
            this.serverId = serverId;
            this.textChannelId = textChannelId;
            this.latestVideoDate = latestVideoDate;
        }
        
        public PlaylistInfoContainer(String serverId, String textChannelId) {
            this(serverId, textChannelId, 0);
        }
        
        public void setLatestVideoDate(long latestVideoDate) {
            this.latestVideoDate = latestVideoDate;
        }

        public long getLatestVideoDate() {
            return latestVideoDate;
        }

        public String getServerId() {
            return serverId;
        }

        public String getTextChannelId() {
            return textChannelId;
        }
    }
    
    private static Map<String, PlaylistInfoContainer> playlists;
    
    private static Thread watchDogThread;    
    private static Runnable watchDogRunnable = () -> {
        try {
            while (true) {
                Thread.sleep(INTERVAL_IN_MS);
                for (String playlistId : playlists.keySet()) {
                    try {
                        final YouTube.PlaylistItems.List playlistItems = youtube.playlistItems().list("snippet").setKey(API_TOKEN);    // Quota cost: 2
                        final PlaylistItem latestVideo = playlistItems.setPlaylistId(playlistId).setMaxResults(1L)
                                .setFields("items(snippet/publishedAt,snippet/title,snippet/channelTitle)")
                                .execute().getItems().get(0);
                        final long uploadDate =  latestVideo.getSnippet().getPublishedAt().getValue();
                        if (uploadDate <= playlists.get(playlistId).getLatestVideoDate()) continue;
                            // A new video is out!
                        playlists.get(playlistId).setLatestVideoDate(uploadDate);
                        final String videoTitle = latestVideo.getSnippet().getTitle();
                        final String videoCreator = latestVideo.getSnippet().getChannelTitle();
                        // React properly
                    } catch (IOException e) {
                        System.out.println("There was a problem retrieving information from a playlist (ID: \""+playlistId+"\")");
                    }
                }
            }
        } catch (InterruptedException ignored) { }
    };
    
    public static void addPlaylist(final String id, PlaylistInfoContainer info) {
        playlists.put(id, info);
    }
}
