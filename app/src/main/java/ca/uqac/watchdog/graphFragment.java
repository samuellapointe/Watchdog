package ca.uqac.watchdog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PL on 2017-04-17.
 */

public class graphFragment extends DialogFragment {
    private String mUrl;
    private String mUnit;
    private String mCol;
    private int mNbDP;
    private int mTime;
    GraphView mGraph;

    private void updateGraph() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        final StringRequest strRequest = new StringRequest(Request.Method.POST, mUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            LineGraphSeries<DataPoint> result = new LineGraphSeries<>();
                            //Toast.makeText(getActivity(),jObj.getString("1"),Toast.LENGTH_LONG).show();
                            for(int i=0; i<jObj.length()-1; i++){
                                result.appendData(new DataPoint(i+1, jObj.getDouble(Integer.toString(i))),false,mNbDP);
                            }
                            //Toast.makeText(getActivity(),result.toString(),Toast.LENGTH_LONG).show();
                            mGraph.addSeries(result);
                            mGraph.getViewport().setXAxisBoundsManual(true);
                            mGraph.getGridLabelRenderer().setNumHorizontalLabels(mNbDP);
                            mGraph.getViewport().setMinX(1);
                            mGraph.getViewport().setMaxX(mNbDP);
                            mGraph.getViewport().setMinY(0);
                        }
                        catch(JSONException e){
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", "stats");
                params.put("col", mCol);
                params.put("time", Integer.toString(mTime));
                params.put("nbPoints", Integer.toString(mNbDP));
                return params;
            }
        };
        queue.add(strRequest);
        queue.start();
        //return result;
    }

    static graphFragment newInstance(String url, String unit, String col, int nbDP, int time) {
        graphFragment f = new graphFragment();

        Bundle args = new Bundle();
        args.putString("url",url);
        args.putString("unit",unit);
        args.putString("col",col);
        args.putInt("nDP",nbDP);
        args.putInt("time",time);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
        mUnit = getArguments().getString("unit");
        mCol = getArguments().getString("col");
        mNbDP = getArguments().getInt("nDP");
        mTime = getArguments().getInt("time");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        View tv = v.findViewById(R.id.frag);
        mGraph = (GraphView) v.findViewById(R.id.graph);
        mGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show symbol in y values
                    return super.formatLabel(value, isValueX) + " "+ mUnit;
                }
            }
        });
        updateGraph();
        ((TextView)tv).setText(mUrl);
        return v;
    }
}