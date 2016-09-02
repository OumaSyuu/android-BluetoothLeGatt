package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

/**
 * Created by Administrator on 2016/8/31.
 */

public class GeneralReceiver extends BroadcastReceiver {

    private static final String TAG = GeneralReceiver.class.getSimpleName();

    private IntentFilter mFilter;

    public GeneralReceiver() {

        mFilter = new IntentFilter();
        mFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

    }

    public void register(Context context) {
        Log.d(TAG, "register");
        context.registerReceiver(this, mFilter);
    }

    public void unregister(Context context) {
        Log.d(TAG, "unregister");
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            switch (state) {
                case BluetoothAdapter.STATE_ON: {
                    Log.v(TAG, "Bluetooth STATE_ON");
                    break;
                }
                case BluetoothAdapter.STATE_OFF: {
                    Log.v(TAG, "Bluetooth STATE_OFF");
                    break;
                }
                case BluetoothAdapter.STATE_TURNING_ON: {
                    Log.v(TAG, "Bluetooth STATE_TURNING_ON");
                    break;
                }
                case BluetoothAdapter.STATE_TURNING_OFF: {
                    Log.v(TAG, "Bluetooth STATE_TURNING_OFF");
                    break;
                }
            }
        } else if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(action)) {//pairing request from the remote device

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.v(TAG, "device " + device.getAddress());

            switch (device.getBondState()) {

                case BluetoothDevice.BOND_BONDED:
                    Log.v(TAG, "PAIRING_REQUEST BluetoothDevice BOND_BONDED");
                    return;
                case BluetoothDevice.BOND_BONDING:
                    Log.v(TAG, "PAIRING_REQUEST BluetoothDevice BOND_BONDING");
                    break;
                case BluetoothDevice.BOND_NONE:
                    Log.v(TAG, "PAIRING_REQUEST BluetoothDevice BOND_NONE");
                    return;
                default:
                    Log.v(TAG, "PAIRING_REQUEST BluetoothDevice BOND_KNOWN");
                    break;

            }
            //cancel system pop and confirm pairing
            abortBroadcast();
            Boolean setPinFlag = false;
            Boolean createBondFlag = false;
            Boolean cancelPairingUserInputFlag = false;
            Boolean setPairingConfirmationFlag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                setPinFlag = device.setPin(new byte[]{0});
                setPairingConfirmationFlag = device.setPairingConfirmation(true);
//                createBondFlag = device.createBond();
            } else {

//                setPinFlag = (Boolean) ReflectUtil.invoke(device, "setPin", new byte[]{0});
                setPairingConfirmationFlag = (Boolean) ReflectUtil.invoke(device, "setPairingConfirmation", true);
//                createBondFlag = (Boolean) ReflectUtil.invoke(device, "createBond");

            }
            //for canceled system pop, it is not necessary to invoke the hidden method cancelPairingUserInput() from BluetoothDevice
//            cancelPairingUserInputFlag = (Boolean) ReflectUtil.invoke(device, "cancelPairingUserInput");
            Log.v(TAG, "setPinFlag = " + setPinFlag +
                    "; createBondFlag = " + createBondFlag +
                    "; cancelPairingUserInputFlag = " + cancelPairingUserInputFlag +
                    "; setPairingConfirmationFlag = " + setPairingConfirmationFlag);

        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equalsIgnoreCase(action)) {

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.v(TAG, "device " + device.getAddress());
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            int preBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE);
            //state change while pairing:BOND_NONE->BOND_BONDING
            //state change while paired:BOND_BONDING->BOND_BONDED
            //state change while unpairing:BOND_BONDED->BOND_BONDING
            //state change while unpaired:BOND_BONDING->BOND_NONE
            //consider status 133 error, it is better to execute gatt operations after paired or unpaired
            if (preBondState == BluetoothDevice.BOND_NONE && bondState == BluetoothDevice.BOND_BONDING) {

                Log.v(TAG, "STATE_CHANGED process : pairing");

            } else if (preBondState == BluetoothDevice.BOND_BONDING && bondState == BluetoothDevice.BOND_BONDED) {

                Log.v(TAG, "STATE_CHANGED process : paired");
                //TODO gatt operations after paired

            } else if (preBondState == BluetoothDevice.BOND_BONDED && bondState == BluetoothDevice.BOND_BONDING) {

                Log.v(TAG, "STATE_CHANGED process : unpairing");

            } else if (preBondState == BluetoothDevice.BOND_BONDING && bondState == BluetoothDevice.BOND_NONE) {

                Log.v(TAG, "STATE_CHANGED process : unpaired");
                //TODO gatt operations after unpaired

            }

        }
    }
}
