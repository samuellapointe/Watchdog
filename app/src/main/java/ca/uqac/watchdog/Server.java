package ca.uqac.watchdog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam on 2017-03-15.
 */

public class Server implements Parcelable{
    private String displayName;
    private String url;
    private double cpu;
    private double ram;

    public Server(String displayName, String url) {
        this.displayName = displayName;
        this.url = url;
    }

    public double getCpu() { return cpu;}

    public String getDisplayName() {
        return displayName;
    }

    public double getRam() { return ram;}

    public String getURL() {
        return url;
    }

    public void setCpu(double cpu) { this.cpu = cpu; }

    public void setRam(double ram) { this.ram = ram; }

    // Parcelable implementation start
    // Interface that allows passing objects efficiently in Android
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(displayName);
        out.writeString(url);
    }

    public static final Parcelable.Creator<Server> CREATOR
            = new Parcelable.Creator<Server>() {
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        public Server[] newArray(int size) {
            return new Server[size];
        }
    };

    private Server(Parcel in) {
        displayName = in.readString();
        url = in.readString();
    }
    // Parcelable implementation end
}
