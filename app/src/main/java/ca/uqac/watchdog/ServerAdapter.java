package ca.uqac.watchdog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sam on 2017-03-15.
 */

public class ServerAdapter extends ArrayAdapter<Server> {

    private Context parentContext;

    public ServerAdapter(Context context, int textViewResourceId, ArrayList<Server> items) {
        super(context, textViewResourceId, items);
        this.parentContext = context;
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

        convertView.setTag(server);

        LinearLayout serverContainer = (LinearLayout) convertView.findViewById(R.id.serverItemContainer);
        serverContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aller à la page des détails du serveur
                Intent i = new Intent(context, ServerDetailsActivity.class);
                Server server = (Server)(v.getTag());
                i.putExtra(ServerDetailsActivity.SERVER, server);
                context.startActivity(i);
            }
        });

        serverContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (parentContext instanceof ServerListActivity) {
                    Server server = (Server)(v.getTag());
                    ((ServerListActivity) parentContext).DeleteServer(server);
                    return true;
                } else {
                    return false; // Long click not handled
                }
            }
        });

        return convertView;
    }
}
