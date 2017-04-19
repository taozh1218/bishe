package com.taozhang.filetransition.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;

/**
 * ɨ��ϵͳ�����е��ļ� sdcard
 * 
 * @author Administrator
 * 
 */
public class ScanSystemFile {

	/**
	 * ���ı�
	 */
	public final static String TEXT_PLAIN = "text/plain";// �����ı���
	/**
	 * ��HTML�ĵ���
	 */
	public final static String TEXT_HTML = "text/html";
	/**
	 * ��XHTML�ĵ���
	 */
	public final static String XHTML = "application/xhtml+xml";

	/**
	 * ��GIFͼ��
	 */
	public final static String GIF = "image/gif";
	/**
	 * ��PHP��Ϊ��image/pjpeg�� ��JPEGͼ��
	 */
	public final static String JPEG = "mage/jpeg";
	/**
	 * ��PNGͼ�񣩡�PHP��Ϊ��image/x-png��
	 */
	public final static String PNG = "image/png";
	/**
	 * ��MPEG������
	 */
	public final static String MPEG = "video/mpeg";
	/**
	 * ������Ķ��������ݣ�
	 */
	public final static String OCTET = "application/octet-stream";
	/**
	 * ��PDF�ĵ���
	 */
	public final static String PDF = "application/pdf";
	/**
	 * ��Microsoft Word�ļ���
	 */
	public final static String WORD = "application/msword";
	/**
	 * ��RFC 822��ʽ��
	 */
	public final static String RFC = "message/rfc822";
	/**
	 * ��HTML�ʼ���HTML��ʽ�ʹ��ı���ʽ����ͬ����ʹ�ò�ͬ��ʽ��ʾ��
	 */
	public final static String ALT = "multipart/alternative";
	/**
	 * ��ʹ��HTTP��POST�����ύ�ı���
	 */
	public final static String FORM = "application/x-www-form-urlencoded";
	/**
	 * ��ͬ�ϣ�����Ҫ���ڱ��ύʱ�����ļ��ϴ��ĳ��ϣ�
	 */
	public final static String FORM_DATA = "multipart/form-data";

	public static Context context = App.context;
	
	
	
	public final  Map<String, List<FileInfo>> folders = new HashMap<String, List<FileInfo>>();
	public Map<String, List<FileInfo>> getFolders(){
		return folders;
	}

	public static ArrayList<FileInfo> scanMusicFile() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA }, "_size>?",
				new String[] { 1024 * 1024 + "" }, null);

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		while (cursor.moveToNext()) {
			String name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
			String type = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
			String data = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			long id = cursor.getLong(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			// get thumbnail
//			Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
//					MyApplication.cr, id,
			
//			Log.e("Name,Type,data,thumbnail", name + " ," + type + ", " + data + "," +  thumbnail);
			FileInfo file = new FileInfo();//name, type, data
			file.dbId = id;
			file.fileName = name;
			file.fileType = type;
			file.filePath = data;
			fileList.add(file);

		}
		cursor.close();
		return fileList;
	}
