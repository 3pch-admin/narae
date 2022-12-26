/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package ext.narae.service.erp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ClassNotFoundException;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import wt.fc.InvalidAttributeException;
import wt.fc.ObjectReference;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospectionException;
import wt.introspection.WTIntrospector;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.ptc.windchill.annotations.metadata.*;
import wt.fc.ObjectReference;  // Preserved unmodeled dependency
import wt.fc.ObjectReference;  // Preserved unmodeled dependency
import java.util.Vector;  // Preserved unmodeled dependency
import wt.fc.ObjectReference;  // Preserved unmodeled dependency
import wt.fc.ObjectReference;  // Preserved unmodeled dependency
import java.util.Vector;  // Preserved unmodeled dependency

/**
 *
 * <p>
 * Use the <code>newERPHistory</code> static factory method(s), not the
 * <code>ERPHistory</code> constructor, to construct instances of this class.
 *  Instances must be constructed using the static factory(s), in order
 * to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/

@GenAsPersistable(
   serializable=Serialization.EXTERNALIZABLE_BASIC,
   properties={
   @GeneratedProperty(name="state", type=String.class,
      javaDoc="ERP_SUESS≪≫ERP_FAILE≪≫"),
   @GeneratedProperty(name="eoType", type=String.class,
      javaDoc="ECR,ECO"),
   @GeneratedProperty(name="message", type=String.class),
   @GeneratedProperty(name="historyType", type=String.class,
      javaDoc="1.CONFIRM (승인완료)≪≫2.COMPLETE(최종승인)"),
   @GeneratedProperty(name="partSend", type=String.class,
      javaDoc="ERP_SUCCESS≪≫ERP_FAILE≪≫ERP_NO≪≫"),
   @GeneratedProperty(name="bomSend", type=String.class,
      javaDoc="ERP_SUCCESS≪≫ERP_FAILE≪≫ERP_NO"),
   @GeneratedProperty(name="ecoSend", type=String.class,
      javaDoc="ERP_SUCCESS≪≫ERP_FAILE≪≫ERP_NO"),
   @GeneratedProperty(name="ecrSend", type=String.class,
      javaDoc="ERP_SUCCESS≪≫ERP_FAILE≪≫ERP_NO"),
   @GeneratedProperty(name="pdfSend", type=String.class),
   @GeneratedProperty(name="eo", type=ObjectReference.class,
      constraints=@PropertyConstraints(required=true))
   })
public class ERPHistory extends _ERPHistory {


   static final long serialVersionUID = 1;




   /**
    * Default factory for the class.
    *
    * @return    ERPHistory
    * @exception wt.util.WTException
    **/
   public static ERPHistory newERPHistory()
            throws WTException {

      ERPHistory instance = new ERPHistory();
      instance.initialize();
      return instance;
   }

   /**
    * Supports initialization, following construction of an instance.  Invoked
    * by "new" factory having the same signature.
    *
    * @exception wt.util.WTException
    **/
   protected void initialize()
            throws WTException {

   }

   /**
    * Gets the value of the attribute: IDENTITY.
    * Supplies the identity of the object for business purposes.  The identity
    * is composed of name, number or possibly other attributes.  The identity
    * does not include the type of the object.
    *
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @deprecated Replaced by IdentityFactory.getDispayIdentifier(object)
    * to return a localizable equivalent of getIdentity().  To return a
    * localizable value which includes the object type, use IdentityFactory.getDisplayIdentity(object).
    * Other alternatives are ((WTObject)obj).getDisplayIdentifier() and
    * ((WTObject)obj).getDisplayIdentity().
    *
    * @return    String
    **/
   public String getIdentity() {

      return null;
   }

   /**
    * Gets the value of the attribute: TYPE.
    * Identifies the type of the object for business purposes.  This is
    * typically the class name of the object but may be derived from some
    * other attribute of the object.
    *
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @deprecated Replaced by IdentityFactory.getDispayType(object) to return
    * a localizable equivalent of getType().  Another alternative is ((WTObject)obj).getDisplayType().
    *
    * @return    String
    **/
   public String getType() {

      return null;
   }

   @Override
   public void checkAttributes()
            throws InvalidAttributeException {

   }

}
