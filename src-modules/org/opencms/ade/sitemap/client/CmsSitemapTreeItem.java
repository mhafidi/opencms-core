/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/sitemap/client/Attic/CmsSitemapTreeItem.java,v $
 * Date   : $Date: 2010/06/18 07:29:54 $
 * Version: $Revision: 1.21 $
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

package org.opencms.ade.sitemap.client;

import org.opencms.ade.sitemap.client.hoverbar.CmsHoverbarMoveButton;
import org.opencms.ade.sitemap.client.hoverbar.CmsSitemapHoverbar;
import org.opencms.ade.sitemap.client.ui.css.I_CmsLayoutBundle;
import org.opencms.ade.sitemap.client.ui.css.I_CmsSitemapItemCss;
import org.opencms.ade.sitemap.shared.CmsClientSitemapEntry;
import org.opencms.file.CmsResource;
import org.opencms.gwt.client.ui.CmsListItemWidget;
import org.opencms.gwt.client.ui.tree.CmsDnDLazyTreeItem;
import org.opencms.gwt.client.ui.tree.CmsDnDTreeItem;
import org.opencms.gwt.client.ui.tree.CmsLazyTreeItem.LoadState;
import org.opencms.util.CmsStringUtil;
import org.opencms.xml.sitemap.CmsSitemapManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sitemap entry tree item implementation.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.21 $ 
 * 
 * @since 8.0.0
 * 
 * @see org.opencms.gwt.client.ui.tree.CmsLazyTreeItem
 * @see org.opencms.ade.sitemap.shared.CmsClientSitemapEntry
 */
public class CmsSitemapTreeItem extends CmsDnDLazyTreeItem {

    /** The CSS bundle used by this widget. */
    private static final I_CmsSitemapItemCss CSS = I_CmsLayoutBundle.INSTANCE.sitemapItemCss();

    /** The current sitemap entry. */
    private CmsClientSitemapEntry m_entry;

    /** The list item widget of this item. */
    private CmsListItemWidget m_listItemWidget;

    /** The original site path. */
    private String m_originalPath;

    /**
     * Default constructor.<p>
     * 
     * @param widget the widget to use
     * @param entry the sitemap entry
     * @param oriSitePath the original site path
     */
    public CmsSitemapTreeItem(CmsListItemWidget widget, CmsClientSitemapEntry entry, String oriSitePath) {

        super(widget);
        m_decoratedPanel.addDecorationBoxStyle(CSS.sitemapEntryDecoration());
        m_listItemWidget = widget;
        m_originalPath = oriSitePath;
        m_entry = entry;
        setId(getName(entry.getSitePath()));
        updateSitePath(entry.getSitePath());
        updateSitemapReferenceStatus(entry);
    }

    /**
     * Returns the original site path, in case this entry has been moved or renamed.<p>
     *
     * @return the original site path
     */
    public String getOriginalPath() {

        return m_originalPath;
    }

    /**
     * Returns the site path.<p>
     *
     * @return the site path
     */
    public String getSitePath() {

        return m_entry.getSitePath();
    }

    /**
     * Turns the highlighting for this item on or off.<p>
     * 
     * @param highlightOn if true, the highlighting is turned on, else off
     */
    public void highlight(boolean highlightOn) {

        if (highlightOn) {
            m_listItemWidget.getContentPanel().addStyleName(CSS.highlight());
        } else {
            m_listItemWidget.getContentPanel().removeStyleName(CSS.highlight());

        }
    }

