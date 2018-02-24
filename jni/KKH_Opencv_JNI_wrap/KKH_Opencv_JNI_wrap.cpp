// Copyright (C) 2018 Kyaw Kyaw Htike @ Ali Abdul Ghafur. All rights reserved.

#include "KKH_Opencv_JNI_wrap.h"

/*
* Class:     KKH_Opencv_JNI_wrap_imgcodecs_CvImage
* Method:    imread_jni
* Signature: (Ljava/lang/String;ZI)LKKH/StdLib/Matk;
*/
JNIEXPORT jobject JNICALL Java_KKH_Opencv_1JNI_1wrap_imgcodecs_CvImage_imread_1jni
(JNIEnv *env, jclass cls, jstring fpath_, jboolean divBy255_, jint readmode_)
{
	jni_utils ju(env);
	std::string fpath = ju.from_jstring(fpath_);
	bool divBy255 = ju.from_jboolean(divBy255_);
	int readmode = ju.from_jint(readmode_);
	cv::Mat img = cv::imread(fpath, readmode);
	img.convertTo(img, CV_64F, 1.0/255.0);
	Matk m;
	switch (img.channels())
	{
	case 1:
		m.create<double, 1>(env, img, divBy255);
		break;
	case 2:
		m.create<double, 2>(env, img, divBy255);
		break;
	case 3:
		m.create<double, 3>(env, img, divBy255);
		break;
	case 4:
		m.create<double, 4>(env, img, divBy255);
		break;
	default:
		ju.throw_exception("ERROR from JNI: number of channels is not within the range of [1,4].\n");
		break;
	}
	
	return m.get_obj();
}

/*
* Class:     KKH_Opencv_JNI_wrap_imgcodecs_CvImage
* Method:    imwrite_jni
* Signature: (Ljava/lang/String;LKKH/StdLib/Matk;)V
*/
JNIEXPORT void JNICALL Java_KKH_Opencv_1JNI_1wrap_imgcodecs_CvImage_imwrite_1jni
(JNIEnv *env, jclass cls, jstring fpath_, jobject img_)
{
	jni_utils ju(env);
	Matk img; img.create(env, img_);
	cv::Mat imgcv;
	int nchannels = img.nchannels();

	switch (nchannels)
	{
	case 1:
		imgcv = img.to_cvMat<double, 1>();
		break;
	case 3:
		imgcv = img.to_cvMat<double, 3>();
		break;
	default:
		ju.throw_exception("ERROR from JNI: Input image must be either 1 or 3 channels");
		return;
	}

	std::string fpath = ju.from_jstring(fpath_);
	imgcv.convertTo(imgcv, CV_8U, 255.0);
	cv::imwrite(fpath, imgcv);

}

/*
* Class:     KKH_Opencv_JNI_wrap_imgcodecs_CvImage
* Method:    imshow_jni
* Signature: (LKKH/StdLib/Matk;ILjava/lang/String;)V
*/
JNIEXPORT void JNICALL Java_KKH_Opencv_1JNI_1wrap_imgcodecs_CvImage_imshow_1jni
(JNIEnv *env, jclass cls, jobject img_, jint delay_, jstring name_win_)
{
	jni_utils ju(env);
	Matk img; img.create(env, img_);
	cv::Mat imgcv;
	switch (img.nchannels())
	{
	case 1:
		imgcv = img.to_cvMat<double, 1>();
		break;
	case 3:
		imgcv = img.to_cvMat<double, 3>();
		break;
	default:
		ju.throw_exception("ERROR from JNI: Input image must be either 1 or 3 channels");
		return;
	}

	int delay = delay_;
	std::string name_win = ju.from_jstring(name_win_);

	cv::imshow(name_win, imgcv);
	cv::waitKey(delay);
}

/*
* Class:     KKH_Opencv_JNI_wrap_imgcodecs_CvImage
* Method:    imresize
* Signature: (LKKH/StdLib/Matk;IIDDI)LKKH/StdLib/Matk;
*/
JNIEXPORT jobject JNICALL Java_KKH_Opencv_1JNI_1wrap_imgcodecs_CvImage_imresize
(JNIEnv *env, jclass cls, jobject img_, jint width_new_, jint height_new_, jdouble fx_, jdouble fy_, jint interpolation_)
{
	jni_utils ju(env);

	Matk img; img.create(env, img_);
	cv::Mat imgcv;
	int nchannels = img.nchannels();
	switch (nchannels)
	{
	case 1:
		imgcv = img.to_cvMat<double, 1>();
		break;
	case 3:
		imgcv = img.to_cvMat<double, 3>();
		break;
	default:
		ju.throw_exception("ERROR from JNI: Input image must be either 1 or 3 channels");
		return nullptr;
	}

	int width_new = width_new_;
	int height_new = height_new_;
	double fx = fx_;
	double fy = fy_;
	int interpolation = interpolation_;
	cv::Size size_img_new(width_new, height_new);

	cv::Mat img_resized;
	cv::resize(imgcv, img_resized, size_img_new, fx, fy, interpolation);

	Matk img_out;

	switch (nchannels)
	{
	case 1:
		img_out.create<double, 1>(env, img_resized);
		break;
	case 3:
		img_out.create<double, 3>(env, img_resized);
		break;
	}

	return img_out.get_obj();
}


