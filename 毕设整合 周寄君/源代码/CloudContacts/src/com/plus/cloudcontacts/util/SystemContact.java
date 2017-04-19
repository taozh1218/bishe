package com.plus.cloudcontacts.util;

import java.io.ByteArrayOutputStream;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;

public class SystemContact {
	
	 public static boolean insert(Context context,String given_name, String mobile_number,  
	            String work_email, String im_qq) {  
	        try {  
	            ContentValues values = new ContentValues();  
	            // 下面的操作会根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContactId  
	            Uri rawContactUri = context.getContentResolver().insert(  
	                    RawContacts.CONTENT_URI, values);  
	            long rawContactId = ContentUris.parseId(rawContactUri);  
	            
	            
	            // 向data表插入姓名数据  
	            if (given_name != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);  
	                values.put(StructuredName.GIVEN_NAME, given_name);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	  
	            // 向data表插入电话数据  
	            if (mobile_number != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);  
	                values.put(Phone.NUMBER, mobile_number);  
	                values.put(Phone.TYPE, Phone.TYPE_MOBILE);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	  
	            // 向data表插入Email数据  
	            if (work_email != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);  
	                values.put(Email.DATA, work_email);  
	                values.put(Email.TYPE, Email.TYPE_WORK);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	  
	            // 向data表插入QQ数据  
	            if (im_qq != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);  
	                values.put(Im.DATA, im_qq);  
	                values.put(Im.PROTOCOL, Im.PROTOCOL_QQ);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	            // 向data表插入头像数据  
//	            Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(),  
//	            		R_Avatar);  
//	            final ByteArrayOutputStream os = new ByteArrayOutputStream();  
//	            // 将Bitmap压缩成PNG编码，质量为100%存储  
//	            sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);  
//	            byte[] avatar = os.toByteArray();  
//	            values.put(Data.RAW_CONTACT_ID, rawContactId);  
//	            values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);  
//	            values.put(Photo.PHOTO, avatar);  
//	            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
//	                    values);  
	        }  
	  
	        catch (Exception e) {  
	            return false;  
	        }  
	        return true;  
	    }  
}
