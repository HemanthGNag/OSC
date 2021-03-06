package com.edse.edu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.commons.io.FileUtils;

/**
 * Checks the state of the external storage of the device.
 * 
 */
public class StorageChecker
{

	private boolean externalStorageAvailable, externalStorageWriteable;

	/**
	 * Checks the external storage's state and saves it in member attributes.
	 */
	private void checkStorage()
	{
		// Get the external storage's state
		String state = Environment.getExternalStorageState();

		if (state.equals(Environment.MEDIA_MOUNTED))
		{
			// Storage is available and writeable
			externalStorageAvailable = externalStorageWriteable = true;
		}
		else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
		{
			// Storage is only readable
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		}
		else
		{
			// Storage is neither readable nor writeable
			externalStorageAvailable = externalStorageWriteable = false;
		}
	}

	/**
	 * Checks the state of the external storage.
	 * 
	 * @return True if the external storage is available, false otherwise.
	 */
	public boolean isExternalStorageAvailable()
	{
		checkStorage();

		return externalStorageAvailable;
	}

	/**
	 * Checks the state of the external storage.
	 * 
	 * @return True if the external storage is writeable, false otherwise.
	 */
	public boolean isExternalStorageWriteable()
	{
		checkStorage();

		return externalStorageWriteable;
	}

	/**
	 * Checks the state of the external storage.
	 * 
	 * @return True if the external storage is available and writeable, false
	 *         otherwise.
	 */
	public boolean isExternalStorageAvailableAndWriteable()
	{
		checkStorage();

		if (!externalStorageAvailable)
		{
			return false;
		}
		else if (!externalStorageWriteable)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@SuppressWarnings("null")
	public void writeImages(ArrayList<byte[]> images, Context context)
	{

		File dirPrev = null;
		File dirCurr = null;

		// 1. When writing of images is to take place we first check if
		// an SD card is available. If it is then that's where we
		// write. If it is not available then store the images in internal
		// storage private to our app.

		// 2. Next we need to check if there are already 4 system status images
		// in place that need to be updated with the new four or there aren't
		// any at all. First we need
		// to read from either the SD card or internal storage and see if we in
		// fact
		// have 4 images already in the directory. If we do then replace them
		// with the fresh
		// images (CHECK THAT WE DEFINITIVELY HAVE THESE IMAGES BEFORE
		// OVERWRITING). If not then just
		// write the four images.

		// 3. For reading our preferences are the same as before. First try to
		// read from the SD card and if
		// it's not there then go ahead and read from internal storage. At this
		// point the BitmapFactory
		// class is used to decode each byte array into a bitmap image. Theses
		// bitmap images are added to
		// an array list and ultimately returned.

		try
		{
			if (isExternalStorageAvailableAndWriteable())
			{
				File deviceSDCard = Environment.getExternalStorageDirectory();
				dirPrev = new File(deviceSDCard.getAbsolutePath() + "/previmgs");
				dirCurr = new File(deviceSDCard.getAbsolutePath()
						+ "/currentimgs");

				dirPrev.mkdirs();
				dirCurr.mkdirs();

			}
			else
			{
				dirPrev = context.getDir("previmgs", Context.MODE_PRIVATE);
				dirCurr = context.getDir("currentimgs", Context.MODE_PRIVATE);
				Log.d("INTERNAL STORAGE DIRECTORY PREVIOUS.",
						dirPrev.toString());
				Log.d("INTERNAL STORAGE DIRECTORY CURRENT.", dirCurr.toString());
			}

			// count images already in directory of internal storage or the SD
			// card.
			String[] files = null;
			if(dirCurr.isDirectory())
			{
				files = dirCurr.list();
			}
			File[] filesInDir = dirCurr.listFiles();
			ArrayList<byte[]> imgArgs = new ArrayList<byte[]>();

			// If four images are in the file directory and four images are
			// being passed in to replace them.
			// The images.size() == 4 guarantees that images are actually there
			// to replace the ones already in the system/SDcard.
			if (files.length > 0)
			{
				if (filesInDir.length == 4 && images.size() == 4)
				{
					// using apache commons libs to easily delete all previous
					// files
					// from the directory.
					for (File file : filesInDir)
					{
						byte[] addedImg = file.toString().getBytes();
						imgArgs.add(addedImg);

					}
					FileUtils.cleanDirectory(dirPrev); // clean files from prev
														// directory.
					writeToFolder(dirPrev, imgArgs); // write current images to
														// previous directory.
					FileUtils.cleanDirectory(dirCurr);// clean current directory
					writeToFolder(dirCurr, images); // write the 4 new images to
													// the current directory.
													// These are the latest.
				}
			}
			else
			{
				writeToFolder(dirCurr, images); // if there are no files in the
												// current directory then just
												// write.
			}
			// So either way this part of code should be writing images to the
			// directory whether its on the
			// SD card or internal storage.

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void writeToFolder(File dir, ArrayList<byte[]> images)
	{
		FileOutputStream outStream = null;
		int imgCount = 1;

		try
		{
			for (byte[] img : images)
			{
				File imgFile = new File(dir, "sysimg" + imgCount);
				outStream = new FileOutputStream(imgFile);

				// normally the method "getBytes()" would need to be called but
				// since we are
				// already dealing with Bytes then the img variable is left as
				// is.
				outStream.write(img);
				

				imgCount++;
			}

			outStream.flush();
			outStream.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public static ArrayList<byte[]> readImages(Context context)
			throws Exception
	{
		// would there be an event where images would be stored on both the sd
		// card and internal memory?
		// Perhaps someone is using are application with an SD initially and
		// then loses their SD card.
		StorageChecker checkWR = new StorageChecker();
		File dir = null;
		if (checkWR.isExternalStorageAvailableAndWriteable())
		{
			File deviceSDCard = Environment.getExternalStorageDirectory();
			dir = new File(deviceSDCard.getAbsolutePath() + "/currentimgs");
		}
		else
		{
			dir = context.getDir("currentimgs", Context.MODE_PRIVATE);
		}

		ArrayList<byte[]> images = new ArrayList<byte[]>(); // arraylist to
															// return images.
		File[] imgFiles = dir.listFiles();

		for (int i = 0; i < imgFiles.length; i++)
		{
			// if any of the four files cannot be read...
			if (!imgFiles[i].canRead())
			{
				throw new Exception("Error in getting system status images");
			}

			// retrieve from SD/internal as an array of bytes.
			byte[] imgBytes = FileUtils.readFileToByteArray(imgFiles[i]);

			// convert from array of bytes to a bitmap.
			//Bitmap img = BitmapFactory.decodeByteArray(imgBytes, 0,
				//	imgBytes.length);

			
			// add each bitmap image to the images arraylist.
			images.add(imgBytes);
		}
		return images;

	}
}