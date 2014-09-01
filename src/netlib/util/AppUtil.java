package netlib.util;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class AppUtil {

	// 获取当前应用应用名
	public static String getAppName(Context context) {
		return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
	}

	// 获取当前应用版本名
	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	// 获取当前应用版本号
	public static int getVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获取当前应用包名
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	// 获取当前应用图标
	public static Drawable getAppIcon(Context context) {
		return context.getApplicationInfo().loadIcon(context.getPackageManager());
	}

	// 通过进程名获取进程的进程id
	public static int getPid(Context context, String processName) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
			if (processName.equals(appProcess.processName)) {
				return appProcess.pid;
			}
		}
		return 0;
	}

	// 安装apk
	public static void installApk(Context context, String apkPath) {
		File file = new File(apkPath);
//		Log.e("", "apk size=" + LibIOUtil.getFileSize(file));
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	// 启动apk
	public static void launchApk(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}

	// 通过文件名获取包名
	public static String getPackageName(Context context, String path) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			return appInfo.packageName;
		} else {
			return null;
		}
	}

	/**
	 * 判断是否已经启动这个程序
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isStartProgram(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
		for (RunningTaskInfo runningTaskInfo : list) {
			if (context.getPackageName().equals(runningTaskInfo.baseActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否已经启动这个程序 且在活动在第一个界面
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTopStartProgram(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
		if (list != null && list.size() > 0) {
			RunningTaskInfo runningTaskInfo = list.get(0);
			if (context.getPackageName().equals(runningTaskInfo.baseActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断此activity 是否已经启动且在堆栈顶部
	 * 
	 * @param context
	 * @param activityName
	 * @return
	 */
	public static boolean isActivityStarted(Context context, String activityName) {
//		Log.d("tag", " activityName = " + activityName);
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
		for (RunningTaskInfo runningTaskInfo : list) {
			Log.i("tag", runningTaskInfo.topActivity.getPackageName() + " runningTaskInfo.baseActivity="
					+ runningTaskInfo.baseActivity);
			if (context.getPackageName().equals(runningTaskInfo.baseActivity.getPackageName())
					&& activityName.equals(runningTaskInfo.baseActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断一个程序是否显示在前端,根据测试此方法执行效率在11毫秒,无需担心此方法的执行效率
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isApplicationShowing(Context context, String packageName) {
		boolean result = false;
//		public static final int IMPORTANCE_BACKGROUND = 400//后台
//		public static final int IMPORTANCE_EMPTY = 500//空进程
//		public static final int IMPORTANCE_FOREGROUND = 100//在屏幕最前端、可获取到焦点 可理解为Activity生命周期的OnResume();
//		public static final int IMPORTANCE_SERVICE = 300//在服务中
//		public static final int IMPORTANCE_VISIBLE = 200//在屏幕前端、获取不到焦点可理解为Activity生命周期的OnStart();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses != null) {
			for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
				if (runningAppProcessInfo.processName.equals(packageName)) {
					int status = runningAppProcessInfo.importance;
					if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
							|| status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	// 判断本程序 最顶部activity 是否是 此activity
	public static boolean isActivityTopStartThisProgram(Context context, String activityName) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
		if (list != null && list.size() > 0) {
			for (RunningTaskInfo runningTaskInfo : list) {
//				Log.e("tag",
//						"runningTaskInfo.topActivity.getClassName() = " + runningTaskInfo.topActivity.getClassName());
//				Log.e("tag", "activityName = " + activityName);
				if (context.getPackageName().equals(runningTaskInfo.baseActivity.getPackageName())) {
					if (runningTaskInfo.topActivity.getClassName().equals(activityName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
