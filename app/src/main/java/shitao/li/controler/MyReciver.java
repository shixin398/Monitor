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

    private static final String CAST = "com.example.broadcast";
    private static final String SEARCHTAG = "caller=";

    private int countDown = 10;
    Handler handler = new Handler();

    static boolean stopFlag = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "---onReceive: " + intent.getComponent() == null ? "null" : intent.getComponent().getPackageName());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String out = exec("dumpsys activity broadcasts");
                Log.d(TAG, "run: " + out);

            }
        });
        thread.start();
//        countDown();

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
        new Thread(new Runnable() {
            String line;
            public void run() {
                System.out.println("listener started");
                boolean oldCastFlag = false;
                boolean castFlag = false;
                boolean callerFlag = false;
                String[] info;
                try {
                    while ((line = inputStream.readLine()) != null) {
                        castFlag = line.contains(CAST);
                        callerFlag = line.contains(SEARCHTAG);
                        if (oldCastFlag && callerFlag){
                            line = line.trim();
                            info = line.split(" ");
                            for (String i: info)
                                Log.d(TAG, "run: " + i);
                            Log.d(TAG, "run: caller package:" + info[0].substring(7));
                            System.out.println(line);
                            break;
                        }
                        oldCastFlag = castFlag;
//                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        new Thread(new Runnable() {
//            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            public void run() {
//                System.out.println("writer started");
//                String line;
//                try {
//                    while ((line = br.readLine()) != null) {
//                        outputStream.write(line + "\r\n");
//                        outputStream.flush();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        int i = 0;
        try {
            i = process.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("i=" + i);
        return null;
    }


    public void countDown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countDown >= 0) {
                    Log.d("lstlog", " time: " + countDown);
                    handler.postDelayed(this, 1000);
                    countDown--;
                } else {
                    stopFlag = true;
                    Log.d("lstlog", "finish...");
//                    finish();
                }

            }
        }, 1000);
    }

}
