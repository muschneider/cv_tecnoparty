/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mauros.camera;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.media.Buffer;

/**
 *
 * @author Leticia Schneider
 */
public class CameraCapture {

    public VideoFormat vf = null;
    public BufferToImage btoi = null;
    public java.awt.Image img = null;
    public Buffer buf = null;
    public Player player = null;
    public CaptureDeviceInfo di = null;
    public MediaLocator ml = null;
    public static final String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";

    public Image getFrameImage() {
        if (player != null) {
            Image image = new Image();
            try {
                FrameGrabbingControl fgc = (FrameGrabbingControl)
                        player.getControl("javax.media.control.FrameGrabbingControl");
                buf = fgc.grabFrame();
                btoi = new BufferToImage((VideoFormat) buf.getFormat());
                img = btoi.createImage(buf);
                if (img != null) {
                    image.open(img);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        } else return null;
    }

    public void startDevice(Panel panel) {
        di = CaptureDeviceManager.getDevice(str2);
        ml = new MediaLocator(str2);
        try {
            player = Manager.createRealizedPlayer(ml);
            player.start();
            panel.add(player.getVisualComponent(), BorderLayout.CENTER);
            panel.doLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
