package com.example.entity;

import android.graphics.drawable.Drawable;
/**
 * Ӧ�õ���
 * @author Administrator
 *
 */
public class AppInfo {
	private String appName;//Ӧ������
	private Drawable drawable;//ͼ��
	private String packageName;//����
	private long firstInstallTime;//�״ΰ�װʱ��
	private long lastUpdateTime;//������ʱ��
	
	public long getFirstInstallTime() {
		return firstInstallTime;
	}

	public void setFirstInstallTime(long firstInstallTime) {
		this.firstInstallTime = firstInstallTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public String toString() {
		return "" + appName + drawable;
	}
}
