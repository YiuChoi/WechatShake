package name.caiyao.wechatshake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by 蔡小木 on 2016/8/23 0023.
 */

public class MainHook implements IXposedHookLoadPackage {
    private int count = 1;
    private static boolean isShake = false;
    private String[] packages = {
            "com.tencen01.mm",
            "com.tencen02.mm",
            "com.tencen03.mm",
            "com.tencen04.mm",
            "com.tencen05.mm",
            "com.tencen06.mm",
            "com.tencen07.mm",
            "com.tencen08.mm",
            "com.tencen09.mm",
            "com.tencen10.mm"
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(packages[0]) ||
                loadPackageParam.packageName.equals(packages[1]) ||
                loadPackageParam.packageName.equals(packages[2]) ||
                loadPackageParam.packageName.equals(packages[3]) ||
                loadPackageParam.packageName.equals(packages[4]) ||
                loadPackageParam.packageName.equals(packages[5]) ||
                loadPackageParam.packageName.equals(packages[6]) ||
                loadPackageParam.packageName.equals(packages[7]) ||
                loadPackageParam.packageName.equals(packages[8]) ||
                loadPackageParam.packageName.equals(packages[9]) || loadPackageParam.packageName.equals("com.tencent.mm")) {
            final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
            final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            systemContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    isShake = true;
                    count = 1;
                }
            }, new IntentFilter("name.caiyao.START"));
            final Class<?> sensorEL = findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isShake) {
                        count++;
                        ((float[]) param.args[1])[0] = new Random().nextFloat() * 1200f + 125f;
                        if (count == 250) {
                            isShake = false;
                        }
                    }
                }
            });
            // com.tencent.mm/.plugin.shake.ui.ShakeReportUI
            findAndHookMethod(loadPackageParam.packageName + ".plugin.shake.ui.ShakeReportUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    systemContext.sendBroadcast(new Intent("name.caiyao.START"));
                }
            });
        }
    }
}
