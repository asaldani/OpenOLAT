/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) frentix GmbH<br>
 * http://www.frentix.com<br>
 * <p>
 */
package com.frentix.olat.vitero.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.segmentedview.SegmentViewComponent;
import org.olat.core.gui.components.segmentedview.SegmentViewEvent;
import org.olat.core.gui.components.segmentedview.SegmentViewFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.id.OLATResourceable;
import org.olat.group.BusinessGroup;

/**
 * 
 * Description:<br>
 * 
 * <P>
 * Initial Date:  13 oct. 2011 <br>
 *
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class ViteroBookingsRunController extends BasicController {
	
	private Link bookingsLink;
	private Link adminLink;
	private SegmentViewComponent segmentView;
	private VelocityContainer mainVC;
	
	private ViteroBookingsController bookingsController;
	private ViteroBookingsEditController adminController;
	
	private final BusinessGroup group;
	private final OLATResourceable ores;
	private final String resourceName;
	
	public ViteroBookingsRunController(UserRequest ureq, WindowControl wControl, BusinessGroup group, OLATResourceable ores,
			String resourceName, boolean admin) {
		super(ureq, wControl);
		
		this.group = group;
		this.ores = ores;
		this.resourceName = resourceName;
		
		if(admin) {
			mainVC = createVelocityContainer("run_admin");
			
			segmentView = SegmentViewFactory.createSegmentView("segments", mainVC, this);
			bookingsLink = LinkFactory.createLink("booking.title", mainVC, this);
			segmentView.addSegment(bookingsLink, true);
			
			adminLink = LinkFactory.createLink("booking.admin.title", mainVC, this);
			segmentView.addSegment(adminLink, false);
			
			doOpenBookings(ureq);
			
			putInitialPanel(mainVC);
		} else {
			bookingsController = new ViteroBookingsController(ureq, wControl,group, ores);
			listenTo(bookingsController);
			putInitialPanel(bookingsController.getInitialComponent());
		}
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if(source == segmentView) {
			if(event instanceof SegmentViewEvent) {
				SegmentViewEvent sve = (SegmentViewEvent)event;
				String segmentCName = sve.getComponentName();
				Component clickedLink = mainVC.getComponent(segmentCName);
				if (clickedLink == bookingsLink) {
					doOpenBookings(ureq);
				} else if (clickedLink == adminLink){
					doOpenAdmin(ureq);
				}
			}
		}
	}
	
	private void doOpenBookings(UserRequest ureq) {
		if(bookingsController == null) {
			bookingsController = new ViteroBookingsController(ureq, getWindowControl(), group, ores);
			listenTo(bookingsController);
		} 
		mainVC.put("segmentCmp", bookingsController.getInitialComponent());
	}
	
	private void doOpenAdmin(UserRequest ureq) {
		if(adminController == null) {
			adminController = new ViteroBookingsEditController(ureq, getWindowControl(), group, ores, resourceName);
			listenTo(adminController);
		} 
		mainVC.put("segmentCmp", adminController.getInitialComponent());
	}
}