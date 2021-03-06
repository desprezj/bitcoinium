
package com.veken0m.bitcoinium.fragments.mining;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veken0m.bitcoinium.R;
import com.veken0m.mining.fiftybtc.FiftyBTC;
import com.veken0m.mining.fiftybtc.Worker;
import com.veken0m.utils.CurrencyUtils;
import com.veken0m.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.List;

public class FiftyBTCFragment extends SherlockFragment {

    private static String pref_50BTCKey = "";
    private static int pref_widgetMiningPayoutUnit = 0;
    private static FiftyBTC data = null;
    private Boolean connectionFail = false;
    private ProgressDialog minerProgressDialog;
    private final Handler mMinerHandler = new Handler();

    public FiftyBTCFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPreferences(getActivity());

        View view = inflater.inflate(R.layout.table_fragment, container, false);
        viewMinerStats(view);
        return view;
    }

    public void onPause() {
        super.onPause();
        mMinerHandler.removeCallbacks(mGraphView);
        minerProgressDialog.dismiss();
    }

    void getMinerStats() {

        try {
            HttpClient client = new DefaultHttpClient();

            HttpGet post = new HttpGet("https://50btc.com/en/api/"
                    + pref_50BTCKey + "?text=1");
            HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();

            // Testing from raw resource
            //InputStream raw = getResources().openRawResource(R.raw.fiftybtc);
            //Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            //data = mapper.readValue(is, FiftyBTC.class);
            data = mapper.readValue(new InputStreamReader(response.getEntity()
                    .getContent(), "UTF-8"), FiftyBTC.class);

        } catch (Exception e) {
            e.printStackTrace();
            connectionFail = true;
        }
    }

    private void viewMinerStats(View view) {
        if (minerProgressDialog != null && minerProgressDialog.isShowing())
            return;

        Context context = view.getContext();
        if (context != null)
            minerProgressDialog = ProgressDialog.show(context, "Working...", "Retrieving Miner Stats", true, false);

        MinerStatsThread gt = new MinerStatsThread();
        gt.start();
    }

    private class MinerStatsThread extends Thread {

        @Override
        public void run() {
            getMinerStats();
            mMinerHandler.post(mGraphView);
        }
    }

    private final Runnable mGraphView = new Runnable() {
        @Override
        public void run() {
            safelyDismiss(minerProgressDialog);
            drawMinerUI();
        }
    };

    private void safelyDismiss(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (connectionFail) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Resources res = getResources();
            String text = String.format(res.getString(R.string.minerConnectionError), "50BTC");
            builder.setMessage(text);
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    void drawMinerUI() {

        View view = getView();

        if (view != null) {
            try {
                TableLayout t1 = (TableLayout) view.findViewById(
                        R.id.minerStatlist);

                Activity activity = getActivity();

                TableRow tr1 = new TableRow(activity);
                TableRow tr2 = new TableRow(activity);
                TableRow tr3 = new TableRow(activity);
                TableRow tr4 = new TableRow(activity);
                TableRow tr5 = new TableRow(activity);
                TableRow tr6 = new TableRow(activity);
                TableRow tr7 = new TableRow(activity);
                TableRow tr9 = new TableRow(activity);

                TextView tvBTCRewards = new TextView(activity);
                TextView tvBTCPayout = new TextView(activity);
                TextView tvHashrate = new TextView(activity);

                tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                tr2.setGravity(Gravity.CENTER_HORIZONTAL);
                tr3.setGravity(Gravity.CENTER_HORIZONTAL);
                tr4.setGravity(Gravity.CENTER_HORIZONTAL);
                tr5.setGravity(Gravity.CENTER_HORIZONTAL);
                tr6.setGravity(Gravity.CENTER_HORIZONTAL);
                tr7.setGravity(Gravity.CENTER_HORIZONTAL);
                tr9.setGravity(Gravity.CENTER_HORIZONTAL);

                String RewardsBTC = "Reward: "
                        + CurrencyUtils.formatPayout(data.getUser().getConfirmed_rewards(), pref_widgetMiningPayoutUnit);
                String Hashrate = "Total Hashrate: "
                        + data.getUser().getHash_rate() + " MH/s\n";
                String Payout = "Total Payout: " + CurrencyUtils.formatPayout(data.getUser().getPayouts(), pref_widgetMiningPayoutUnit);

                tvBTCRewards.setText(RewardsBTC);
                tvBTCPayout.setText(Payout);
                tvHashrate.setText(Hashrate);

                tr1.addView(tvBTCRewards);
                tr2.addView(tvBTCPayout);
                tr3.addView(tvHashrate);

                t1.addView(tr1);
                t1.addView(tr2);
                t1.addView(tr3);

                // WORKER INFO
                List<Worker> workers = data.getWorkers().getWorkers();
                for (Worker worker : workers) {
                    String name = "Miner: " + worker.getWorker_name();
                    String alive = "Alive: " + worker.getAlive();
                    String minerHashrate = "Hashrate: " + worker.getHash_rate()
                            + " MH/s";
                    String shares = "Shares: " + worker.getShares();
                    String lastShare = "Last Share: "
                            + Utils.dateFormat(activity,
                            worker.getLast_share() * 1000);
                    String totalShares = "Total Shares: "
                            + worker.getTotal_shares();

                    TableRow tr10 = new TableRow(activity);
                    TableRow tr11 = new TableRow(activity);
                    TableRow tr12 = new TableRow(activity);
                    TableRow tr13 = new TableRow(activity);
                    TableRow tr14 = new TableRow(activity);

                    TextView tvMinerName = new TextView(activity);
                    TextView tvAlive = new TextView(activity);
                    TextView tvMinerHashrate = new TextView(activity);
                    TextView tvShares = new TextView(activity);
                    TextView tvLastShare = new TextView(activity);
                    TextView tvTotalShares = new TextView(activity);

                    tr10.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr11.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr12.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr13.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr14.setGravity(Gravity.CENTER_HORIZONTAL);

                    tvMinerName.setText(name);
                    tvAlive.setText(alive);
                    tvMinerHashrate.setText(minerHashrate);
                    tvShares.setText(shares);
                    tvLastShare.setText(lastShare);
                    tvTotalShares.setText(totalShares);

                    if (worker.getAlive()) {
                        tvMinerName.setTextColor(Color.GREEN);
                    } else {
                        tvMinerName.setTextColor(Color.RED);
                    }

                    tr9.addView(tvMinerName);
                    tr10.addView(tvMinerHashrate);
                    tr11.addView(tvShares);
                    tr12.addView(tvLastShare);
                    tr13.addView(tvTotalShares);
                    tr14.addView(tvAlive);

                    t1.addView(tr9);
                    t1.addView(tr14);
                    t1.addView(tr10);
                    t1.addView(tr11);
                    t1.addView(tr12);
                    t1.addView(tr13);
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private static void readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        pref_50BTCKey = prefs.getString("50BTCKey", "");
        pref_widgetMiningPayoutUnit = Integer.parseInt(prefs.getString("widgetMiningPayoutUnitPref", "0"));
    }

}
