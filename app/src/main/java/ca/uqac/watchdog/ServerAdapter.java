package ca.uqac.watchdog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
        final Context context = this.getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_server, parent, false);
        }

        TextView serverName = (TextView) convertView.findViewById(R.id.serverName);
        serverName.setText(server.getDisplayName());

        TextView serverURL = (TextView) convertView.findViewById(R.id.serverURL);
        serverURL.setText(server.getURL());

        LinearLayout serverContainer = (LinearLayout) convertView.findViewById(R.id.serverItemContainer);
        serverContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aller à la page des détails du serveur
                Intent i = new Intent(context, ServerDetailsActivity.class);
                context.startActivity(i);
            }
        });

        return convertView;
    }
}
