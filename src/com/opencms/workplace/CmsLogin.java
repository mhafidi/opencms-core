package com.opencms.workplace;

import com.opencms.file.*;
import com.opencms.core.*;
import com.opencms.util.*;
import com.opencms.template.*;

import javax.servlet.http.*;

import java.util.*;

/**
 * Definition of the CmsLogin.
 * 
 * @author Michael Emmerich
 * @version $Revision: 1.4 $ $Date: 2000/01/27 10:54:38 $
 */
public class CmsLogin extends CmsWorkplaceDefault {
           
    /**
     * Indicates if the results of this class are cacheable.
     * <P>
     * Complex classes that are able top include other subtemplates
     * have to check the cacheability of their subclasses here!
     * 
     * @param cms A_CmsObject Object for accessing system resources
     * @param templateFile Filename of the template file 
     * @param parameters Hashtable with all template class parameters.
     * @return <EM>true</EM> if cacheable, <EM>false</EM> otherwise.
     */
    public boolean isCacheable(A_CmsObject cms, String templateFile, Hashtable parameters) {
        return false;
    }

    
    public byte[] getContent(A_CmsObject cms, String templateFile, String elementName, Hashtable parameters, String templateSelector) throws CmsException {
        String result = null;     
        String user=null;
        // the template to be displayed
        String template="template";
        
        // get user name and password
        String name=(String)parameters.get("NAME");
        String password=(String)parameters.get("PASSWORD");
        
        // try to read this user
        if ((name != null) && (password != null)){
            try {
                user=cms.loginUser(name,password);
            } catch (CmsException e) {
                if (e.getType()==CmsException.C_NO_ACCESS) {
                    // there was an authentification error during login
                    // set user to null and switch to error template
                    user=null;     
                    template="error";
                } else {
                    throw e;
                }   
            }   
            // check if a user was found.
            if (user!= null) {
                // get a session for this user so that he is authentificated at the
                // end of this request
                HttpSession session = ((HttpServletRequest)cms.getRequestContext().getRequest().getOriginalRequest()).getSession(true);
                if(A_OpenCms.isLogging()) {
                    A_OpenCms.log(C_OPENCMS_INFO, "[CmsLogin] Login user " + user);
                }
            }
        }
        
        CmsXmlWpTemplateFile xmlTemplateDocument = new CmsXmlWpTemplateFile(cms,templateFile);        
            
        // this is the first time the dockument is selected, so reade the page forwarding
        if (user == null) {
            xmlTemplateDocument.clearStartup();
        }
        
        // process the selected template
        return startProcessing(cms,xmlTemplateDocument,"",parameters,template);
    
    }
        
}