    /**
     * Temporarily highlights an item.<p>
     * 
     * @param duration the duration for which  
     */
    public void highlightTemporarily(int duration) {

        int blinkInterval = 300;
        final int blinkCount = duration / blinkInterval;

        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

            private int m_counter;

            /**
             * @see com.google.gwt.core.client.Scheduler.RepeatingCommand#execute()
             */
            public boolean execute() {

                boolean finish = m_counter > blinkCount;
                highlight((m_counter % 2 == 0) && !finish);
                m_counter += 1;
                return !finish;
            }
        }, blinkInterval);
    }

    /**
     * @see org.opencms.gwt.client.ui.tree.CmsDnDTreeItem#onDragOverIn()
     */
    @Override
    public boolean onDragOverIn() {

        if (m_entry.getProperties().containsKey(CmsSitemapManager.Property.sitemap)) {
            // prevent dropping into a subsitemap driven entry
            return false;
        }
        return super.onDragOverIn();
    }

    /**
     * @see org.opencms.gwt.client.ui.CmsDnDListItem#onDragStart()
     */
    @Override
    public void onDragStart() {

        CmsSitemapHoverbar hoverbar = getHoverbar();
        if (hoverbar != null) {
            hoverbar.deattach();
        }
        super.onDragStart();
    }

    /**
     * @see com.google.gwt.user.client.ui.UIObject#toString()
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(m_entry.getSitePath()).append("\n");
        for (int i = 0; i < getChildCount(); i++) {
            CmsDnDTreeItem child = getChild(i);
            if (child instanceof CmsDnDLazyTreeItem.LoadingItem) {
                continue;
            }
            sb.append(child.toString());
        }
        return sb.toString();
    }

    /**
     * Refreshes the displayed data from the given sitemap entry.<p>
     * 
     * @param entry the sitemap entry to update
     */
    public void updateEntry(CmsClientSitemapEntry entry) {

        // since we keep a reference to the same entry we do not have to update it
        m_listItemWidget.setTitleLabel(entry.getTitle());
        m_listItemWidget.setAdditionalInfoValue(1, entry.getVfsPath());
        m_listItemWidget.updateTruncation();
        updateSitemapReferenceStatus(entry);
    }

    /**
     * Updates the recursively the site path.<p>
     * 
     * @param sitePath the new site path to set
     */
    public void updateSitePath(String sitePath) {

        if (m_listItemWidget.getSubtitleLabel().equals(sitePath)) {
            // nothing to do
            return;
        }
        m_listItemWidget.setSubtitleLabel(sitePath);
        String name = getName(sitePath);
        setId(name);
        m_listItemWidget.setAdditionalInfoValue(0, name);
        if (getLoadState() == LoadState.LOADED) {
            for (int i = 0; i < getChildCount(); i++) {
                CmsSitemapTreeItem item = (CmsSitemapTreeItem)getChild(i);
                item.updateSitePath(sitePath + CmsResource.getName(item.getSitePath()));
            }
        }
        m_listItemWidget.updateTruncation();
    }

    /**
     * @see org.opencms.gwt.client.ui.CmsDnDListItem#getDragHandle()
     */
    @Override
    protected Element getDragHandle() {

        if (!m_dndEnabled) {
            return null;
        }
        CmsSitemapHoverbar hoverbar = getHoverbar();
        if (hoverbar == null) {
            return null;
        }
        for (Widget button : hoverbar) {
            if (!(button instanceof CmsHoverbarMoveButton)) {
                continue;
            }
            if (!((CmsHoverbarMoveButton)button).isEnabled()) {
                return null;
            }
            return button.getElement();
        }
        return null;
    }

    /**
     * Return the name of this item, which can differ from the entry name for root nodes.<p>
     * 
     * @param sitePath the sitemap entry's site path 
     * 
     * @return the name
     */
    protected String getName(String sitePath) {

        String name = CmsResource.getName(sitePath);
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    /**
     * Changes the look of this widget if the entry passed as a parameter has a reference to a sub-sitemap.<p>
     * 
     * @param entry the entry which should be checked 
     */
    protected void updateSitemapReferenceStatus(CmsClientSitemapEntry entry) {

        if (!CmsStringUtil.isEmptyOrWhitespaceOnly(entry.getProperties().get(CmsSitemapManager.Property.sitemap.name()))) {
            m_listItemWidget.getContentPanel().addStyleName(CSS.subSitemapRef());
        } else {
            m_listItemWidget.getContentPanel().removeStyleName(CSS.subSitemapRef());
        }
    }

    /**
     * Retrieves the hoverbar, can be <code>null</code> if not attached.<p>
     * 
     * @return the hoverbar, or <code>null</code> if not attached
     */
    private CmsSitemapHoverbar getHoverbar() {

        for (Widget w : getListItemWidget().getContentPanel()) {
            if (!(w instanceof CmsSitemapHoverbar)) {
                continue;
            }
            return (CmsSitemapHoverbar)w;
        }
        return null;
    }

}
