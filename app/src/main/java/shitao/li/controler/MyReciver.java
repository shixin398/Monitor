package shitao.li.controler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @ProjectName: Controler
 * @Package: shitao.li.controler
 * @ClassName: MyReciver
 * @Description:
 * @Author: shitao.li
 * @CreateDate: 2020/7/22 10:31
 * @UpdateUser:
 * @UpdateDate: 2020/7/22 10:31
 * @UpdateRemark:
 * @Version: 1.0
 */
public class MyReciver extends BroadcastReceiver {
    private static final String TAG = "MyReciver";

    private static final String ACTIONTAG = "act=";
    private static final String CAST = "com.example.broadcast";
    private static final String CALLERTAG = "caller=";

    @Override
    public void onReceive(Context context, Intent intent) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                exec("dumpsys activity broadcasts");
            }
        });
        thread.start();
    }


    private static String exec(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
        } //adb shell
//        final BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //这里一定要注意错误流的读取，不然很容易阻塞，得不到你想要的结果，
        final BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        final Process finalProcess = process;
        new Thread(new Runnable() {
            public void run() {
                String line;
                boolean castFlag = false;
                String[] info;
                try {
                    while ((line = inputStream.readLine()) != null) {
                        if (line.contains(ACTIONTAG)) {
                            castFlag = line.contains(CAST);
                        }
                        if (castFlag && line.contains(CALLERTAG)) {
                            line = line.trim();
                            info = line.split(" ");
                            for (String i: info)
                                Log.d(TAG, "run: " + i);
                            Log.d(TAG, "packageName: " + info[0].substring(CALLERTAG.length()));
                            finalProcess.destroy();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
