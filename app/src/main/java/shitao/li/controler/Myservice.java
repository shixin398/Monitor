package shitao.li.controler;

import android.app.IActivityController;
import android.app.IActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ProjectName: Controler
 * @Package: shitao.li.controler
 * @ClassName: Myservice
 * @Description:
 * @Author: shitao.li
 * @CreateDate: 2020/7/20 17:23
 * @UpdateUser:
 * @UpdateDate: 2020/7/20 17:23
 * @UpdateRemark:
 * @Version: 1.0
 */
public class Myservice extends Service {
    private static final String TAG = "Myservice";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setActivityController();
    }

    private void setActivityController() {
        Log.d(TAG, "setActivityController: ...........");
        try {
            Class<?> cActivityManagerNative = Class
                    .forName("android.app.ActivityManagerNative");
            Method mGetDefault = cActivityManagerNative.getMethod("getDefault");
            Object oActivityManagerNative = mGetDefault.invoke(ActivityManagerNative.getDefault());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                Method mSetActivityController = cActivityManagerNative.getMethod(
                        "setActivityController",
                        Class.forName("android.app.IActivityController"), Boolean.TYPE);
                mSetActivityController.invoke(oActivityManagerNative,
                        new ActivityController(), true);
            } else {
                Method mSetActivityController = cActivityManagerNative.getMethod(
                        "setActivityController",
                        Class.forName("android.app.IActivityController"));
                mSetActivityController.invoke(oActivityManagerNative,
                        new ActivityController());
            }

            Log.d(TAG, "setActivityController: ....................end");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果启动的应用在黑名单，则禁止启动该应用
     */
    public class ActivityController extends IActivityController.Stub {
        public boolean activityStarting(Intent intent, String pkg) {
            Log.d(TAG, "activityStarting: " + pkg + ", intent= " + intent);
            //retrun false;//false 则不会启动，直接返回。
            return true;
        }

        public boolean activityResuming(String pkg) {
            Log.d(TAG, "activityResuming: " + pkg);
            return true;
        }

        public int appEarlyNotResponding(String processName, int pid,
                                         String annotation) {
            return 0;
        }

        public boolean appCrashed(String processName, int pid, String shortMsg,
                                  String longMsg, long timeMillis, String stackTrace) {
            return false;
        }

        public int appNotResponding(String processName, int pid,
                                    String processStats) {
            return 0;
        }

        public int systemNotResponding(String msg) {
            return 1;
        }
    }
}
