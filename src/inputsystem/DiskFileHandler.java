package inputsystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class DiskFileHandler implements FileHandler{
	String mFileName;
	FileInputStream stream;
	
	public DiskFileHandler(String name) {
		mFileName = name;
	}

	public void Open() {
		try {
			stream = new FileInputStream(mFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int Close() {
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int Read( byte[] buf, int begin, int len) {
		try {
			return stream.read(buf,begin,len);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
