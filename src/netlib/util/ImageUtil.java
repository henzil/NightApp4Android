package netlib.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class ImageUtil {

	public static final int DEFAULT_MAX_WIDTH = 320;
	public static final int DEFAULT_MAX_HEIGHT = 480;

//
//	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
//		if (bitmap == null)
//			return null;
//		if (bitmap.isRecycled())
//			return null;
//		Matrix matrix = new Matrix();
//		matrix.postRotate(degrees);
//
//		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//		if (resizedBitmap != bitmap) {
//			bitmap.recycle();
//			bitmap = resizedBitmap;
//		}
//		return bitmap;
//	}

	public static Bitmap getBitmapFromMedia(Context context, String pathName) {
		return getBitmapFromMedia(context, pathName, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
	}

//
	public static Bitmap getBitmapFromMedia(Context context, String pathName, int maxWidth, int maxHeight) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
			// inJustDecodeBounds为true 不分配内存，返回一个空bitmap
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, options);
			options.inJustDecodeBounds = false;
			// 图片的宽度和高度
			int outputWidth = options.outWidth;
			int outputHeight = options.outHeight;
//			Log.e("ImageUtil", "&&&&&&&&pathName = " + pathName + " outputHeight = " + outputHeight);
			if (maxWidth <= 0) {
				maxWidth = DEFAULT_MAX_WIDTH;
			}
			if (maxHeight <= 0) {
				maxHeight = DEFAULT_MAX_HEIGHT;
			}

			if (outputWidth < maxWidth && outputHeight < maxHeight) {
				bitmap = BitmapFactory.decodeFile(pathName);
			} else {
				int inSampleSize = 0;
				int widthSmapleSize = (int) (outputWidth / maxWidth);
				int heightSmapleSize = (int) (outputHeight / maxHeight);
				if (widthSmapleSize >= heightSmapleSize) {
					inSampleSize = widthSmapleSize;
				} else {
					inSampleSize = heightSmapleSize;
				}
				options.inSampleSize = inSampleSize;
				bitmap = BitmapFactory.decodeFile(pathName, options);
			}

		} catch (OutOfMemoryError oom) {
			Log.e("ImageUtil", oom.getMessage(), oom);
			System.gc();
			return null;
		} catch (Exception e) {
			Log.e("ImageUtil", e.getMessage(), e);
			return null;
		}
		return bitmap;
	}

//	public static Bitmap drawableToBitmap(Drawable drawable) {
//		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
//				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
//		Canvas canvas = new Canvas(bitmap);
//		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//		drawable.draw(canvas);
//		return bitmap;
//	}

	public static int getBitmapByteCount(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return 0;
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

//	public static String getPathName(String url) {
//		return url.substring(url.lastIndexOf("/") + 1);
//	}
//
//	public static String getExtentionName(String url) {
//		if ((url != null) && (url.length() > 0)) {
//			int dot = url.lastIndexOf('.');
//			if ((dot > -1) && (dot < (url.length() - 1))) {
//				return url.substring(dot + 1);
//			}
//		}
//		return null;
//	}
//
//	public static Bitmap.CompressFormat getImageType(String imageType) {
//		if (imageType.equalsIgnoreCase("png")) {
//			return Bitmap.CompressFormat.PNG;
//		} else if (imageType.equalsIgnoreCase("jpg")) {
//			return Bitmap.CompressFormat.JPEG;
//		} else {
//			return Bitmap.CompressFormat.PNG;
//		}
//	}

	public static boolean compressBitmap(Bitmap bitmap, Bitmap.CompressFormat compressFormat, String compressPath) {
		if (bitmap == null)
			return false;
		OutputStream fileOutputStream = null;
		File file = new File(compressPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			fileOutputStream = new FileOutputStream(file);

			bitmap.compress(compressFormat, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

//按照片质量压缩，压缩至小于100k
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		Log.v("tag", "baos.toByteArray().length / 1024 > 500=" + (baos.toByteArray().length / 1024 > 500));
		while (baos.toByteArray().length > 500 * 1024) {
			Log.v("tag", options + "");// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			if (options <= 0) {
				options = 1;
				image.compress(Bitmap.CompressFormat.PNG, options, baos);
				break;
			} else {
				image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 5;// 每次都减少10
			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	@SuppressLint("NewApi")
	public static int compressImageTake(int count) {
		int i = 1;
//		if (count > 500*1024) {
//			i = count / (500 * 1024);
//		}
//		if (i > 1) {
//			return i+1;
//		} else {
//			return 2;
//		}
		i = count / (500 * 1024);
		if (i % 2 == 0) {
			return i + 2;
		} else {
			return i + 3;
		}

	}

	// 将Bitmap存入SD卡
	public static void saveBitmapToSD(Bitmap mBitmap, String imagePath) {
		File f = new File(imagePath);
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 120;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap zoomImage(Bitmap bgimage) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) DEFAULT_MAX_HEIGHT) / width;
		float scaleHeight = ((float) DEFAULT_MAX_WIDTH) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

}
