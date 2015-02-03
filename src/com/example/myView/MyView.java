package com.example.myView;

import java.util.List;

import com.example.baidudesktop.R;
import com.example.entity.AppInfo;
import com.example.provider.PackageInfoProvider;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MyView extends View implements OnTouchListener {

	private Paint paintForLine = new Paint();// �����
	private Paint paintForCircle = new Paint();// Բ
	private Paint paintForText = new Paint();// ����
	private Paint paintForShan;//����
	private Paint paintForBitmap ;//ͼ��
	private Bitmap[] icons = new Bitmap[18];// ͼ���б�
	private Point[] AllPoints;// ���е��б�
	private Point[] RecentPoints; // ���ʹ�õ�Ӧ��
	private Point[] RecentInstalledPoints; // �����װ��Ӧ��
	private Point[] OftenUsedPoints; // ����ʹ�õ�Ӧ��
	private static final int PONIT_NUM = 18;// �����Ŀ
	private int mPointX = 0, mPointY = 0;// Բ������
	private int mRadius = 0;// �뾶
	private int mDegreeDelta;// ÿ����������ĽǶ�
	private int tempDegree = 0;// ÿ��ת���ĽǶȲ�
	private int chooseBtn = 999;// ѡ�е�ͼ���ʶ 999��δѡ���κ�ͼ��
	private Matrix mMatrix = new Matrix();
	private int startAngle = 210;//��ʼ�Ƕ�
	private int degree;// ƫת�Ƕ�
	private int selectFlag = 0;//����ѡ������־
	private PackageInfoProvider pakageInfoProvider = null;
	private static List<AppInfo> recentAppInfos = null;
	private static List<AppInfo> recentInstallAppInfos = null;
	private static List<AppInfo> oftenUseAppInfos = null;
	private Bitmap bitmapBg;//ÿ��Ӧ�õı���ͼƬ
	private static int mAlpha = 255;
	// ��������
	private OnTurnplateListener onTurnplateListener;

	public void setOnTurnplateListener(OnTurnplateListener onTurnplateListener) {
		this.onTurnplateListener = onTurnplateListener;
	}

	public MyView(Context context, int px, int py, int radius) {
		super(context);
		// ��ȡ��������Ϣ
		pakageInfoProvider = new PackageInfoProvider(context);
		//��ȡ���Ӧ��
		recentAppInfos = pakageInfoProvider.getRecentTasks();
		//��ȡ�����װӦ��
		recentInstallAppInfos = pakageInfoProvider.getRecentlyinstalledTasks();
		bitmapBg = convertToBitmapFromDrawable(getResources().getDrawable(
				R.drawable.bg_green));
		initPaints();// ��ʼ������
		mPointX = px;
		mPointY = py;
		mRadius = radius;
		initPoints();
		computeCoordinates();// ����ÿ���������
	}

	/**
	 * ��ʼ������
	 */
	private void initPaints() {
		paintForLine = new Paint();// �����
		paintForCircle = new Paint();// Բ
		paintForText = new Paint();// ����
		paintForShan = new Paint();//����
		paintForBitmap = new Paint();//ͼ��
		
		paintForLine.setColor(Color.RED);
		paintForLine.setStrokeWidth(2);
		paintForCircle.setAntiAlias(true);
		paintForCircle.setColor(Color.WHITE);

		paintForText.setColor(Color.WHITE);
		paintForText.setTextSize(15.0f);
		paintForText.setTextAlign(Align.CENTER);
		paintForText.setAlpha(mAlpha);
		
		paintForShan.setColor(Color.BLACK);
		paintForShan.setAlpha(35);
		paintForBitmap.setAlpha(mAlpha);
	}

	/**
	 * ��drawableת��Ϊbitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap convertToBitmapFromDrawable(Drawable drawable) {
		//�޸Ĵ˴����Ը���ͼ��Ĵ�С
		Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, 80, 80);
		drawable.draw(canvas);
		
		return bitmap;
	}

	/**
	 * ��ʼ�����еĵ�
	 */
	private void initPoints() {
		AllPoints = new Point[PONIT_NUM];
		initRecentPoints();
		initRecentInstalledPoints();
		initOftenUsedPoints();//���Ը����Լ�������ģ��˴������ʹ�õ�Ӧ�ô���
		for (int i = 0; i < 6; i++) {
			AllPoints[i] = RecentPoints[i];
			AllPoints[i + 6] = RecentInstalledPoints[i];
			AllPoints[i + 12] = OftenUsedPoints[i];
		}
	}

	/**
	 *���ܣ���ʼ�����ʹ�õĵ� ���� ��ĽǶȣ�ƫ�ƽǶȣ�λͼ����־
	 */

	private void initRecentPoints() {
		RecentPoints = new Point[6];
		Point point;
		int angle = 270;// ��ʼ�Ƕ�
		int sizeOfPoint =0;
		//�Ӷ�ջ������ȡ������ǰ����Ϊ��׿ϵͳ���͵�ǰӦ�ã����Դ˴��ı�ž���2����Ϊ8
		if (PackageInfoProvider.sizeOfAppLists>=8) {
			mDegreeDelta = 360 / 6;
			sizeOfPoint = 6;
			// ��ֹ����Խ������
			for (int index = 0; index < sizeOfPoint; index++) {
				point = new Point();
				point.angle = angle;
				angle += mDegreeDelta;
				if (angle > 360 || angle < -360) {
					angle = angle % 360;
				}// ��֤angle��0~360��Χ��
				point.bitmap = convertToBitmapFromDrawable(recentAppInfos.get(
						index + 2).getDrawable());
				point.flag = index;
				point.appName = recentAppInfos.get(index +2).getAppName();
				point.packageName = recentAppInfos.get(index + 2).getPackageName();
				point.startDegree = angle;
				RecentPoints[index] = point;
			}
		}else {
			sizeOfPoint = pakageInfoProvider.sizeOfAppLists;
			// ��ֹ����Խ������
			for (int index = 0; index < sizeOfPoint; index++) {
				point = new Point();
				point.angle = angle;
				angle += mDegreeDelta;
				if (angle > 360 || angle < -360) {
					angle = angle % 360;
				}// ��֤angle��0~360��Χ��
				point.bitmap = convertToBitmapFromDrawable(recentAppInfos.get(
						index).getDrawable());
				point.flag = index;
				point.appName = recentAppInfos.get(index ).getAppName();
				point.packageName = recentAppInfos.get(index).getPackageName();
				point.startDegree = angle;
				RecentPoints[index] = point;
			}
		}
	}

	/**
	 * 
	 * 
	 */

	private void initRecentInstalledPoints() {
		RecentInstalledPoints = new Point[6];
		Point point;
		int angle = 270;// ��ʼ�Ƕ�
		mDegreeDelta = 360 / 6;

		for (int index = 0; index < 6; index++) {
			point = new Point();
			point.angle = angle;
			angle += mDegreeDelta;
			if (angle > 360 || angle < -360) {
				angle = angle % 360;
			}// ��֤angle��0~360��Χ��
			point.bitmap = convertToBitmapFromDrawable(recentInstallAppInfos
					.get(index).getDrawable());
			point.flag = index + 6;
			point.appName = recentInstallAppInfos.get(index).getAppName();
			point.packageName = recentInstallAppInfos.get(index)
					.getPackageName();
			point.startDegree = angle;
			RecentInstalledPoints[index] = point;
		}
	}

	/**
	 * 
	 * ��������initPoints ���ܣ���ʼ��ÿ���� ���� ��ĽǶȣ�ƫ�ƽǶȣ�λͼ����־
	 */

	private void initOftenUsedPoints() {
		OftenUsedPoints = new Point[6];
		Point point;
		int angle = 270;// ��ʼ�Ƕ�
		mDegreeDelta = 360 / 6;

		for (int index = 0; index < 6; index++) {
			point = new Point();
			point.angle = angle;
			angle += mDegreeDelta;
			if (angle > 360 || angle < -360) {
				angle = angle % 360;
			}// ��֤angle��0~360��Χ��
			point.bitmap = convertToBitmapFromDrawable(recentAppInfos.get(
					index+2).getDrawable());
			point.flag = index + 12;
			point.appName = recentAppInfos.get(index+2).getAppName();
			point.packageName = recentAppInfos.get(index+2).getPackageName();
			point.startDegree = angle;
			OftenUsedPoints[index] = point;
		}
	}

	/**
	 * 
	 * ��������resetPointAngle ���ܣ����¼���ÿ����ĽǶ� ������
	 * 
	 */
	private void resetPointAngle(float x, float y) {
		// ÿ��ת���ĽǶ�
		degree = computeMigrationAngle(x, y);
		for (int index = 0 + selectFlag; index < 6 + selectFlag; index++) {
			AllPoints[index].angle += degree;
			if (AllPoints[index].angle > 360) {
				AllPoints[index].angle -= 360;
			} else if (AllPoints[index].angle < 0) {
				AllPoints[index].angle += 360;
			}
		}
	}

	/**
	 * 
	 * ��������computeCoordinates ���ܣ�����ÿ��������� ������ �������,���Բ��֮�����ĵ������
	 */
	private void computeCoordinates() {
		Point point;
		for (int index = 0 + selectFlag; index < 6 + selectFlag; index++) {
			point = AllPoints[index];
			point.x = mPointX
					+ (float) (mRadius * Math.cos(point.angle * Math.PI / 180));
			point.y = mPointY
					+ (float) (mRadius * Math.sin(point.angle * Math.PI / 180));
			point.x_c = mPointX + (point.x - mPointX) / 2;
			point.y_c = mPointY + (point.y - mPointY) / 2;
		}
	}

	/**
	 * 
	 * ��������computeMigrationAngle ���ܣ�����ƫ�ƽǶ� ������ ÿ��ת���ĽǶȲ�
	 */
	private int computeMigrationAngle(float x, float y) {
		int a = 0;
		float distance = (float) Math
				.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
						* (y - mPointY)));
		int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
		if (y < mPointY) {
			degree = -degree;
		}
		if (tempDegree != 0) {
			a = degree - tempDegree;
		}
		tempDegree = degree;
		return a;
	}

	/**
	 * 
	 * ��������computeCurrentDistance ���ܣ����㴥����λ�������Ԫ��ľ��� ������ �Ƿ�ѡ�񣬱�־
	 */
	private void computeCurrentDistance(float x, float y) {
		for (Point point : AllPoints) {
			float distance = (float) Math
					.sqrt(((x - point.x) * (x - point.x) + (y - point.y)
							* (y - point.y)));
			if (distance < 31) {
				chooseBtn = 999;
				point.isCheck = true;
				break;
			} else {
				point.isCheck = false;
				chooseBtn = point.flag;
			}
		}
	}

	private void switchScreen(MotionEvent event) {
		computeCurrentDistance(event.getX(), event.getY());
		for (Point point : AllPoints) {
			if (point.isCheck) {
				onTurnplateListener.onPointTouch(point);
				break;
			}
		}
	}

	/**
	 * ���ȴ����¼�
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN :
			
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			resetPointAngle(event.getX(), event.getY());
			MoveDegreeShan();
			computeCoordinates();
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			switchScreen(event);
			upDegreeShan();
			tempDegree = 0;
			initPoints();
			computeCoordinates();
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		
	
		return true;
	}

	private void MoveDegreeShan() {
		startAngle += degree;
		if (startAngle > 360 || startAngle < -360) {
			startAngle = startAngle % 360;
		}

		if (startAngle >= 150 && startAngle < 270) {
			selectFlag = 0;
			// ��ʾ��һ��
		} else if (startAngle >= 30 && startAngle < 150) {
			// ��ʾ������
			selectFlag = 12;
		} else {
			// ��ʾ�ڶ���
			selectFlag = 6;
		}
		//setAnimation(startAngle % 120);
		setAlphaForPaint((startAngle-30)%120);
	}

	private void setAlphaForPaint(int number){
		int numberForAlpha = number*255/120;
		paintForBitmap.setAlpha(255-numberForAlpha);
		paintForText.setAlpha(255-numberForAlpha);
	}
	private void setAnimation(int number) {
		for (int i = 0; i < AllPoints.length; i++) {
			AllPoints[i].bitmap = setAlpha(AllPoints[i].bitmap, number);
		}
	}

	private void upDegreeShan() {
		if (startAngle >= 150 && startAngle < 270) {
			selectFlag = 0;
			startAngle = 210;
			setAlphaForPaint(255);
			// ��ʾ��һ��
		} else if (startAngle >= 30 && startAngle < 150) {
			// ��ʾ������
			selectFlag = 12;
			startAngle = 90;
			setAlphaForPaint(255);
		} else {
			// ��ʾ�ڶ���
			selectFlag = 6;
			startAngle = 330;
			setAlphaForPaint(255);
		}
		
	}

	@Override
	public void onDraw(Canvas canvas) {

		Bitmap bitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.quick_launcher_bg))).getBitmap();
		Bitmap girlBitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.quick_launcher_tab_bg))).getBitmap();
		canvas.drawBitmap(bitmap, mPointX - bitmap.getWidth() / 2, mPointY
				- bitmap.getHeight() / 2, null);
		canvas.drawBitmap(girlBitmap, mPointX - girlBitmap.getWidth() / 2,
				mPointY - girlBitmap.getHeight() / 2, null);

		RectF rect = new RectF(mPointX - girlBitmap.getWidth() / 2, mPointY
				- girlBitmap.getHeight() / 2, mPointX + girlBitmap.getWidth()
				/ 2, mPointY + girlBitmap.getHeight() / 2);
		canvas.drawArc(rect, // ������ʹ�õľ��������С
				startAngle, // ��ʼ�Ƕ�
				120, // ɨ���ĽǶ�
				true, // �Ƿ�ʹ������
				paintForShan);

		for (int index = 0 + selectFlag; index < 6 + selectFlag; index++) {

			drawInCenter(canvas, AllPoints[index].bitmap, AllPoints[index].x,
					AllPoints[index].y, AllPoints[index].flag,
					AllPoints[index].appName, AllPoints[index].packageName);
		}
	}

	/**
	 * 
	 * ��������drawInCenter ���ܣ��ѵ�ŵ�ͼƬ���Ĵ� ������
	 */
	void drawInCenter(Canvas canvas, Bitmap bitmap, float left, float top,
			int flag, String appName, String packageName) {
		//canvas.drawPoint(left, top, paintForLine);

		/*if (chooseBtn == flag) {
			
			 mMatrix.setScale(70f / bitmap.getWidth(), 70f /
			 bitmap.getHeight());// ���� 
			 mMatrix.postTranslate(left - 35, top -35);// �ƶ�
			 canvas.drawBitmap(bitmap, mMatrix, null);
		} else {*/
			canvas.drawBitmap(bitmapBg, left - bitmap.getWidth() / 2, top
					- bitmap.getHeight() / 2, paintForBitmap);
			canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top
					- bitmap.getHeight() / 2, paintForBitmap);
			// ����appName
			canvas.drawText(appName, left, top + bitmap.getHeight() / 2 + 20,
					paintForText);
	}

	/**
	 * ����͸����
	 * @param sourceImg
	 * @param number
	 * @return
	 */
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (number <<24) | (argb[i] & 0x00FFFFFF);// �޸����2λ��ֵ
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	public class Point {
		public int flag;// λ�ñ�ʶ
		Bitmap bitmap;// ͼƬ
		String appName;
		public String packageName;
		int angle;// �Ƕ�
		float x;// x����
		float y;// y����
		float x_c;// ����Բ�ĵ�����x����
		float y_c;// ����Բ�ĵ�����y����
		int acrossDegree = 60;// ÿ��ͼ���ķ�ΧΪ60��
		int startDegree;
		boolean isCheck = false;
	}

	private class Shan {
		int startDegree;// ��ʼ�Ƕ�
		int acrossDegree = 120;// ���
		int flag;// λ�ñ�ʶ
		boolean isCheck;// �Ƿ�ѡ��
	}

	public static interface OnTurnplateListener {
		public void onPointTouch(Point point);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return true;
	}

	
}
