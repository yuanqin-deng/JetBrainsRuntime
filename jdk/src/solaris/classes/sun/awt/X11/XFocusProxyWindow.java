/*
 * Copyright 2003-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.awt.X11;

import java.awt.*;
import java.util.logging.*;

/**
 * This class represent focus holder window implementation. When toplevel requests or receives focus
 * it instead sets focus to this proxy. This proxy is mapped but invisible(it is kept at (-1,-1))
 * and therefore X doesn't control focus after we have set it to proxy.
 */
public class XFocusProxyWindow extends XBaseWindow {
    private static final Logger focusLog = Logger.getLogger("sun.awt.X11.focus.XFocusProxyWindow");
    XWindowPeer owner;

    public XFocusProxyWindow(XWindowPeer owner) {
        super(new XCreateWindowParams(new Object[] {
            BOUNDS, new Rectangle(-1, -1, 1, 1),
            PARENT_WINDOW, new Long(owner.getWindow()),
            EVENT_MASK, new Long(FocusChangeMask | KeyPressMask | KeyReleaseMask)
        }));
        this.owner = owner;
    }

    public void postInit(XCreateWindowParams params){
        super.postInit(params);
        setWMClass(getWMClass());
        xSetVisible(true);
    }

    protected String getWMName() {
        return "FocusProxy";
    }
    protected String[] getWMClass() {
        return new String[] {"Focus-Proxy-Window", "FocusProxy"};
    }

    public XWindowPeer getOwner() {
        return owner;
    }

    public void dispatchEvent(XEvent ev) {
        int type = ev.get_type();
        switch (type)
        {
          case XlibWrapper.FocusIn:
          case XlibWrapper.FocusOut:
              handleFocusEvent(ev);
              break;
        }
        super.dispatchEvent(ev);
    }

    public void handleFocusEvent(XEvent xev) {
        owner.handleFocusEvent(xev);
    }

    public void handleKeyPress(XEvent xev) {
        owner.handleKeyPress(xev);
    }

    public void handleKeyRelease(XEvent xev) {
        owner.handleKeyRelease(xev);
    }
}
