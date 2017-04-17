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
    private String sshAddress;
    private double cpu;
    private double ram;
    private double ramCap;

    public Server(String displayName, String url,String sshAddress) {
        this.displayName = displayName.replace('#', '_').replace(',','_'); // Remove separators
        this.url = url.replace('#', '_').replace(',','_'); // Remove separators;
        this.sshAddress = sshAddress;
    }

    public double getCpu() { return cpu;}

    public String getSshAddress() { return sshAddress; }

    public String getDisplayName() {
        return displayName;
    }

    public double getRamCap() { return ramCap; }

    public double getRam() { return ram;}

    public String getURL() {
        return url;
    }

    public void setCpu(double cpu) { this.cpu = cpu; }

    public void setDisplayName(String displayName) {this.displayName = displayName;}

    public void setRam(double ram) { this.ram = ram; }

    public void setRamCap(double ramCap) { this.ramCap = ramCap; }

    public void setURL(String url) {this.url = url;}

    public void setSshAddress(String sshAddress) { this.sshAddress = sshAddress; }

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
        out.writeString(sshAddress);
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
        sshAddress = in.readString();
    }
    // Parcelable implementation end
}
