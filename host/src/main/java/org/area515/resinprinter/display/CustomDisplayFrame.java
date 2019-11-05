package org.area515.resinprinter.display;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;



public class CustomDisplayFrame extends Frame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public GraphicsDevice graphicsDevice;

	public void setGraphicsDevice(GraphicsDevice device) {
		graphicsDevice = device;
	}

	public void repaint() {
		CustomPhotocentricDisplayDevice dev = (CustomPhotocentricDisplayDevice)graphicsDevice;
		Rectangle screenSize = dev.getBounds();
		BufferedImage bi = new BufferedImage(screenSize.width, screenSize.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = bi.createGraphics();
		paint( g2 );
		dev.outputImage( bi );
		g2.dispose();
		bi.flush();
	}

	public Rectangle getBounds() {
		CustomPhotocentricDisplayDevice dev = (CustomPhotocentricDisplayDevice)graphicsDevice;
		return dev.getBounds();
	}
}
