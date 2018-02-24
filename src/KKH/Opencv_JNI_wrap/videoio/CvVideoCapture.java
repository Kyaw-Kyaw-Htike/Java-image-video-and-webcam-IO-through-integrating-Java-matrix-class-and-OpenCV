/**
 * Copyright (C) 2018 Kyaw Kyaw Htike @ Ali Abdul Ghafur. All rights reserved.
 *
 */

package KKH.Opencv_JNI_wrap.videoio;

import KKH.StdLib.Matk;
import KKH.Opencv_JNI_wrap.imgcodecs.CvImage;;

public final class CvVideoCapture implements AutoCloseable {
	
	static {
		System.loadLibrary("opencv_world340");
		System.loadLibrary("KKH_Opencv_JNI_wrap");
	}
	
	public static enum Property
	{
		CAP_PROP_POS_MSEC, // Current position of the video file in milliseconds.
		CAP_PROP_POS_FRAMES, // 0-based index of the frame to be decoded/captured next.
		CAP_PROP_POS_AVI_RATIO, // Relative position of the video file: 0=start of the film, 1=end of the film.
		CAP_PROP_FRAME_WIDTH, // Width of the frames in the video stream.
		CAP_PROP_FRAME_HEIGHT, // Height of the frames in the video stream.
		CAP_PROP_FPS, // Frame rate.
		CAP_PROP_FOURCC, // 4-character code of codec. see VideoWriter::fourcc.
		CAP_PROP_FRAME_COUNT, // Number of frames in the video file.
		CAP_PROP_FORMAT, // Format of the Mat objects returned by VideoCapture::retrieve().
		CAP_PROP_MODE, // Backend-specific value indicating the current capture mode.
		CAP_PROP_BRIGHTNESS, // Brightness of the image (only for cameras).
		CAP_PROP_CONTRAST, // Contrast of the image (only for cameras).
		CAP_PROP_SATURATION, // Saturation of the image (only for cameras).
		CAP_PROP_HUE, // Hue of the image (only for cameras).
		CAP_PROP_GAIN, // Gain of the image (only for cameras).
		CAP_PROP_EXPOSURE, // Exposure (only for cameras).
		CAP_PROP_CONVERT_RGB, // Boolean flags indicating whether images should be converted to RGB.
		CAP_PROP_WHITE_BALANCE_BLUE_U, // Currently unsupported.
		CAP_PROP_RECTIFICATION, // Rectification flag for stereo cameras (note: only supported by DC1394 v 2.x backend currently).
		CAP_PROP_MONOCHROME, 
		CAP_PROP_SHARPNESS, 	
		CAP_PROP_AUTO_EXPOSURE, // DC1394: exposure control done by camera, user can adjust reference level using this feature.
		CAP_PROP_GAMMA, 	
		CAP_PROP_TEMPERATURE, 	
		CAP_PROP_TRIGGER, 	
		CAP_PROP_TRIGGER_DELAY, 	
		CAP_PROP_WHITE_BALANCE_RED_V, 	
		CAP_PROP_ZOOM, 	
		CAP_PROP_FOCUS, 	
		CAP_PROP_GUID, 	
		CAP_PROP_ISO_SPEED, 	
		CAP_PROP_BACKLIGHT,	
		CAP_PROP_PAN,
		CAP_PROP_TILT, 	
		CAP_PROP_ROLL,	
		CAP_PROP_IRIS, 	
		CAP_PROP_SETTINGS, 	
		CAP_PROP_BUFFERSIZE, // Pop up video/camera filter dialog (note: only supported by DSHOW backend currently. Property value is ignored)
		CAP_PROP_AUTOFOCUS;		
	}
	
	// stores pointer to dynamically allocated C++ object
	// need to free this after use
    private long obj_CvCapture; 
    
    public CvVideoCapture(String fpath)
    {
        obj_CvCapture = cvCreateFileCapture(fpath);
    }
    public CvVideoCapture(int index)
    {
        obj_CvCapture = cvCreateCameraCapture(index);
    }       
    
    public Matk get_frame()
    {
    	Matk img = cvQueryFrame(obj_CvCapture);
    	if(img==null)
    		return null;
    	else
    	{
        	CvImage.prepare_img_after_jni(img);
            return img;
    	}
    }
    
    public double get_prop(Property prop)
    {
    	return get_video_prop_jni(obj_CvCapture, prop.ordinal());
    }
    
    public double get_nframes()
    {
    	return get_prop(Property.CAP_PROP_FRAME_COUNT);
    }
    
    public double get_fps()
    {
    	return get_prop(Property.CAP_PROP_FPS);
    }
    
    public double get_width_frame()
    {
    	return get_prop(Property.CAP_PROP_FRAME_WIDTH);
    }
    
    public double get_height_frame()
    {
    	return get_prop(Property.CAP_PROP_FRAME_HEIGHT);
    }
    
    // get the current position in terms of milliseconds
    public double get_position_time()
    {
    	return get_prop(Property.CAP_PROP_POS_MSEC);
    }
    
    // get the current position in terms of frame count
    public double get_position_frameCount()
    {
    	return get_prop(Property.CAP_PROP_POS_FRAMES);
    }
    
    // set the current position in terms of milliseconds
    public void set_position_time(double value)
    {
    	set_prop(Property.CAP_PROP_POS_MSEC, value);
    }
    
    // set the current position in terms of frame count
    public void set_position_frameCount(double value)
    {
    	set_prop(Property.CAP_PROP_POS_FRAMES, value);
    }
    
    public void set_prop(Property prop, double value)
    {
    	set_video_prop_jni(obj_CvCapture, prop.ordinal(), value);
    }
    
	@Override
	public void close() {
		// TODO Auto-generated method stub
		System.out.println("Video object closed.");
		try {
			cvReleaseCapture(obj_CvCapture);
		} catch (Exception e)
		{
			System.out.println("Video object could not be closed. Ignored.");
		}		
	}
    
    private static native long cvCreateFileCapture(String fpath);
    private static native long cvCreateCameraCapture(int index);
    private static native void cvReleaseCapture(long obj_CvCapture);
    private static native Matk cvQueryFrame(long obj_CvCapture);
    private static native double get_video_prop_jni(long obj_CvCapture, int prop_id);
    private static native void set_video_prop_jni(long obj_CvCapture, int prop_id, double value);
}
