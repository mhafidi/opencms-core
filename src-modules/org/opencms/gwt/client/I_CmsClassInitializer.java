/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/Attic/I_CmsClassInitializer.java,v $
 * Date   : $Date: 2010/05/05 15:26:26 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.gwt.client;

/**
 * The interface for initializing all classes which implement the {@link I_CmsHasInit} interface.<p>
 * 
 * @see org.opencms.gwt.rebind.CmsInitGenerator
 * 
 * @author Georg Westenberger
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 8.0.0
 */
public interface I_CmsClassInitializer {

    /**
     * The method which, when called, should initialize all classes implementing the {@link I_CmsHasInit} interface.<p>
     */
    void initClasses();

}
