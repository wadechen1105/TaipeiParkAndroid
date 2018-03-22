package app.android.wade.taipeiparks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.android.wade.taipeiparks.ui.ParksListAdapter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_URL = "http://data.taipei/opendata/datalist/apiAccess?" +
            "scope=resourceAquire&rid=bf073841-c734-49bf-a97f-3757a6013812";
    private RecyclerView mParksRecycleView;
    private ParksListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParksRecycleView = (RecyclerView) findViewById(R.id.parks_list_view);
        mAdapter = new ParksListAdapter(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mParksRecycleView.setLayoutManager(layoutManager);
        mParksRecycleView.setAdapter(mAdapter);

        if (isNetworkAvailable(this)) {
//            new JsonTask().execute(FILE_URL);  /// async
            parseJSON(FILE_URL); /// rx android
        } else {
            Toast.makeText(this, "Network connection lost ...", Toast.LENGTH_SHORT).show();
        }

    }


    private void parseJSON(final String path) {

        final Function func = new Function<String, String>() {
            @Override
            public String apply(String action) throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                String errMsg = "";

                try {
                    URL url = new URL(action);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    Log.i("RX", "buffer = " + buffer.toString());
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    Log.e("ERR1", e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    Log.e("ERR2", e.getMessage());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        errMsg = e.getMessage();
                        Log.e("ERR3", e.getMessage());
                    }

                    final String passErrMsg = errMsg;

                    if (!passErrMsg.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, passErrMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                return "";
            }
        };


        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Observable.just(path)
                .subscribeOn(Schedulers.io())
                .map(func)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("RX", "onSubscribe");
                        progressDialog.setMessage("Please wait ...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(String result) {
                        Log.i("RX", "onNext : " + result);
                        mAdapter.setItems(ParksInfo.fromJsonString(result));
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("RX", "onError");
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.i("RX", "onComplete");
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conMgr.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


    /// async task
    private class JsonTask extends AsyncTask<String, String, String> {
        ProgressDialog mProgressDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Please wait ...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String errMsg = "";

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                errMsg = e.getMessage();
                Log.e("ERR1", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                errMsg = e.getMessage();
                Log.e("ERR2", e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    Log.e("ERR3", e.getMessage());
                }

                final String passErrMsg = errMsg;

                if (!passErrMsg.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, passErrMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mAdapter.setItems(ParksInfo.fromJsonString(result));
            mAdapter.notifyDataSetChanged();

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

}
