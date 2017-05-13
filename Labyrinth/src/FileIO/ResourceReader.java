package FileIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class ResourceReader {
	public static String readText(final Context context, final int id)
	{
		final InputStream in = context.getResources().openRawResource(id);
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String next;
		final StringBuilder sb = new StringBuilder();
		
		try{
			while((next = br.readLine()) != null)
			{
				sb.append(next + '\n');
			}
		}
		catch(IOException e)
		{
			return null;
		}
		
		return sb.toString();
	}
}
