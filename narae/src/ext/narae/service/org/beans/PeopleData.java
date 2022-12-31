/**
 * @(#)	PeopleData.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.4.2
 * @author Cho Sung Ok, jerred@e3ps.com
 */

package ext.narae.service.org.beans;

import java.net.URL;
import java.util.Vector;

import ext.narae.service.org.Department;
import ext.narae.service.org.DepartmentPeopleLink;
import ext.narae.service.org.People;
import ext.narae.service.org.WTUserPeopleLink;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class PeopleData {
	public static ReferenceFactory rf = new ReferenceFactory();
	public WTUser wtuser;
	public String wtuserOID;
	public People people;
	public String peopleOID;
	public Department department;

	public String id = "";
	public String name = "";
	public String name_en = "";
	public String email = "";
	public String duty = "";
	public String dutycode = "";
	public String gradeName = "";
	public String gradeCode = "";
	public String cccCode = "";
	public String cellTel = "";
	public String homeTel = "";
	public String officeTel = "";
	public String address = "";
	public String departmentName = "";
	public String password = "";
	public String imgUrl = CompanyState.defautlURL.toString();

	public boolean isDiable = false;

	public PeopleData() throws Exception {
		this(SessionHelper.manager.getPrincipal());
	}

	public PeopleData(Object obj) throws Exception {

		if (obj == null)
			return;

		if (obj instanceof WTPrincipalReference) {
			obj = (WTUser) ((WTPrincipalReference) obj).getObject();
		}

		if (obj instanceof Object[]) {
			Object[] o = (Object[]) obj;
			if (o[0] instanceof WTUser) {
				this.wtuser = (WTUser) o[0];
				this.people = (People) o[1];
			} else {
				this.people = (People) o[0];
				this.wtuser = (WTUser) o[1];
			}
		} else if (obj instanceof WTUser) {
			this.wtuser = (WTUser) obj;

			QueryResult qr = PersistenceHelper.manager.navigate(this.wtuser, "people", WTUserPeopleLink.class);
			if (qr.hasMoreElements()) {
				this.people = (People) qr.nextElement();
			} else {
				try {
					this.people = People.newPeople();
					this.people.setUser(this.wtuser);
					this.people.setName(this.wtuser.getFullName());
					this.people.setNameEn(this.wtuser.getFullName());
					this.people = (People) PersistenceHelper.manager.save(this.people);
					this.people.setIsDisable((this.people.isIsDisable() == false) ? false : this.people.isIsDisable());

				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				} catch (WTException e) {
					e.printStackTrace();
				}
			}

		} else if (obj instanceof People) {

			this.people = (People) obj;
			this.wtuser = this.people.getUser();
		}

		this.wtuserOID = PersistenceHelper.getObjectIdentifier(this.wtuser).getStringValue();
		this.peopleOID = PersistenceHelper.getObjectIdentifier(this.people).getStringValue();
		setWTUserData();
		setPeopleData();
		setDepartmentData();
	}

	public PeopleData(String oid) throws Exception {
		this(rf.getReference(oid).getObject());
	}

	private void setWTUserData() throws Exception {
		if (this.wtuser == null)
			return;

		this.id = this.wtuser.getName();
		this.name = this.wtuser.getFullName();
		this.name_en = this.wtuser.getFullName();
		this.email = this.wtuser.getEMail();
		if (this.email == null)
			this.email = "";
	}

	private void setPeopleData() throws Exception {
		if (this.people == null)
			return;

		this.name = this.people.getName();

		if (people.isIsDisable()) {
			this.name = name + "(퇴사)";
		}
		this.name_en = this.people.getNameEn();

		this.duty = this.people.getDuty();
		if (this.duty == null)
			this.duty = "";
		this.dutycode = this.people.getDutyCode();
		if (this.dutycode == null)
			this.dutycode = "";
		this.cellTel = this.people.getCellTel();
		if (this.cellTel == null)
			this.cellTel = "";
		this.homeTel = this.people.getHomeTel();
		if (this.homeTel == null)
			this.homeTel = "";
		this.officeTel = this.people.getOfficeTel();
		if (this.officeTel == null)
			this.officeTel = "";
		this.address = this.people.getAddress();
		if (this.address == null)
			this.address = "";
		this.isDiable = this.people.isIsDisable();
		this.password = this.people.getPassword();
		this.imgUrl = getImageURL();
	}

	private void setDepartmentData() throws Exception {
		if (people != null) {

			QueryResult qr = PersistenceHelper.manager.navigate(this.people, "department", DepartmentPeopleLink.class);
			if (qr.hasMoreElements()) {
				this.department = (Department) qr.nextElement();
				this.departmentName = this.department.getName();

				String[] prefix = { "중대형전지.", "Battery연구소." };

				for (int i = 0; i < prefix.length; i++) {
					if (departmentName.startsWith(prefix[i])) {
						this.departmentName = departmentName.substring(prefix[i].length());
						break;
					}
				}
			} else {
				this.departmentName = "지정안됨";
			}
		} else {
			this.departmentName = "지정안됨";
		}
	}

	public String getImageURL() throws Exception {
		this.people = (People) ContentHelper.service.getContents(this.people);
		Vector appList = ContentHelper.getApplicationData(this.people);
		URL picURL = CompanyState.defautlURL;
		if (appList.size() > 0) {
			ApplicationData pic = (ApplicationData) appList.get(0);
			picURL = ContentHelper.getDownloadURL(this.people, pic);
		}
		return picURL.toString();
	}

	public boolean getChief() {

		if (this.people.getChief() == null) {

			return false;
		}

		return this.people.getChief();

	}

}
