package com.Carewell.Data;

public class Filter {

	static
    {
    	System.loadLibrary("CarewellFilter");
    }
	/*********************************低通滤波*****************************************/
	/**
	 * 165HZ低通滤波
	 * @param i
	 * @param OriData16
	 * @param countNum
	 * @return   滤波后得到的结果
	 */
	public native short LowPass165HzFilter(int i,short OriData16,int countNum);
	
	/**
	 * 对LowPass165HzFilter中静态变量的清零
	 */
	public native void LowPass165HzFilterFree();
	
	/**
	 * 100HZ低通滤波
	 * @return 滤波后得到的结果
	 */
	public native short LowPass100HzFilter(int i,short OriData16,int countNum);
	
	/**
	 * 对LowPass100HzFilter中静态变量的清零
	 */
	public native void LowPass100HzFilterFree();
	
	/**
	 * 90HZ低通滤波
	 * @return 滤波后得到的结果
	 */
	public native short LowPass90HzFilter(int i,short OriData16,int countNum);
	
	/**
	 * 对LowPass90HzFilter中静态变量的清零
	 */
	public native void LowPass90HzFilterFree();
	
	/**
	 * 75HZ低通滤波
	 * @return 滤波后得到的结果
	 */
	public native short LowPass75HzFilter(int i,short OriData16,int countNum);
	
	/**
	 * 对LowPass75HzFilter中静态变量的清零
	 */
	public native void LowPass75HzFilterFree();
	
	/***********************************************工频滤波*********************************************/
	
	/**
	 * 50HZ交流工频滤波
	 * @return 滤波后得到的结果
	 */
	public native short HUMFilter(int i,short OriData16,int countNum);
	
	
//	public native short HUM50Filter(int i,short OriData16,int countNum);
	
	/**
	 * 对HUMFilter中静态变量的清零
	 */
	public native void HUMFilterFree();
	
//	public native short HUM50FilterFree();
	
	/******************************************肌电干扰**************************************************/
	
	/**
	 * 滤除25HZ肌电干扰
	 * @return 滤波后得到的结果
	 */
	public native short EMG25Filter(int i,short OriData16,int countNum);
	
	/**
	 * 对EMG25Filter中静态变量的清零
	 */
	public native void EMG25FilterFree();
	
	/**
	 * 滤除35HZ肌电干扰
	 * @return 滤波后得到的结果
	 */
	public native short EMG35Filter(int i,short OriData16,int countNum);
	
	/**
	 * 对EMG35Filter中静态变量的清零
	 */
	public native void EMG35FilterFree();
	
	/**
	 * 滤除45HZ肌电干扰
	 * @return 滤波后得到的结果
	 */
	public native short EMG45Filter(int i,short OriData16,int countNum);
	
	/**
	 * 对EMG45Filter中静态变量的清零
	 */
	public native void EMG45FilterFree();
}
