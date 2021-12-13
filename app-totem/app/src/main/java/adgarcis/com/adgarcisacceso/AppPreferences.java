package adgarcis.com.adgarcisacceso;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

public class AppPreferences {

    public static final String PREFERENCES_FILE_NAME = "prefs_accesos";

    public static final String PREFS_TIMEOUT_OFFLINE = "prefs_timeout_offline";



    public static final String PREFS_ULT_CODIGO = "prefs_ult_codigo";
    public static final String PREFS_ULT_FECHA = "prefs_ult_fecha";
    public static final String PREFS_ULT_RESPUESTA = "prefs_ult_respuesta";

    //parámetros hackathon
    private static final String PREFS_URL_BASE_HACKATHON = "url_base_hackathon";

    private static final String PREFS_API_KEY_HACKATHON = "API_KEY";

    private AppPreferences() {

    }

    private static SharedPreferences getPreferences() {
        Context context;
        context = AccesosApplication.getContext();
        return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void setPrefsUrlBaseHackathon(String urlBaseHackathon) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(AppPreferences.PREFS_URL_BASE_HACKATHON, urlBaseHackathon);
        editor.apply();
    }
    public static String getPrefsUrlBaseHackathon() {
        return getPreferences().getString(AppPreferences.PREFS_URL_BASE_HACKATHON, "http://ec2-35-178-162-113.eu-west-2.compute.amazonaws.com");
    }

    public static void setPrefsApiKeyHackathon(String apiKeyHackathon) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(AppPreferences.PREFS_API_KEY_HACKATHON, apiKeyHackathon);
        editor.apply();
    }
    public static String getPrefsApiKeyHackathon() {
        return getPreferences().getString(AppPreferences.PREFS_API_KEY_HACKATHON, "889e88003d36d012f56a860e3180a5235");
    }


    public static int getPrefsTimeoutOffline() {
        return getPreferences().getInt(AppPreferences.PREFS_TIMEOUT_OFFLINE, 5000);
    }

    public static void setPrefsUltCodigo(String codigo) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(AppPreferences.PREFS_ULT_CODIGO, codigo);
        editor.apply();
    }
    public static String getPrefsUltCodigo() {
        return getPreferences().getString(AppPreferences.PREFS_ULT_CODIGO, "");
    }

    public static void setPrefsUltFecha(long fecha) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(AppPreferences.PREFS_ULT_FECHA, fecha);
        editor.apply();
    }
    public static long getPrefsUltFecha() {
        return getPreferences().getLong(AppPreferences.PREFS_ULT_FECHA, 0);
    }


    public static void setPrefsUltRespuesta(String respuesta) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(AppPreferences.PREFS_ULT_RESPUESTA, respuesta);
        editor.apply();
    }
    public static String getPrefsUltRespuesta() {
        return getPreferences().getString(AppPreferences.PREFS_ULT_RESPUESTA, "");
    }
}
