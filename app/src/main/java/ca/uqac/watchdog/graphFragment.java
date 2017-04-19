package ca.uqac.watchdog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private String mTitle;
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
    }

    static graphFragment newInstance(String url, String unit, String col, int nbDP, int time, String title) {
        graphFragment f = new graphFragment();

        Bundle args = new Bundle();
        args.putString("url",url);
        args.putString("unit",unit);
        args.putString("col",col);
        args.putInt("nDP",nbDP);
        args.putInt("time",time);
        args.putString("title",title);
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
        mTitle = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        View titleView = v.findViewById(R.id.frag);

        Spinner spinnerView = (Spinner)v.findViewById(R.id.timeSpinner);
        String[] spinnerItems = new String[]{"1", "2", "3", "4", "5", "6", "12", "24"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinnerView.setAdapter(adapter);
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                mTime = Integer.parseInt(item.toString());
                mGraph.removeAllSeries();
                updateGraph();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mGraph = (GraphView) v.findViewById(R.id.graph);
        mGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show formatted x axis values
                    double val = mTime*60-((value)*((mTime*60)/mNbDP));
                    if(val >= 60){
                        return "-"+Long.toString(Math.round(val/60))+"h";
                    }
                    else{
                        return "-"+Long.toString(Math.round(val))+"m";
                    }
                } else {
                    // show symbol in y values
                    return super.formatLabel(value, isValueX) + " "+ mUnit;
                }
            }
        });
        ((TextView)titleView).setText(mTitle);
        return v;
    }
}