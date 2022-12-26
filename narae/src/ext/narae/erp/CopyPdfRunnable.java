package ext.narae.erp;

import java.io.File;
import java.io.IOException;

public class CopyPdfRunnable  implements Runnable {

	String _pdfdir = "";
	String _target = "";
	public CopyPdfRunnable(Object parameter) {
		// store parameter for later user
		try{
		_pdfdir = ((String[])parameter)[0];
		_target = ((String[])parameter)[1];
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void run() {
		int cnt = 0;
		File dwgdir = new File(_pdfdir);
		if(!dwgdir.isDirectory()){
			System.out.println("//==============================");
			System.out.println("//  생성된 AutoCAD PDF가 없습니다.  ");
			System.out.println("//==============================");
			return;
		} else {
			// dwg 갯수만 파악한다.
			for(File dwg : dwgdir.listFiles()){
				cnt = cnt + (dwg.getName().toLowerCase().matches("^.*.dwg$") ? 1 : 0);
			}
		} 
		while ((cnt * 2 + 1) != dwgdir.list().length) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("//==============================");
		System.out.println("//  PDF Copy Start ... ");
		System.out.println("//==============================");
		for(File pdf : dwgdir.listFiles()){
			if(!pdf.getAbsolutePath().toUpperCase().matches("^.*.PDF$")){
			//	pdf.delete(); // dpf가 아니면 삭제 //
				continue;
			}
			File pdfto = new File(_target + File.separator + pdf.getName());
			if(!pdfto.exists()){
				System.out.print(String.format("// ==> Copy from %s to %s ==> ", pdf.getAbsolutePath(), pdfto.getAbsolutePath()));
				try {
					ERPInterface.copyFile2(pdf.getAbsolutePath(), pdfto.getAbsolutePath());
					System.out.println("Success");
				} catch (IOException e) {
					System.out.println("Failure");
				}
			}
		}
		System.out.println("//==============================");
		System.out.println("//  PDF Copy END. ");
		System.out.println("//==============================");
	}


}
