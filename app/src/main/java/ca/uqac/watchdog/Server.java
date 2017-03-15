package ca.uqac.watchdog;

/**
 * Created by Sam on 2017-03-15.
 */

public class Server {
    private String displayName;
    private String url;

    public Server(String displayName, String url) {
        this.displayName = displayName;
        this.url = url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getURL() {
        return url;
    }
}
