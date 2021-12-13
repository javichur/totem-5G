package adgarcis.com.adgarcisacceso;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class AccesosApplication extends Application {
    private static WeakReference<Context> mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = new WeakReference<>(getApplicationContext());
    }

    public static Context getContext() {
        return (mContext != null) ? mContext.get() : null;
    }

}
