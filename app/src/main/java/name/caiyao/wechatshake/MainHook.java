package name.caiyao.wechatshake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 蔡小木 on 2016/8/23 0023.
 */

public class MainHook implements IXposedHookLoadPackage {
    private int count = 1;
    private static boolean isShake = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
            Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            systemContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    isShake = true;
                    count = 1;
                }
            }, new IntentFilter("name.caiyao.START"));
            final Class<?> sensorEL = XposedHelpers.findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isShake) {
                        count++;
                        ((float[]) param.args[1])[0] = new Random().nextFloat() * 1200f + 125f;
                        if (count == 10) {
                            isShake = false;
                        }
                    }
                }
            });
        }
    }
}