//	/**
//	 * @Description ��ȡר������
//	 * @param filePath �ļ�·����like XXX/XXX/XX.mp3
//	 * @return ר������bitmap
//	 */
//	public static Bitmap createAlbumArt(final String filePath) {
//	    Bitmap bitmap = null;
//	    //�ܹ���ȡ��ý���ļ�Ԫ���ݵ���
//	    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//	    try {
//	        retriever.setDataSource(filePath); //��������Դ
//	        byte[] embedPic = retriever.getEmbeddedPicture(); //�õ��ֽ�������
//	        bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length); //ת��ΪͼƬ
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    } finally {
//	        try {
//	            retriever.release();
//	        } catch (Exception e2) {
//	            e2.printStackTrace();
//	        }
//	    }
//	    return bitmap;
//	}
	
	/**
	 * ��Ƶ
	 * 
	 * @return
	 */
	public static ArrayList<FileInfo> scanVideoFile() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Video.Media._ID,
						MediaStore.Video.Media.DISPLAY_NAME,
						MediaStore.Video.Media.TITLE,
						MediaStore.Video.Media.DURATION,
						MediaStore.Video.Media.ARTIST,
						MediaStore.Video.Media.ALBUM,
						MediaStore.Video.Media.MIME_TYPE,
						MediaStore.Video.Media.SIZE,
						MediaStore.Video.Media.DATA }, "_size>?",
				new String[] { 1024 * 1024 + "" }, null);

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		while (cursor.moveToNext()) {

			String name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
			String type = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
			long id = cursor.getLong(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
			
			String data = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

			// get the video thumbnail by id,only for video,the follew methods both are success
//			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data,Images.Thumbnails.MINI_KIND);
			Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
					App.cr, id,Images.Thumbnails.MICRO_KIND,options);
			FileInfo file = new FileInfo();//name, type, data
			file.fileName = name;
			file.fileType = type;
			file.filePath = data;
			// save id and thumbnail
			file.dbId = id;
			file.thumbnail = thumbnail;

			fileList.add(file);

		}
		cursor.close();
		return fileList;
	}

	/**
	 * ����ָ�����͵��ļ�
	 * 
	 * @param types
	 *            ָ��������
	 */
	public static ArrayList<FileInfo> scanAllFile(String[] selectionArg) {
		String[] columns = new String[] { MediaStore.Files.FileColumns.TITLE,
				MediaStore.Files.FileColumns.DATA,
				MediaStore.Files.FileColumns.MIME_TYPE };

		Uri uri = MediaStore.Files.getContentUri("external");
		String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

		Cursor c;
		c = App.context.getContentResolver().query(uri, columns,
				selection, selectionArg, null);
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		while (c.moveToNext()) {
			String title = c.getString(c
					.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
			String data = c.getString(c
					.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
			String type = c
					.getString(c
							.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
			FileInfo file = new FileInfo();//title, type, data
			file.fileName = title;
			file.fileType = type;
			file.filePath = data;
			fileList.add(file);
		}
		c.close();
		return fileList;
	}
	
	public boolean isRunning = false;
	  //��ͼ�����ֶ�
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.TITLE
    };
    final List<FileInfo> paths = new ArrayList<FileInfo>();
    public boolean isInited() {
        return paths.size() > 0;
    }
    
    
	public synchronized void initImage() {
        if (isRunning)
            return;
        isRunning=true;
        if (isInited())
            return;
        //��ȡ��ͼ���α�
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // ��ͼURI
                STORE_IMAGES,   // �ֶ�
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //����ʱ������
        if (cursor == null)
            return;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//��ͼID
            String path = cursor.getString(1);//��ͼ·��
            File file = new File(path);
            //�жϴ�ͼ�Ƿ����
            if (file.exists()) {
                //СͼURI
                String thumbUri = getThumbnail(id, path);
                //��ȡ��ͼURI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if(StringUtils.isEmpty(uri))
                    continue;
                if (StringUtils.isEmpty(thumbUri))
                    thumbUri = uri;
                //��ȡĿ¼��
                String folder = file.getParentFile().getName();

                FileInfo localFile = new FileInfo();
                localFile.fileName = cursor.getString(3);
                localFile.filePath = file.getAbsolutePath();
                localFile.oriURI = uri;
                localFile.thURI = thumbUri;
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.orientation = 360-degree;

                paths.add(localFile);
                //�ж��ļ����Ƿ��Ѿ�����
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<FileInfo> files = new ArrayList<FileInfo>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        folders.put("����ͼƬ", paths);
        cursor.close();
        isRunning=false;
    }
	  //Сͼ�����ֶ�
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA,
    };
	private String getThumbnail(int id, String path) {
        //��ȡ��ͼ������ͼ
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }
	
	 


}
