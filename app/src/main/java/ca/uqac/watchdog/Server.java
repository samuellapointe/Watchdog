package ca.uqac.watchdog;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Sam on 2017-03-15.
 */

public class Server implements Parcelable{
    private String displayName;
    private String url;
    private double cpu;
    private double ram;
    private double ramCap;

    public Server(String displayName, String url) {
        this.displayName = displayName.replace('#', '_').replace(',','_'); // Remove separators
        this.url = url.replace('#', '_').replace(',','_'); // Remove separators;
    }

    public double getCpu() { return cpu;}

    public String getDisplayName() {
        return displayName;
    }

    public double getRamCap() { return ramCap; }

    public double getRam() { return ram;}

    public String getURL() {
        return url;
    }

    public void setCpu(double cpu) { this.cpu = cpu; }

    public void setRam(double ram) { this.ram = ram; }

    public void setRamCap(double ramCap) { this.ramCap = ramCap; }

    // Parcelable implementation start
    // Interface that allows passing objects efficiently in Android
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(cpu);
        out.writeString(displayName);
        out.writeDouble(ram);
        out.writeString(url);
        out.writeDouble(ramCap);
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
        cpu = in.readDouble();
        displayName = in.readString();
        ram = in.readDouble();
        url = in.readString();
        ramCap = in.readDouble();
    }
    // Parcelable implementation end
}
