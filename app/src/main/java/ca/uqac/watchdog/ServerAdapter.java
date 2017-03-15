package ca.uqac.watchdog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Sam on 2017-03-15.
 */

public class ServerAdapter extends ArrayAdapter<Server> {

    public ServerAdapter(Context context, int textViewResourceId, ArrayList<Server> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Server server = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_server, parent, false);
        }

        TextView serverName = (TextView) convertView.findViewById(R.id.serverName);
        serverName.setText(server.getDisplayName());
        
        return convertView;
    }
}
