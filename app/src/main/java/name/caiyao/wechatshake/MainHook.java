package name.caiyao.wechatshake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 蔡小木 on 2016/8/23 0023.
 */

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final String START = "name.caiyao.wechatshake.SHAKE";
    private XSharedPreferences sharedPreferences;
    private static boolean isShake = false;
    private static boolean isOpen = true;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            isOpen = sharedPreferences.getBoolean("open", true);
            new Thread() {
                @Override
                public void run() {
                    while (!interrupted()) {
                        if (isOpen) {
                            if (isShake) {
                                try {
                                    Thread.sleep(1000);
                                    isShake = false;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Thread.sleep(5000);
                                    isShake = true;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            isShake = false;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();

            final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
            final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            IntentFilter intentFilter = new IntentFilter();
            systemContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    XposedBridge.log("收到：" + intent.getAction() + ":" + intent.getBooleanExtra("open", true));
                    isOpen = intent.getBooleanExtra("open", true);
                }
            }, new IntentFilter(START));

            final Class<?> sensorEL = XposedHelpers.findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isShake) {
                        ((float[]) param.args[1])[0] = new Random().nextFloat() * 1200f + 125f;
                    }
                }
            });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID);
    }
}
