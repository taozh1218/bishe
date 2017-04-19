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
	            // ����Ĳ��������RawContacts�������е�rawContactIdʹ������Զ���������ϵ�˵�rawContactId  
	            Uri rawContactUri = context.getContentResolver().insert(  
	                    RawContacts.CONTENT_URI, values);  
	            long rawContactId = ContentUris.parseId(rawContactUri);  
	            
	            
	            // ��data�������������  
	            if (given_name != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);  
	                values.put(StructuredName.GIVEN_NAME, given_name);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	  
	            // ��data�����绰����  
	            if (mobile_number != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);  
	                values.put(Phone.NUMBER, mobile_number);  
	                values.put(Phone.TYPE, Phone.TYPE_MOBILE);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	  
	            // ��data�����Email����  
	            if (work_email != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);  
	                values.put(Email.DATA, work_email);  
	                values.put(Email.TYPE, Email.TYPE_WORK);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	  
	            // ��data�����QQ����  
	            if (im_qq != "") {  
	                values.clear();  
	                values.put(Data.RAW_CONTACT_ID, rawContactId);  
	                values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);  
	                values.put(Im.DATA, im_qq);  
	                values.put(Im.PROTOCOL, Im.PROTOCOL_QQ);  
	                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,  
	                        values);  
	            }  
	            // ��data�����ͷ������  
//	            Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(),  
//	            		R_Avatar);  
//	            final ByteArrayOutputStream os = new ByteArrayOutputStream();  
//	            // ��Bitmapѹ����PNG���룬����Ϊ100%�洢  
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
