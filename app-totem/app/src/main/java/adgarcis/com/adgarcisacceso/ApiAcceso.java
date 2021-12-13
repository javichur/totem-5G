package adgarcis.com.adgarcisacceso;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ApiAcceso extends AsyncTask<Void, Void, String> {
    private Context context;

    private String success = "";

    private String id;
    private static String mEntradaTexto = "";

    private int tipo = 1;

    private static ProgressDialog dialog;

    ApiAcceso(Context context, String id, int tipo) {
        this.id = id;
        this.context = context;
        this.tipo = tipo;
    }

    @Override
    protected  void onPreExecute() {
        checkExecuteDialog();
    }

    @Override
    protected String doInBackground(Void... params) {
        success = "";
        try {

            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = AppPreferences.getPrefsTimeoutOffline();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket =  AppPreferences.getPrefsTimeoutOffline();
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (tipo == 1) {
                nameValuePairs.add(new BasicNameValuePair("action", "in"));
            } else {
                nameValuePairs.add(new BasicNameValuePair("action", "out"));
            }
            nameValuePairs.add(new BasicNameValuePair("id", id));
            HttpPost httppost = new HttpPost(AppPreferences.getPrefsUrlBaseHackathon() + "/api/readNfc");
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Auth-Token", AppPreferences.getPrefsApiKeyHackathon());
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse resp1 = httpclient.execute(httppost);
            if (resp1.getStatusLine().getStatusCode() == 200) {
                HttpEntity ent = resp1.getEntity();
                success = EntityUtils.toString(ent);
                System.out.println("SUCCESS: "+success);

                try {
                    JSONObject response = new JSONObject(success);
                    if (response.has("error")) {
                        if (response.getBoolean("error")) {
                            if (response.has("message")) {
                                success = "\uFEFF{\"error\":true,\"mensaje\":\" " + response.getString("message") +" \"}";;
                            } else {
                                MainActivity.runOnUI(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(MainActivity.mContainer, "Server error, try again later.", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            success = "\uFEFF{\"error\":false,\"mensaje\":\" " + response.getString("message") +" \"}";
                        }
                    } else {
                        MainActivity.runOnUI(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(MainActivity.mContainer, "Server error, try again later.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(MainActivity.mContainer, "Server error, try again later.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (resp1.getStatusLine().getStatusCode() == 400){
                HttpEntity ent = resp1.getEntity();
                success = EntityUtils.toString(ent);
                System.out.println("SUCCESS: "+success);

                try {
                    JSONObject response = new JSONObject(success);
                    if (response.has("error")) {
                        if (response.getBoolean("error")) {
                            if (response.has("message")) {
                                success = "\uFEFF{\"error\":true,\"mensaje\":\" " + response.getString("message") +" \"}";
                            } else {
                                MainActivity.runOnUI(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(MainActivity.mContainer, "Server error, try again later.", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            success = "\uFEFF{\"error\":true,\"mensaje\":\" " + response.getString("message") +" \"}";
                        }
                    } else {
                        MainActivity.runOnUI(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(MainActivity.mContainer, "Server error, try again later.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(MainActivity.mContainer, "Server error, try again later.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
            MainActivity.runOnUI(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(MainActivity.mContainer, "Network error, try again later.", Snackbar.LENGTH_SHORT).show();
                }
            });

            return null;
        }

        return success;
    }

    @Override
    protected void onPostExecute(String result) {
        if (dialog != null) {
            dialog.dismiss();
        }

        System.out.println(success);

        try {
            JSONObject object = new JSONObject(success);

            boolean error = Boolean.FALSE;
            String texto1 = "";
            String texto2 = "";
            String titulo;

            if (object.has("error")) {
                error = object.getBoolean("error");
            }

            if (error) {
                titulo = "ERROR";
                AppPreferences.setPrefsUltCodigo("");
                AppPreferences.setPrefsUltRespuesta("");
                AppPreferences.setPrefsUltFecha(0);
            } else {
                titulo = "PROCEED";

                AppPreferences.setPrefsUltCodigo(id.toUpperCase());
                long ahora = System.currentTimeMillis();
                Date date = new Date(ahora);
                AppPreferences.setPrefsUltFecha(date.getTime());

                if (object.has("titulo")) {
                    AppPreferences.setPrefsUltRespuesta(object.getString("titulo"));
                } else {
                    AppPreferences.setPrefsUltRespuesta(object.getString("mensaje"));
                }
            }

            if (object.has("titulo")) {
                titulo = object.getString("titulo");
            }

            if (object.has("texto1")) {
                texto1 = object.getString("texto1");
            } else if (!mEntradaTexto.isEmpty()) {
                texto1 = mEntradaTexto;
            }

            if (object.has("texto2")) {
                texto2 = object.getString("texto2");
            }

            showAlert(titulo, object.getString(Constants.PARAM_MENSAJE), mEntradaTexto, error, texto1, texto2);

            mEntradaTexto = "";

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkExecuteDialog() {

                MainActivity.runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        if (success == null) {
                            if (dialog == null) {
                                dialog = ProgressDialog.show(context, context.getString(R.string.cargando_informacion), context.getString(R.string.buscando_pulsera), true);
                            }
                            dialog.show();
                        } else if (success.isEmpty()) {
                            if (dialog == null) {
                                dialog = ProgressDialog.show(context, context.getString(R.string.cargando_informacion), context.getString(R.string.buscando_pulsera), true);
                            }
                            dialog.show();
                        }
                    }
                });

    }

    public static void showAlert(String head,String msg, String entradaTexto, boolean error, String texto1, String texto2) {
        MainActivity.setBackgroundCardView(head, msg, entradaTexto, error, texto1, texto2);
    }
}