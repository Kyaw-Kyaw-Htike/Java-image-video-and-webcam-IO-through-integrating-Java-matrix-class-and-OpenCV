/**
 * Copyright (C) 2018 Kyaw Kyaw Htike @ Ali Abdul Ghafur. All rights reserved.
 *
 */

package KKH.Opencv_JNI_wrap.imgcodecs;

import KKH.StdLib.Matk;

public final class CvImage {
	
	static {
		System.loadLibrary("opencv_world340");
		System.loadLibrary("KKH_Opencv_JNI_wrap");
	}
	
	public static enum  ImreadModes 
	{ 
		  IMREAD_UNCHANGED (-1), 
		  IMREAD_GRAYSCALE (0), 
		  IMREAD_COLOR (1), 
		  IMREAD_ANYDEPTH (2), 
		  IMREAD_ANYCOLOR (4), 
		  IMREAD_LOAD_GDAL (8), 
		  IMREAD_REDUCED_GRAYSCALE_2 (16), 
		  IMREAD_REDUCED_COLOR_2 (17), 
		  IMREAD_REDUCED_GRAYSCALE_4 (32), 
		  IMREAD_REDUCED_COLOR_4 (33), 
		  IMREAD_REDUCED_GRAYSCALE_8 (64), 
		  IMREAD_REDUCED_COLOR_8 (65), 
		  IMREAD_IGNORE_ORIENTATION (128);
		
		private int id;
		
		ImreadModes(int id)
		{
			this.id = id;
		}
		
		public int get_id()
		{
			return id;
		}
		
	};
	
	public static Matk prepare_img_before_jni(Matk img)
	{		
		Matk img_prepped; 		
	
    	if(!img.is_image())
    		throw new IllegalArgumentException("ERROR: the input matrix is not an image. Therefore, cannot convert proceed.");
    	
    	if(img.check_if_image_range_0_255())
    		img_prepped = img.div(255.0);    		
    	else
    		img_prepped = img.copy_deep();
				
		switch(img.get_image_type())
		{
			case GRAY:
			case BGR:				
			case BGRA:				
				break;
			case RGB:
				img_prepped = img_prepped.get_channels(new int[] {2,1,0});
				break;
			case RGBA:
				img_prepped = img_prepped.get_channels(new int[] {2,1,0,3});
				break;
			case ABGR:
				img_prepped = img_prepped.get_channels(new int[] {1,2,3,0});
				break;
			case ARGB:
				img_prepped = img_prepped.get_channels(new int[] {3,2,1,0});
				break;
			default:
				throw new IllegalArgumentException("ERROR: Failed preparing image for JNI. Input image has a type that is not valid for further processing.");
		}	
		
		return img_prepped;
	}
	
	public static void prepare_img_after_jni(Matk img)
	{
		int nchannels = img.nchannels();
		Matk.ImageType img_type;
		switch(nchannels)
		{
			case 1:
				img_type = Matk.ImageType.GRAY;
				break;
			case 3:
				img_type = Matk.ImageType.BGR;
				break;
			case 4:
				img_type = Matk.ImageType.BGRA;
				break;
			default:
				throw new IllegalArgumentException("ERROR: OpenCV JNI returns an image that has number of channels not equal to either 1, 3 or 4. So cannot determine the image type.");
		}
		img.set_as_image(img_type, false);
	}
	
	public static Matk imread(String fpath, ImreadModes readmode)
	{
		Matk img = imread_jni(fpath, false, readmode.get_id());	
		prepare_img_after_jni(img);
		return img;
	}	

	public static Matk imread(String fpath)
	{
		return imread(fpath, ImreadModes.IMREAD_COLOR);
	}
	
	private static native Matk imread_jni(String fpath, boolean divBy255, int readmode);
	
	
	public static void imwrite(String fpath, Matk img)
	{		
		imwrite_jni(fpath, prepare_img_before_jni(img));
	}
	
	private static native void imwrite_jni(String fpath, Matk img);
	
    /**
     * The interpolation method used for image resizing
     * NN: a nearest-neighbor interpolation
     * LINEAR: a bilinear interpolation (used by opencv by default)
     * CUBIC: a bicubic interpolation over 4x4 pixel neighborhood
     * AREA:  resampling using pixel area relation. It may be a preferred method for image decimation, as it gives moire’-free results. But when the image is zoomed, it is similar to the INTER_NEAREST method.
     * LANCZOS4: a Lanczos interpolation over 8x8 pixel neighborhood
     */
    public static enum Imresize_interpolation 
    { 
    	NN, 
    	LINEAR, 
    	CUBIC, 
    	AREA, 
    	LANCZOS4; 
	}
    
    private static native void imshow_jni(Matk img, int delay, String name_win);
    
    public static void imshow(Matk img, int delay, String name_win)
    {   	
    	imshow_jni(prepare_img_before_jni(img), delay,name_win);
    }
    
    public static void imshow(Matk img, int delay)
    {
        imshow(img, delay, "Window opencv");
    }
    
    public static void imshow(Matk img)
    {
        imshow(img, 0, "Window opencv");
    }

    public static Matk imresize(Matk img, int width_new, int height_new, double fx, double fy, Imresize_interpolation interpolation)
    {
    	Matk img_resized = imresize(prepare_img_before_jni(img), width_new, height_new, fx, fy, interpolation.ordinal());
    	prepare_img_after_jni(img_resized);
        return img_resized;
    }

    public static Matk imresize(Matk img, int width_new, int height_new, double fx, double fy)
    {
        return imresize(img, width_new, height_new, fx, fy, Imresize_interpolation.LINEAR);
    }

    public static Matk imresize(Matk img, int width_new, int height_new, Imresize_interpolation interpolation)
    {
        return imresize(img, width_new, height_new, 0, 0, interpolation);
    }

    public static Matk imresize(Matk img, int width_new, int height_new)
    {
        return imresize(img, width_new, height_new, 0, 0, Imresize_interpolation.LINEAR);
    }

    public static Matk imresize(Matk img, double scale, Imresize_interpolation interpolation)
    {
        return imresize(img, 0, 0, scale, scale, interpolation);
    }

    public static Matk imresize(Matk img, double scale)
    {
        return imresize(img, 0, 0, scale, scale, Imresize_interpolation.LINEAR);
    }

    private static native Matk imresize(Matk img, int width_new, int height_new, double fx, double fy, int interpolation);

	
	
}
