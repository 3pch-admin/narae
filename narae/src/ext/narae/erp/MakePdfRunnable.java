package ext.narae.erp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MakePdfRunnable implements Runnable {

	String _batch = "";
	public MakePdfRunnable(Object parameter) {
		// store parameter for later user
		_batch = (String)parameter;
	}

	public void run() {
		String result = "FAILURE"; 
		try {
			System.out.println("//==============================");
			System.out.println("//  DWG to PDF Convert Start ... ");
			System.out.println("//==============================");
			System.out.println("//==> 변환시작; Thread : " + _batch);
			Process p = Runtime.getRuntime().exec(_batch);
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";			
			while ((line = reader.readLine())!= null) {
				Thread.sleep(3000);
				break;
			}
			result = "SUCCESS"; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			System.out.println("//==============================");
			System.out.println("//  DWG to PDF Convert END ==> " + result);
			System.out.println("//==============================");
		}
	}
}