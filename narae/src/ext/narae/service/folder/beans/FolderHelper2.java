package ext.narae.service.folder.beans;

import java.net.URLEncoder;

import ext.narae.util.WCUtil;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class FolderHelper2 {
	public static Folder checkFolder(String folderStr, WTContainerRef container) throws WTException {
		String[] folders = folderStr.substring(1).split("/");
		String folderPath = "/Default";
		
		Folder childFolder = null;
		Folder parentFolder = null;
		childFolder = FolderHelper.service.getFolder(folderPath, container);
		
		for(int index=1 ; index < folders.length ; index++) {
			folderPath = folderPath + "/" + folders[index];
			if(existFolder(parentFolder, folders[index], (WTContainer)container.getObject()) == true ) {
				childFolder = FolderHelper.service.getFolder(folderPath, container);
			} else {
				childFolder = FolderHelper.service.createSubFolder(folderPath, container);
			}
			parentFolder = childFolder;
		}
		return childFolder;
	}
	
	public static boolean existFolder(Folder parent, String childName, WTContainer container) throws WTException {
		QuerySpec spec = new QuerySpec(SubFolder.class);
		
		spec.appendWhere(new SearchCondition(SubFolder.class, "name", SearchCondition.EQUAL, childName));
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(SubFolder.class, "containerReference.key.id", SearchCondition.EQUAL, container.getPersistInfo().getObjectIdentifier().getId()));
		spec.appendAnd();
		
		if( parent == null ) {
			spec.appendWhere(new SearchCondition(SubFolder.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, new Long(0)));
		} else {
			spec.appendWhere(new SearchCondition(SubFolder.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, parent.getPersistInfo().getObjectIdentifier().getId()));
		}
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		if( result.size() > 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String args[]) {
		
		try {
			WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
			WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
			Folder folder = FolderHelper2.checkFolder(URLEncoder.encode("/Default/한글폴더1"), partContainerRef);
			System.out.println(folder.getFolderPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
