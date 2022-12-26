package ext.narae.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wt.org.WTUser;

public class ApprovalLineVO implements Serializable {
	List<WTUser> changManager1 = new ArrayList<WTUser>();
	List<WTUser> changManager2 = new ArrayList<WTUser>();
	List<WTUser> changManager3 = new ArrayList<WTUser>();
	
	List<List<String>> changeManagerInfo1 = new ArrayList<List<String>>();
	List<List<String>> changeManagerInfo2 = new ArrayList<List<String>>();
	List<List<String>> changeManagerInfo3 = new ArrayList<List<String>>();
	
	public List<List<String>> getChangeManagerInfo1() {
		return changeManagerInfo1;
	}
	public void setChangeManagerInfo1(List<List<String>> changeManagerInfo1) {
		this.changeManagerInfo1 = changeManagerInfo1;
	}
	public List<List<String>> getChangeManagerInfo2() {
		return changeManagerInfo2;
	}
	public void setChangeManagerInfo2(List<List<String>> changeManagerInfo2) {
		this.changeManagerInfo2 = changeManagerInfo2;
	}
	public List<List<String>> getChangeManagerInfo3() {
		return changeManagerInfo3;
	}
	public void setChangeManagerInfo3(List<List<String>> changeManagerInfo3) {
		this.changeManagerInfo3 = changeManagerInfo3;
	}
	public List<WTUser> getChangManager1() {
		return changManager1;
	}
	public void setChangManager1(List<WTUser> changManager1) {
		this.changManager1 = changManager1;
	}
	public List<WTUser> getChangManager2() {
		return changManager2;
	}
	public void setChangManager2(List<WTUser> changManager2) {
		this.changManager2 = changManager2;
	}
	public List<WTUser> getChangManager3() {
		return changManager3;
	}
	public void setChangManager3(List<WTUser> changManager3) {
		this.changManager3 = changManager3;
	}
}
