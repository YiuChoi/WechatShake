package name.caiyao.wechatshake;

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
    private int count = 0;
    private boolean isShake = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
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
                    }
                }
            }.start();

            final Class<?> sensorEL = XposedHelpers.findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isShake) {
                        if (count > 50000) {
                            return;
                        }
                        count++;
                        ((float[]) param.args[1])[0] = new Random().nextFloat() * 1200f + 125f;
                    }
                }
            });
        }
    }
}
