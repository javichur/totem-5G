package adgarcis.com.adgarcisacceso;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acs.smartcard.Reader;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.tonnyl.light.Light;


import static adgarcis.com.adgarcisacceso.NavigationDrawerFragment.mDrawerListView;



public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private static int LUZ_APAGADA = 0;
    private static int LUZ_AZUL = 1;
    private static int LUZ_VERDE = 2;
    private static int LUZ_CYAN = 3;
    private static int LUZ_ROJA = 4;
    private static int LUZ_MAGENTA = 5;
    private static int LUZ_AMARILLA = 6;
    private static int LUZ_BLANCA = 7;

    private static int[] LED_R = new int[]{461, 462};
    private static int[] LED_G = new int[]{460, 464};
    private static int[] LED_B = new int[]{463, 432};

    private static int LADO_ENTRADA = 1;
    private static int LADO_SALIDA = 2;
    private static int LADO_AMBOS = 3;

    private static byte[] ENABLE_BUZZER = new byte[]{ (byte)0xE0, (byte)0x00, (byte)0x00,(byte)0x21, (byte)0x01, (byte)0x6F};
    private static byte[] DISABLE_BUZZER = new byte[]{ (byte)0xE0, (byte)0x00, (byte)0x00,(byte)0x21, (byte)0x01, (byte)0x67};
    private static byte[] BEEP_BUZZER = new byte[]{ (byte)0xE0, (byte)0x00, (byte)0x00,(byte)0x28, (byte)0x01, (byte)0x32};

    private final int MY_PERMISSIONS_REQUEST_READ_IMEI = 209;

    private static Context context;

    public static TextView tv_saldo;
    public static TextView textViewSaveSettings;
    public static EditText editTextUrlServer;
    public static EditText editTextApiKey;

    public static TextView mTextViewVersion;
    public static LinearLayout b;
    public static Activity activity;


    public static FrameLayout mContainer;



    public static int sectionNumber = 0;


    public static CardView mCardViewInfo;
    public static RelativeLayout mLinearLayoutHead;
    public static TextView mTextViewHead;
    public static TextView mTextViewMsg;
    public static TextView mTextViewText1;
    public static TextView mTextViewText2;

    public static String mStringuID = "";

    public static Handler UIHandler;

    public static int positionFragment;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public MainActivity() throws IOException {
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }


    private PulseraManager pulseraManager;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private GoogleApiClient client;

    private static MediaPlayer mp;


    public static View rootView;

    FragmentManager fragmentManager;

    public static int seccionActual;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static UsbManager mManager;
    private static Reader mReader;
    private static PendingIntent mPermissionIntent;
    private static final String[] stateStrings = { "Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific" };
    public static boolean encendido;

    private static Process suProcess;

    static {
        try {
            suProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DataOutputStream os =  new DataOutputStream(suProcess.getOutputStream());

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {

                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if (device != null) {

                            // abrir reader
                            System.out.println("Abriendo reader: " + device.getDeviceName() + "...");
                            new OpenTask().execute(device);
                        }
                    } else {

                        System.out.println("Permiso denegado para el dispositivo "
                                + device.getDeviceName());
                        setLectorNfc();
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (device != null && device.equals(mReader.getDevice())) {

                        // cerrar reader
                        System.out.println("Cerrando reader...");
                        new CloseTask().execute();
                    }
                }
            }
        }
    };

    public static ArrayList<String> listaComandos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D47A1")));

        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        activity = this;
        mContainer = (FrameLayout) findViewById(R.id.container);

        setLectorNfc();

        try {
            confLedPins();
        } catch (IOException e) {
            e.printStackTrace();
        }

        encendido = false;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
    }

    private void confLedPins() throws IOException {

        os.writeBytes("echo " + LED_R[0] + " > /sys/class/gpio/export\n");
        os.flush();
        os.writeBytes("echo out > /sys/class/gpio/gpio" + LED_R[0] + "/direction\n");
        os.flush();
        os.writeBytes("echo " + LED_R[1] + " > /sys/class/gpio/export\n");
        os.flush();
        os.writeBytes("echo out > /sys/class/gpio/gpio" + LED_R[1] + "/direction\n");
        os.flush();
        os.writeBytes("echo " + LED_G[0] + " > /sys/class/gpio/export\n");
        os.flush();
        os.writeBytes("echo out > /sys/class/gpio/gpio" + LED_G[0] + "/direction\n");
        os.flush();
        os.writeBytes("echo " + LED_G[1] + " > /sys/class/gpio/export\n");
        os.flush();
        os.writeBytes("echo out > /sys/class/gpio/gpio" + LED_G[1] + "/direction\n");
        os.flush();
        os.writeBytes("echo " + LED_B[0] + " > /sys/class/gpio/export\n");
        os.flush();
        os.writeBytes("echo out > /sys/class/gpio/gpio" + LED_B[0] + "/direction\n");
        os.flush();
        os.writeBytes("echo " + LED_B[1] + " > /sys/class/gpio/export\n");
        os.flush();
        os.writeBytes("echo out > /sys/class/gpio/gpio" + LED_B[1] + "/direction\n");
        os.flush();
    }

    private static void controlReader(byte[] command) {
        byte[] response = new byte[6];
        int responseLength;
        try {
            mReader.control(1, 3500, command, command.length, response, response.length);

            if (response[5] == command[5]) {
                System.out.println("Comando de control enviado correctamente");
            } else {
                System.out.println("Error enviando el comando de control");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLectorNfc() {

        mManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        mReader = new Reader(mManager);

        mReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {
                if (currState == Reader.CARD_PRESENT) {
                    pulseraManager = new PulseraManager(mReader, slotNum, new PulseraManager.OnReadOrWriteDataListener() {
                        @Override
                        public void onReadSuccess() {
                            String mStringIdPulsera = pulseraManager.getIdBloquePulsera();

                            if (AppPreferences.getPrefsUltCodigo().equals(mStringIdPulsera.toUpperCase()) && check10seconds(AppPreferences.getPrefsUltFecha()) && sectionNumber != 4) {
                                setBackgroundCardView("You have scanned the same ticket", "The previous message was:\n" + AppPreferences.getPrefsUltRespuesta(), "", Boolean.TRUE, "", "");

                            } else {
                                ApiAcceso apiAcceso;
                                try {
                                    switch (sectionNumber) {
                                        case 1:
                                                apiAcceso = new ApiAcceso(activity, mStringIdPulsera, 1);
                                                apiAcceso.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                apiAcceso.get(500, TimeUnit.MILLISECONDS);
                                            break;
                                        case 2:
                                                apiAcceso = new ApiAcceso(activity, mStringIdPulsera, 1);
                                                apiAcceso.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                apiAcceso.get(500, TimeUnit.MILLISECONDS);
                                            break;
                                        case 3:
                                                apiAcceso = new ApiAcceso(activity, mStringIdPulsera, 2);
                                                apiAcceso.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                apiAcceso.get(500, TimeUnit.MILLISECONDS);
                                            break;
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (TimeoutException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onReadInvalidData() {
                        }

                        @Override
                        public void onWritingProcessEnd() {
                        }
                    });

                    pulseraManager.conectarYleerDatos();
                }
            }
        });

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceiver, filter);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_IMEI);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_IMEI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_IMEI: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                }

                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (sectionNumber == 5) {
            replaceFragment(positionFragment);
        }
    }

    public static void pedirPermisosLectorNfc() {
        for (UsbDevice device : mManager.getDeviceList().values()) {
            if (seccionActual == 1) {
                if (device != null && device.getDeviceName().equals("/dev/bus/usb/001/003")){
                    mManager.requestPermission(device, mPermissionIntent);
                    break;
                }
            } else if (seccionActual == 2) {
                if (device != null && device.getDeviceName().equals("/dev/bus/usb/001/004")){
                    mManager.requestPermission(device, mPermissionIntent);
                    break;
                }
            } else {
                if (device != null && device.getDeviceName().equals("/dev/bus/usb/001/003")){
                    mManager.requestPermission(device, mPermissionIntent);
                    break;
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.closeDrawer();
        }
    }

    private static boolean check10seconds(long fecha) {
        long ahora = System.currentTimeMillis();
        Date date = new Date(ahora);

        long diffInMs = date.getTime() - fecha;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        if (diffInSec < 10) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (MainActivity.getContext() != null) {
            replaceFragment(position);
        } else {
            replaceFragment(1);
        }

    }

    private void replaceFragment(int position) {
        if (position == 0 || position == 6) {
            position = 1;
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 2:
                mTitle = getString(R.string.title_section1);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                break;
            case 4:
                mTitle = getString(R.string.configuracion);
                break;
        }

        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0D47A1")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            getMenuInflater().inflate(R.menu.menu_actionbar, menu);
            //menuItem = menu.getItem(0).setVisible(false);
            return true;
        }

        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                
                Uri.parse("android-app://adgarcis.com.adgarcisacceso/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                
                Uri.parse("android-app://adgarcis.com.adgarcisacceso/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static void setBackgroundCardView(String head, String mensaje, String entradaTexto, boolean error, String texto1, String texto2) {

        MainActivity.runOnUI(new Runnable() {
            @Override
            public void run() {
                String msg = mensaje;
                mTextViewHead.setText(head);
                mTextViewMsg.setText(msg);
                if (msg.equals("¡Bienvenido!")) {
                    if (texto1.contains("Hora de acceso:")) {
                        mTextViewText1.setText("");
                        mTextViewText2.setText("");
                    } else {
                        mTextViewText1.setText(texto1);
                        mTextViewText2.setText(texto2);

                        if (!texto1.isEmpty()) {
                            msg = msg + "\n" + texto1;
                        }

                        if (!texto2.isEmpty()) {
                            msg = msg + "\n" + texto2;
                        }
                    }
                } else {
                    mTextViewText1.setText(texto1);
                    mTextViewText2.setText(texto2);

                    if (!texto1.isEmpty()) {
                        msg = msg + "\n" + texto1;
                    }

                    if (!texto2.isEmpty()) {
                        msg = msg + "\n" + texto2;
                    }
                }

                setAnimationCardView(error, head, msg);

            }
        });

    }

    private static void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private static void setAnimationCardView(boolean error, String head, String msg) {
        ColorDrawable[] color;
        int color_luces = 0;
        int duty = 100;


        if (head.equals("You have scanned the same ticket")) {

            color_luces = LUZ_AMARILLA;

            color = new ColorDrawable[]{new ColorDrawable(Color.WHITE), new ColorDrawable(Color.YELLOW)};

        } else {
            if (error) {

                color_luces = LUZ_ROJA;

                color = new ColorDrawable[]{new ColorDrawable(Color.WHITE), new ColorDrawable(Color.RED)};

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controlReader(BEEP_BUZZER);
                    }
                }).start();

            } else {
                color_luces = LUZ_VERDE;

                color = new ColorDrawable[]{new ColorDrawable(Color.WHITE), new ColorDrawable(Color.GREEN)};
            }
        }

            int finalColor_luces = color_luces;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // apagar azules
                        if (sectionNumber == 1 || sectionNumber == 2) {
                            setLeds(duty, LUZ_APAGADA, LADO_AMBOS);
                        } else if (sectionNumber == 3) {
                            setLeds(duty, LUZ_APAGADA, LADO_AMBOS);
                        }
                        // encender colores
                        setLeds(duty, finalColor_luces, LADO_AMBOS);
                        TimeUnit.MILLISECONDS.sleep(500);
                        setLeds(duty, LUZ_APAGADA, LADO_AMBOS);

                        //encender azules
                        if (sectionNumber == 1 || sectionNumber == 2) {
                            setLeds(duty, LUZ_AZUL, LADO_SALIDA);
                        } else if (sectionNumber == 3) {
                            setLeds(duty, LUZ_AZUL, LADO_ENTRADA);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        TransitionDrawable transInfo = new TransitionDrawable(color);
        TransitionDrawable transHead = new TransitionDrawable(color);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            mCardViewInfo.setBackground(transInfo);
            mLinearLayoutHead.setBackground(transHead);
        } else {
            mCardViewInfo.setBackgroundDrawable(transInfo);
            mLinearLayoutHead.setBackgroundDrawable(transHead);
        }

        transInfo.startTransition(800);
        transInfo.reverseTransition(300);

        transHead.startTransition(800);
        transHead.reverseTransition(300);
    }

    public static String toNumeralString(final Boolean input) {
        if (input == null) {
            return "null";
        } else {
            return input.booleanValue() ? "1" : "0";
        }
    }

    private static void setPin(boolean pwr, int pin) throws IOException {
        os.writeBytes("echo " + toNumeralString(pwr) + " > /sys/class/gpio/gpio" + pin + "/value\n");
        os.flush();
    }


    private static void setLeds(int duty, int color, int side) throws IOException {

        long t_on = (duty / 10);
        long t_off = (10 - (duty / 10));
        long delay_yellow = ((t_on) / 4);

        if (null != os) {
            if (duty == 100) {
                switch (color) {
                    case 0: // Luces apagadas
                        if (side == 1 || side == 3) {
                            setPin(false, LED_R[0]);
                            setPin(false, LED_G[0]);
                            setPin(false, LED_B[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(false, LED_R[1]);
                            setPin(false, LED_G[1]);
                            setPin(false, LED_B[1]);
                        }
                        break;

                    case 1: // Luz color AZUL (B)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_B[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_B[1]);
                        }
                        break;

                    case 2: // Luz color VERDE (G)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_G[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_G[1]);
                        }
                        break;

                    case 3: // Luz color CYAN (G+B)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_G[0]);
                            setPin(true, LED_B[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_G[1]);
                            setPin(true, LED_B[1]);
                        }
                        break;

                    case 4: // Luz color ROJO (R)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_R[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_R[1]);
                        }
                        break;

                    case 5: // Luz color MAGENTA (R+B)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_R[0]);
                            setPin(true, LED_B[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_R[1]);
                            setPin(true, LED_B[1]);
                        }
                        break;

                    case 6: // Luz color AMARILLO (G+R)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_R[0]);
                            setPin(true, LED_G[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_R[1]);
                            setPin(true, LED_G[1]);
                        }
                        break;

                    case 7: // Luz color BLANCA (R+G+B)
                        if (side == 1 || side == 3) {
                            setPin(true, LED_R[0]);
                            setPin(true, LED_G[0]);
                            setPin(true, LED_B[0]);
                        }
                        if (side == 2 || side == 3) {
                            setPin(true, LED_R[1]);
                            setPin(true, LED_G[1]);
                            setPin(true, LED_B[1]);
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

 
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    positionFragment = 1;
                    mTextViewVersion = (TextView) rootView.findViewById(R.id.textview_version);
                    dismissKeyBoard();

                    seccionActual = 1;

                case 2:
                    resetPrefsUltCode();
                    positionFragment = 1;
                    rootView = inflater.inflate(R.layout.fragment_entrada, container, false);

                    tv_saldo = (TextView) rootView.findViewById(R.id.tv_saldo);
                    mTextViewVersion = (TextView) rootView.findViewById(R.id.textview_version);
                    b = (LinearLayout) rootView.findViewById(R.id.backgtound);
                    mCardViewInfo = (CardView) rootView.findViewById(R.id.card_view);
                    mLinearLayoutHead = (RelativeLayout) rootView.findViewById(R.id.linearlayout_head);
                    mTextViewHead = (TextView) rootView.findViewById(R.id.textview_head);
                    mTextViewMsg = (TextView) rootView.findViewById(R.id.textview_msg);
                    mTextViewText1 = (TextView) rootView.findViewById(R.id.textview_text1);
                    mTextViewText2 = (TextView) rootView.findViewById(R.id.textview_text2);


                    dismissKeyBoard();

                    seccionActual = 1;

                    controlReader(DISABLE_BUZZER);

                    if (mReader.isOpened()){
                        mReader.close(); }

                    pedirPermisosLectorNfc();

                    try {
                        setLeds(100, LUZ_APAGADA, LADO_AMBOS);
                        setLeds(100, LUZ_AZUL, LADO_SALIDA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    positionFragment = 2;
                    rootView = inflater.inflate(R.layout.fragment_consultar, container, false);

                    resetPrefsUltCode();

                    b = (LinearLayout) rootView.findViewById(R.id.backgtound);
                    tv_saldo = (TextView) rootView.findViewById(R.id.tv_saldo);
                    mTextViewVersion = (TextView) rootView.findViewById(R.id.textview_version);
                    mCardViewInfo = (CardView) rootView.findViewById(R.id.card_view);
                    mLinearLayoutHead = (RelativeLayout) rootView.findViewById(R.id.linearlayout_head);
                    mTextViewHead = (TextView) rootView.findViewById(R.id.textview_head);
                    mTextViewMsg = (TextView) rootView.findViewById(R.id.textview_msg);
                    mTextViewText1 = (TextView) rootView.findViewById(R.id.textview_text1);
                    mTextViewText2 = (TextView) rootView.findViewById(R.id.textview_text2);

                    dismissKeyBoard();

                    seccionActual = 2;

                    controlReader(DISABLE_BUZZER);

                    if (mReader.isOpened()){
                        mReader.close(); }

                    pedirPermisosLectorNfc();

                    try {
                        setLeds(100, LUZ_APAGADA, LADO_AMBOS);
                        setLeds(100, LUZ_AZUL, LADO_ENTRADA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 4:

                    rootView = inflater.inflate(R.layout.fragment_configuracion, container, false);

                    b = (LinearLayout) rootView.findViewById(R.id.backgtound);
                    tv_saldo = (TextView) rootView.findViewById(R.id.tv_saldo);
                    textViewSaveSettings = rootView.findViewById(R.id.textview_save_settings);
                    editTextUrlServer = rootView.findViewById(R.id.editext_url_servidor);
                    editTextApiKey = rootView.findViewById(R.id.editext_api_key);
                    setGuardarConf();

                    dismissKeyBoard();

                    break;

                case 5:
                    rootView = inflater.inflate(R.layout.fragment_entrada, container, false);

                    tv_saldo = (TextView) rootView.findViewById(R.id.tv_saldo);
                    mTextViewVersion = (TextView) rootView.findViewById(R.id.textview_version);
                    b = (LinearLayout) rootView.findViewById(R.id.backgtound);
                    mCardViewInfo = (CardView) rootView.findViewById(R.id.card_view);
                    mLinearLayoutHead = (RelativeLayout) rootView.findViewById(R.id.linearlayout_head);
                    mTextViewHead = (TextView) rootView.findViewById(R.id.textview_head);
                    mTextViewMsg = (TextView) rootView.findViewById(R.id.textview_msg);
                    mTextViewText1 = (TextView) rootView.findViewById(R.id.textview_text1);
                    mTextViewText2 = (TextView) rootView.findViewById(R.id.textview_text2);

                    dismissKeyBoard();

                    break;
                case 6:
                    break;
            }

            return rootView;
        }

        private void setGuardarConf() {
            if (!AppPreferences.getPrefsUrlBaseHackathon().isEmpty()) {
                editTextUrlServer.setText(AppPreferences.getPrefsUrlBaseHackathon());
            } else {
                editTextUrlServer.setText("http://");
                editTextUrlServer.setSelection(editTextUrlServer.getText().toString().length());
            }
            if (!AppPreferences.getPrefsApiKeyHackathon().isEmpty()) {
                editTextApiKey.setText(AppPreferences.getPrefsApiKeyHackathon());
            }

            textViewSaveSettings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editTextUrlServer.getText().toString().isEmpty() && !editTextApiKey.getText().toString().isEmpty()) {
                        AppPreferences.setPrefsUrlBaseHackathon(editTextUrlServer.getText().toString().trim());
                        AppPreferences.setPrefsApiKeyHackathon(editTextApiKey.getText().toString().trim());
                        dismissKeyBoard();
                        Snackbar.make(b, "Settings Saved", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(b, "All fields are required", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void dismissKeyBoard() {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        private void resetPrefsUltCode() {
            AppPreferences.setPrefsUltCodigo("");
            AppPreferences.setPrefsUltFecha(0);
            AppPreferences.setPrefsUltRespuesta("");
        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    public static Context getContext() {
        return activity;
    }

    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        protected Exception doInBackground(UsbDevice... params) {

            Exception result = null;

            try {
                mReader.open(params[0]);

            } catch (Exception e) {

                result = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {

            if (result != null) {

                System.out.println(result.toString());

            } else {

                System.out.println("Reader name: " + mReader.getReaderName());

                int numSlots = mReader.getNumSlots();
                System.out.println("Number of slots: " + numSlots);

                controlReader(ENABLE_BUZZER);
            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (mReader.isOpened()){
                mReader.close(); }
            return null;
        }
    }
}