package adgarcis.com.adgarcisacceso;


import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;

import java.util.Arrays;

import static adgarcis.com.adgarcisacceso.Utils.byteArrayToHexString;

public class PulseraManager {

    private static final int OK_LECTURA = -2;
    private static final int OK_ESCRITURA = -1;
    private static final int ERROR_LECTURA = 1;
    private static final int ERROR_ESCRITURA = 2;

    private Reader mReader;
    private int slotNum;

    private String idPulsera;
    private String idBloquePulsera;
    private int resultadoEscritura;

    private OnReadOrWriteDataListener onReadOrWriteDataListener;
    public interface OnReadOrWriteDataListener {
        void onReadSuccess();
        void onReadInvalidData();
        void onWritingProcessEnd();
    }

    public PulseraManager(Reader mReader, int slotNum, OnReadOrWriteDataListener onReadOrWriteDataListener) {
        this.mReader = mReader;
        this.slotNum = slotNum;
        this.onReadOrWriteDataListener = onReadOrWriteDataListener;
        this.resultadoEscritura = OK_ESCRITURA;
    }

    private boolean autenticacionCorrecta() {
        boolean auth = false;

        if (cargarClaveLector(MifareClassic.KEY_DEFAULT)) {
            if (autenticar()) {
                imprimirEnConsola("Autenticacion correcta");
                auth = true;
            }
        } else {
            imprimirEnConsolaError("fallo al guardar a clave en el lector");
        }

        return auth;
    }

    private boolean autenticar() {
        byte[] command = { (byte)0xFF, (byte)0x86, 0x00, 0x00, 0x05, 0x01, 0x00, 0x0A, 0x60, 0x00};
        byte[] response = new byte[2];
        int responseLength;

        try {
            responseLength = mReader.transmit(slotNum, command, command.length, response, response.length);

            if (Arrays.equals(response, new byte[]{(byte)0x90, 0x00})) {
                return true;
            }
        } catch (ReaderException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean cargarClaveLector(byte [] claveAccesos) {
        try {
            byte[] commandGuardarClaveLector = { (byte)0xFF, (byte)0x82, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            System.arraycopy(claveAccesos, 0, commandGuardarClaveLector, 5, claveAccesos.length);

            byte[] responseGuardarClaveLector = new byte[2];
            int responseLengthGuardarClaveLector;

            responseLengthGuardarClaveLector = mReader.transmit(slotNum, commandGuardarClaveLector, commandGuardarClaveLector.length, responseGuardarClaveLector, responseGuardarClaveLector.length);

            if (Arrays.equals(responseGuardarClaveLector, new byte[]{(byte)0x90, 0x00})) {
                return true;
            }

        } catch (ReaderException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean leerDatos() {
        return leerBloqueUsuario();
    }

    private boolean leerBloqueUsuario() {
        boolean leerMenorDeEdad = false;
        try {
            byte[] commandLeer = { (byte)0xFF, (byte)0xB0, 0x00, 0x0A, 0x10};
            byte[] dataEdad = new byte[18];
            int responseLengthLeer;
            responseLengthLeer = mReader.transmit(slotNum, commandLeer, commandLeer.length, dataEdad, dataEdad.length);

            if (dataEdad[16] == (byte)0x90) {
                String dataEdadHex =  byteArrayToHexString(dataEdad).substring(0, 32);
                idBloquePulsera = String.valueOf(Integer.parseInt(dataEdadHex.substring(0, 1)));


                leerMenorDeEdad = true;
                imprimirEnConsola("Id bloque de pulsera =>  " + idBloquePulsera);
            } else {
                imprimirEnConsolaError("No se puede acceder a la pulsera");
            }
        } catch (ReaderException e) {
            e.printStackTrace();
        }
        return leerMenorDeEdad;
    }

    private void imprimirEnConsola(String mensaje) {
        System.out.println("PULSERA --> " + mensaje);
    }

    private void imprimirEnConsolaError(String mensaje) {
        System.out.println("PULSERA -- ERROR --> " + mensaje);
    }

    private void invocarListenerSiEsNecesario(int resultadoEscritura) {
        this.resultadoEscritura = resultadoEscritura;
        switch (resultadoEscritura) {
            case ERROR_LECTURA:
                imprimirEnConsolaError("Error al leer");
                break;
            case OK_LECTURA:
                onReadOrWriteDataListener.onReadSuccess();
                break;
            case ERROR_ESCRITURA:
            case OK_ESCRITURA:
                onReadOrWriteDataListener.onWritingProcessEnd();
                break;
        }
    }

    public void conectarYleerDatos() {
        if (conectarTagLector()) {
            idPulsera = getIdLector();
            if (!idPulsera.isEmpty()) {
                if (autenticacionCorrecta()) {
                    if (leerDatos()) {
                        invocarListenerSiEsNecesario(OK_LECTURA);
                    } else {
                        invocarListenerSiEsNecesario(ERROR_LECTURA);
                    }
                } else {
                    MainActivity.setBackgroundCardView("Scan the wristband again", "", "", Boolean.TRUE, "", "");
                }

            }
        }  else {
            imprimirEnConsolaError("Error al conectar");
        }
    }

    private boolean conectarTagLector() {
        try {
            byte[] atr = mReader.power(slotNum, Reader.CARD_WARM_RESET);
            mReader.setProtocol(slotNum, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);

                byte[] command = new byte[]{ (byte)0xFF, (byte)0x00, (byte)0x51, (byte)0x85, (byte)0x00};
                byte[] response = new byte[2];
                int responseLength;

                responseLength=mReader.transmit(slotNum,command,     command.length, response,response.length);

                if (response[0] == (byte)0x90) {
                    return true;
                }


        } catch (ReaderException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getIdLector() {
        String id = "";

        byte[] command = new byte[]{ (byte)0xFF, (byte)0xCA, (byte)0x0, (byte)0x0, (byte)0x0};
        byte[] response = new byte[6];
        int responseLength;

        try {
            responseLength=mReader.transmit(slotNum,command,     command.length, response,response.length);

            if (response[4] == (byte)0x90) {
                id =  byteArrayToHexString(response).substring(0, 8);
            }

        } catch (ReaderException e) {
            e.printStackTrace();
        }

        return id;
    }

    public void conectarYleerDatosSinClave() {
        idPulsera = getIdLector();
        if (!idPulsera.isEmpty()) {
            if (conectarTagLector()) {
                invocarListenerSiEsNecesario(OK_LECTURA);
            } else {
                imprimirEnConsolaError("Error al conectar");
            }
        }
    }

    public String getIdPulsera() {
        return idPulsera;
    }


    public String getIdBloquePulsera() {
        return idBloquePulsera;
    }

}