/*
* Class:     KKH_Opencv_JNI_wrap_videoio_CvVideoCapture
* Method:    cvCreateFileCapture
* Signature: (Ljava/lang/String;)J
*/
JNIEXPORT jlong JNICALL Java_KKH_Opencv_1JNI_1wrap_videoio_CvVideoCapture_cvCreateFileCapture
(JNIEnv *env, jclass cls, jstring fpath_)
{
	jni_utils ju(env);
	std::string fpath = ju.from_jstring(fpath_);
	cv::VideoCapture* capObj = new cv::VideoCapture(fpath);
	if (capObj == NULL)
		ju.throw_exception("ERROR from JNI: video capture from video file failed.");
	return (jlong)capObj;
}

/*
* Class:     KKH_Opencv_JNI_wrap_videoio_CvVideoCapture
* Method:    cvCreateCameraCapture
* Signature: (I)J
*/
JNIEXPORT jlong JNICALL Java_KKH_Opencv_1JNI_1wrap_videoio_CvVideoCapture_cvCreateCameraCapture
(JNIEnv *env, jclass cls, jint index_)
{
	jni_utils ju(env);
	int index = ju.from_jint(index_);
	cv::VideoCapture* capObj = new cv::VideoCapture(index);
	if (capObj == NULL)
		ju.throw_exception("ERROR from JNI: video capture from camera failed.");
	return (jlong)capObj;
}

/*
* Class:     KKH_Opencv_JNI_wrap_videoio_CvVideoCapture
* Method:    cvReleaseCapture
* Signature: (J)V
*/
JNIEXPORT void JNICALL Java_KKH_Opencv_1JNI_1wrap_videoio_CvVideoCapture_cvReleaseCapture
(JNIEnv *env, jclass cls, jlong obj_CvCapture_)
{
	cv::VideoCapture* capObj = (cv::VideoCapture*)obj_CvCapture_;
	delete capObj;
}

/*
* Class:     KKH_Opencv_JNI_wrap_videoio_CvVideoCapture
* Method:    cvQueryFrame
* Signature: (J)LKKH/StdLib/Matk;
*/
JNIEXPORT jobject JNICALL Java_KKH_Opencv_1JNI_1wrap_videoio_CvVideoCapture_cvQueryFrame
(JNIEnv *env, jclass cls, jlong obj_CvCapture_)
{
	cv::VideoCapture* capObj = (cv::VideoCapture*)obj_CvCapture_;
	cv::Mat frame; 
	capObj->read(frame);
	if (frame.empty()) // no more frame, return null
		return nullptr;
	frame.convertTo(frame, CV_64F, 1.0 / 255.0);

	Matk img;
	img.create<double, 3>(env, frame);
	return img.get_obj();

}


/*
* Class:     KKH_Opencv_JNI_wrap_videoio_CvVideoCapture
* Method:    get_video_prop_jni
* Signature: (JI)D
*/
JNIEXPORT jdouble JNICALL Java_KKH_Opencv_1JNI_1wrap_videoio_CvVideoCapture_get_1video_1prop_1jni
(JNIEnv *env, jclass cls, jlong obj_CvCapture_, jint prop_id_)
{
	cv::VideoCapture* capObj = (cv::VideoCapture*)obj_CvCapture_;
	jni_utils ju(env);
	int prop_id = ju.from_jint(prop_id_);
	double value = capObj->get(prop_id);
	return value;
}

/*
* Class:     KKH_Opencv_JNI_wrap_videoio_CvVideoCapture
* Method:    set_video_prop_jni
* Signature: (JID)V
*/
JNIEXPORT void JNICALL Java_KKH_Opencv_1JNI_1wrap_videoio_CvVideoCapture_set_1video_1prop_1jni
(JNIEnv *env, jclass cls, jlong obj_CvCapture_, jint prop_id_, jdouble value_)
{
	cv::VideoCapture* capObj = (cv::VideoCapture*)obj_CvCapture_;
	jni_utils ju(env);
	int prop_id = ju.from_jint(prop_id_);
	double value = ju.from_jdouble(value_);
	capObj->set(prop_id, value);
}