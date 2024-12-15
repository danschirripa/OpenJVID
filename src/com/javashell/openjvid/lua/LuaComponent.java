package com.javashell.openjvid.lua;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.swing.LuaImageWrapper;
import com.javashell.openjvid.lua.swing.components.LuaBorder;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;

public class LuaComponent extends LuaUserdata implements Serializable {
	private JComponent internalComp;
	private LuaComponent reference;
	protected LuaInterpreter interp;
	private LuaObject paintFunction = null;
	protected LuaObject eventCallback = null;
	private _LuaComponentEventListener evListener;
	private _LuaAccessibleContext context;

	private static final LuaObject luaComponentMetatable = Lua.newTable();

	public LuaComponent(LuaInterpreter interp) {
		this.interp = interp;
		internalComp = new _LuaComponent();
		evListener = new _LuaComponentEventListener();
		assignEventHandler();
		metatable = luaComponentMetatable;
		context = new _LuaAccessibleContext();
		reference = this;
	}

	public LuaComponent(LuaInterpreter interp, JComponent internalComp) {
		this.interp = interp;
		this.internalComp = internalComp;
		evListener = new _LuaComponentEventListener();
		assignEventHandler();
		metatable = luaComponentMetatable;
		context = new _LuaAccessibleContext();
		reference = this;
	}

	public void setJComponent(JComponent internalComp) {
		this.internalComp.removeMouseListener(evListener);
		this.internalComp.removeMouseMotionListener(evListener);
		this.internalComp.removeKeyListener(evListener);
		if (internalComp == null)
			internalComp = new _LuaComponent();
		else
			this.internalComp = internalComp;
		assignEventHandler();
	}

	@Override
	public Object getUserdata() {
		return internalComp;
	}

	@Override
	public String name() {
		return "LuaComponent";
	}

	private void assignEventHandler() {
		internalComp.addMouseListener(evListener);
		internalComp.addMouseMotionListener(evListener);
		internalComp.addKeyListener(evListener);
	}

	private class _LuaAccessibleContext extends AccessibleContext implements Serializable {
		@Override
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.PANEL;
		}

		@Override
		public AccessibleStateSet getAccessibleStateSet() {
			return new AccessibleStateSet();
		}

		@Override
		public int getAccessibleIndexInParent() {
			return 0;
		}

		@Override
		public int getAccessibleChildrenCount() {
			return 0;
		}

		@Override
		public Accessible getAccessibleChild(int i) {
			return null;
		}

		@Override
		public Locale getLocale() throws IllegalComponentStateException {
			return Locale.US;
		}

	}

	public class _LuaComponent extends JComponent {

		private static final long serialVersionUID = -8757032357422851131L;

		public _LuaComponent() {
		}

		@Override
		public void paintComponent(Graphics g) {
			if (paintFunction != null) {
				LuaGraphicsWrapper lgw = new LuaGraphicsWrapper(g);
				paintFunction.call(interp, lgw);
			}
		}

		@Override
		public AccessibleContext getAccessibleContext() {
			return context;
		}

	}

	public JComponent getComponent() {
		return internalComp;
	}

	public static class LuaGraphicsWrapper extends LuaUserdata implements Serializable {
		private Graphics g;
		private static final LuaObject luaGraphicsWrapperMetatable = Lua.newTable();

		public LuaGraphicsWrapper(Graphics g) {
			this.g = g;
			metatable = luaGraphicsWrapperMetatable;
		}

		@Override
		public Object getUserdata() {
			return this;
		}

		private Graphics getGraphics() {
			return g;
		}

		@Override
		public String name() {
			return "Graphics";
		}

		static {
			luaGraphicsWrapperMetatable.rawSet("__name", Lua.newString("GRAPHICS"));
			luaGraphicsWrapperMetatable.rawSet("__index", luaGraphicsWrapperMetatable);

			LuaObject clearRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("ClearRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					lgw.getGraphics().clearRect(x, y, width, height);
				}

			});

			LuaObject clipRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("ClipRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					lgw.getGraphics().clipRect(x, y, width, height);
				}

			});

			LuaObject copyAreaFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("CopyArea", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					int dx = args[5].getInt();
					int dy = args[6].getInt();
					lgw.getGraphics().copyArea(x, y, width, height, dx, dy);
				}

			});

			LuaObject draw3DRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("Draw3dRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.BOOLEAN);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					boolean raised = args[5].getBoolean();
					lgw.getGraphics().draw3DRect(x, y, width, height, raised);
				}

			});

			LuaObject drawArcFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawArc", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					int startAngle = args[5].getInt();
					int arcAngle = args[6].getInt();
					lgw.getGraphics().drawArc(x, y, width, height, startAngle, arcAngle);
				}

			});

			LuaObject drawImageFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawImage", args, LuaType.USERDATA, LuaType.USERDATA, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					LuaImageWrapper li = (LuaImageWrapper) args[1];
					int x = args[2].getInt();
					int y = args[3].getInt();
					lgw.getGraphics().drawImage(li.getImage(), x, y, null);
				}

			});

			LuaObject drawLineFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawLine", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x1 = args[1].getInt();
					int y1 = args[2].getInt();
					int x2 = args[3].getInt();
					int y2 = args[4].getInt();
					lgw.getGraphics().drawLine(x1, y1, x2, y2);
				}

			});

			LuaObject drawOvalFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawOval", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					lgw.getGraphics().drawOval(x, y, width, height);
				}

			});

			LuaObject drawPolygonFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawPolygon", args, LuaType.USERDATA, LuaType.TABLE);

					Set<Map.Entry<LuaObject, LuaObject>> table = args[1].getEntries();
					Iterator<Map.Entry<LuaObject, LuaObject>> iterator = table.iterator();
					int[] xPoints = new int[table.size()];
					int[] yPoints = new int[table.size()];
					int nPoints = table.size();

					int i = 0;
					while (iterator.hasNext()) {
						Map.Entry<LuaObject, LuaObject> entry = iterator.next();
						xPoints[i] = entry.getKey().getInt();
						yPoints[i] = entry.getValue().getInt();
						i++;
					}

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					lgw.getGraphics().drawPolygon(xPoints, yPoints, nPoints);
				}

			});

			LuaObject drawPolyLinesFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawPolyLines", args, LuaType.USERDATA, LuaType.TABLE);

					Set<Map.Entry<LuaObject, LuaObject>> table = args[1].getEntries();
					Iterator<Map.Entry<LuaObject, LuaObject>> iterator = table.iterator();
					int[] xPoints = new int[table.size()];
					int[] yPoints = new int[table.size()];
					int nPoints = table.size();

					int i = 0;
					while (iterator.hasNext()) {
						Map.Entry<LuaObject, LuaObject> entry = iterator.next();
						xPoints[i] = entry.getKey().getInt();
						yPoints[i] = entry.getValue().getInt();
						i++;
					}

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					lgw.getGraphics().drawPolyline(xPoints, yPoints, nPoints);
				}

			});

			LuaObject drawRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					lgw.getGraphics().drawRect(x, y, width, height);
				}

			});

			LuaObject drawRoundRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawRoundRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					int arcWidth = args[5].getInt();
					int arcHeight = args[6].getInt();
					lgw.getGraphics().drawRoundRect(x, y, width, height, arcWidth, arcHeight);
				}

			});

			LuaObject drawStringFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawString", args, LuaType.USERDATA, LuaType.ANY, LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					String text = args[1].getString();
					int x = args[2].getInt();
					int y = args[3].getInt();

					lgw.getGraphics().drawString(text, x, y);
				}

			});

			LuaObject fill3DRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("Fill3DRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.BOOLEAN);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					boolean raised = args[5].getBoolean();
					lgw.getGraphics().fill3DRect(x, y, width, height, raised);
				}

			});

			LuaObject fillArcFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("FillArc", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					int startAngle = args[5].getInt();
					int arcAngle = args[6].getInt();
					lgw.getGraphics().fillArc(x, y, width, height, startAngle, arcAngle);
				}

			});

			LuaObject fillOvalFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("FillOval", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					lgw.getGraphics().fillOval(x, y, width, height);
				}

			});

			LuaObject fillPolygonFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("FillPolygon", args, LuaType.USERDATA, LuaType.TABLE);

					Set<Map.Entry<LuaObject, LuaObject>> table = args[1].getEntries();
					Iterator<Map.Entry<LuaObject, LuaObject>> iterator = table.iterator();
					int[] xPoints = new int[table.size()];
					int[] yPoints = new int[table.size()];
					int nPoints = table.size();

					int i = 0;
					while (iterator.hasNext()) {
						Map.Entry<LuaObject, LuaObject> entry = iterator.next();
						xPoints[i] = entry.getKey().getInt();
						yPoints[i] = entry.getValue().getInt();
						i++;
					}

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					lgw.getGraphics().fillPolygon(xPoints, yPoints, nPoints);
				}

			});

			LuaObject fillRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("FillRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					lgw.getGraphics().fillRect(x, y, width, height);
				}

			});

			LuaObject fillRoundRectFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("DrawRoundRect", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER,
							LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					int x = args[1].getInt();
					int y = args[2].getInt();
					int width = args[3].getInt();
					int height = args[4].getInt();
					int arcWidth = args[5].getInt();
					int arcHeight = args[6].getInt();
					lgw.getGraphics().fillRoundRect(x, y, width, height, arcWidth, arcHeight);
				}

			});

			LuaObject setColorFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("SetColor", args, LuaType.USERDATA, LuaType.STRING);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];

					Color newColor = null;

					try {
						Field colorField = Class.forName("java.awt.Color").getField(args[1].getString());
						newColor = (Color) colorField.get(null);
					} catch (Exception e) {
						newColor = null;
					}

					lgw.getGraphics().setColor(newColor);
				}

			});

			LuaObject getColorFunction = Lua.newMethod(new LuaMethod() {

				@Override
				public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
					Lua.checkArgs("GetColor", args, LuaType.USERDATA);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					return Lua.newString(lgw.getGraphics().getColor().toString());
				}

			});

			LuaObject setFontFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

				@Override
				public void accept(LuaObject[] args) {
					Lua.checkArgs("SetFont", args, LuaType.USERDATA, LuaType.STRING);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					lgw.getGraphics().setFont(Font.getFont(args[1].getString()));
				}

			});

			LuaObject getFontFunction = Lua.newMethod(new LuaMethod() {

				@Override
				public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
					Lua.checkArgs("DrawRoundRect", args, LuaType.USERDATA);

					LuaGraphicsWrapper lgw = (LuaGraphicsWrapper) args[0];
					return Lua.newString(lgw.getGraphics().getFont().toString());
				}

			});

			luaGraphicsWrapperMetatable.rawSet("ClearRect", clearRectFunction);
			luaGraphicsWrapperMetatable.rawSet("ClipRect", clipRectFunction);
			luaGraphicsWrapperMetatable.rawSet("CopyArea", copyAreaFunction);
			luaGraphicsWrapperMetatable.rawSet("Draw3dRect", draw3DRectFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawArc", drawArcFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawImage", drawImageFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawLine", drawLineFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawOval", drawOvalFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawPolygon", drawPolygonFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawPolyLines", drawPolyLinesFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawRect", drawRectFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawRoundRect", drawRoundRectFunction);
			luaGraphicsWrapperMetatable.rawSet("DrawString", drawStringFunction);
			luaGraphicsWrapperMetatable.rawSet("Fill3dRect", fill3DRectFunction);
			luaGraphicsWrapperMetatable.rawSet("FillArc", fillArcFunction);
			luaGraphicsWrapperMetatable.rawSet("FillOval", fillOvalFunction);
			luaGraphicsWrapperMetatable.rawSet("FillPolygon", fillPolygonFunction);
			luaGraphicsWrapperMetatable.rawSet("FillRect", fillRectFunction);
			luaGraphicsWrapperMetatable.rawSet("FillRoundRect", fillRoundRectFunction);
			luaGraphicsWrapperMetatable.rawSet("SetColor", setColorFunction);
			luaGraphicsWrapperMetatable.rawSet("GetColor", getColorFunction);
			luaGraphicsWrapperMetatable.rawSet("SetFont", setFontFunction);
			luaGraphicsWrapperMetatable.rawSet("GetFont", getFontFunction);
		}
	}

	private class _LuaComponentEventListener implements MouseListener, MouseMotionListener, KeyListener, Serializable {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MouseClick");
			eventTable.rawSet("X", e.getX());
			eventTable.rawSet("Y", e.getY());
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MousePress");
			eventTable.rawSet("X", e.getX());
			eventTable.rawSet("Y", e.getY());
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MouseRelease");
			eventTable.rawSet("X", e.getX());
			eventTable.rawSet("Y", e.getY());
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MouseEnter");
			eventTable.rawSet("X", e.getX());
			eventTable.rawSet("Y", e.getY());
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MouseExit");
			eventTable.rawSet("X", e.getX());
			eventTable.rawSet("Y", e.getY());
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "KeyType");
			eventTable.rawSet("KeyCode", e.getExtendedKeyCode());
			eventTable.rawSet("KeyChar", e.getKeyChar());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "KeyPress");
			eventTable.rawSet("KeyCode", e.getExtendedKeyCode());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "KeyRelease");
			eventTable.rawSet("KeyCode", e.getExtendedKeyCode());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MouseDrag");
			eventTable.rawSet("X", e.getPoint().x);
			eventTable.rawSet("Y", e.getPoint().y);
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (eventCallback == null)
				return;
			LuaObject eventTable = Lua.newTable();
			eventTable.rawSet("Source", reference);
			eventTable.rawSet("EventType", "MouseMove");
			eventTable.rawSet("X", e.getX());
			eventTable.rawSet("Y", e.getY());
			eventTable.rawSet("Button", e.getButton());
			eventCallback.call(interp, eventTable);

		}

	}

	static {
		luaComponentMetatable.rawSet("__name", Lua.newString("COMPONENT"));
		luaComponentMetatable.rawSet("__index", luaComponentMetatable);

		LuaObject setSizeFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetSize", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER);
				LuaComponent lc = (LuaComponent) args[0];
				lc.internalComp.setSize(args[1].getInt(), args[2].getInt());
			}

		});

		LuaObject getSizeFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetSize", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];

				LuaObject dimensionTable = Lua.newTable();
				dimensionTable.rawSet(0, lc.internalComp.getWidth());
				dimensionTable.rawSet(1, lc.internalComp.getHeight());
				return dimensionTable;
			}

		});

		LuaObject addLuaComponentFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("AddComponent", args, LuaType.USERDATA, LuaType.USERDATA);
				LuaComponent main = (LuaComponent) args[0];
				LuaComponent toAdd = (LuaComponent) args[1];
				main.internalComp.add(toAdd.internalComp);
			}

		});

		LuaObject removeLuaComponentFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("RemoveComponent", args, LuaType.USERDATA, LuaType.USERDATA);
				LuaComponent main = (LuaComponent) args[0];
				LuaComponent toAdd = (LuaComponent) args[1];
				main.internalComp.remove(toAdd.internalComp);
			}

		});

		LuaObject setBoundsFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetBounds", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER,
						LuaType.INTEGER);
				LuaComponent lc = (LuaComponent) args[0];
				int width = args[3].getInt();
				int height = args[4].getInt();
				int x = args[1].getInt();
				int y = args[2].getInt();
				lc.internalComp.setBounds(x, y, width, height);
			}

		});

		LuaObject setGraphicsEventHandler = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetGraphicsEventHandler", args, LuaType.USERDATA, LuaType.ANY);
				LuaComponent lc = (LuaComponent) args[0];
				lc.paintFunction = args[1];
			}

		});

		LuaObject repaintFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("Repaint", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];
				lc.internalComp.repaint();
			}

		});

		LuaObject setLocationFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetLocation", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER);
				LuaComponent lc = (LuaComponent) args[0];
				lc.getComponent().setLocation(args[1].getInt(), args[2].getInt());
			}

		});

		LuaObject getLocationFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				Lua.checkArgs("GetLocation", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];
				LuaObject positionTable = Lua.newTable();
				positionTable.rawSet(0, lc.internalComp.getX());
				positionTable.rawSet(1, lc.internalComp.getY());
				return positionTable;
			}

		});

		LuaObject setEventHandler = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetHandler", args, LuaType.USERDATA, LuaType.ANY);
				LuaComponent lc = (LuaComponent) args[0];
				if (args[1] == Lua.NIL)
					lc.eventCallback = null;
				else
					lc.eventCallback = args[1];
			}

		});

		LuaObject setBackgroundColor = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetBackground", args, LuaType.USERDATA, LuaType.STRING);
				LuaComponent lc = (LuaComponent) args[0];

				Color newColor = null;

				try {
					Field colorField = Class.forName("java.awt.Color").getField(args[1].getString());
					newColor = (Color) colorField.get(null);
				} catch (Exception e) {
					return;
				}
				lc.internalComp.setBackground(newColor);
			}

		});

		LuaObject setForegroundColor = Lua.newFunc(new Consumer<LuaObject[]>() {
			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetForeground", args, LuaType.USERDATA, LuaType.STRING);
				LuaComponent lc = (LuaComponent) args[0];

				Color newColor = null;

				try {
					Field colorField = Class.forName("java.awt.Color").getField(args[1].getString());
					newColor = (Color) colorField.get(null);
				} catch (Exception e) {
					return;
				}
				lc.internalComp.setForeground(newColor);
			}
		});

		LuaObject getBackgroundColor = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetBackground", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];
				return Lua.newString(lc.internalComp.getBackground().toString());
			}

		});

		LuaObject getForegroundColor = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetForeground", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];
				return Lua.newString(lc.internalComp.getForeground().toString());
			}

		});

		LuaObject setVisibilityFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetVisible", args, LuaType.USERDATA, LuaType.BOOLEAN);
				LuaComponent lc = (LuaComponent) args[0];
				lc.internalComp.setVisible(args[1].getBoolean());
			}

		});

		LuaObject getVisibilityFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetVisible", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];
				return Lua.newBool(lc.internalComp.isVisible());
			}

		});

		LuaObject setPreferredSizeFunction = Lua.newFunc(new Consumer<LuaObject[]>() {
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetPreferredSize", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER);
				LuaComponent lc = (LuaComponent) args[0];
				lc.internalComp.setPreferredSize(new Dimension(args[1].getInt(), args[2].getInt()));
			}
		});

		LuaObject getPreferredSizeFunction = Lua.newMethod(new LuaMethod() {
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetPreferredSize", args, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];

				Dimension preferredSize = lc.internalComp.getPreferredSize();

				LuaObject dimensionTable = Lua.newTable();
				dimensionTable.rawSet(0, preferredSize.getWidth());
				dimensionTable.rawSet(1, preferredSize.getHeight());
				return dimensionTable;
			}
		});

		LuaObject setBorderFunction = Lua.newFunc(new Consumer<LuaObject[]>() {
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetBorder", args, LuaType.USERDATA, LuaType.USERDATA);
				LuaComponent lc = (LuaComponent) args[0];
				if (args[1] == Lua.NIL) {
					lc.internalComp.setBorder(null);
					return;
				}
				if (!(args[1] instanceof LuaBorder))
					Lua.badArgument(1, "SetBorder", "Expected BORDER");
				LuaBorder lb = (LuaBorder) args[1];
				lc.internalComp.setBorder(lb.getBorder());
			}
		});

		luaComponentMetatable.rawSet("SetSize", setSizeFunction);
		luaComponentMetatable.rawSet("GetSize", getSizeFunction);
		luaComponentMetatable.rawSet("AddComponent", addLuaComponentFunction);
		luaComponentMetatable.rawSet("RemoveComponent", removeLuaComponentFunction);
		luaComponentMetatable.rawSet("SetBounds", setBoundsFunction);
		luaComponentMetatable.rawSet("SetGraphicsEventHandler", setGraphicsEventHandler);
		luaComponentMetatable.rawSet("Repaint", repaintFunction);
		luaComponentMetatable.rawSet("SetLocation", setLocationFunction);
		luaComponentMetatable.rawSet("GetLocation", getLocationFunction);
		luaComponentMetatable.rawSet("SetHandler", setEventHandler);
		luaComponentMetatable.rawSet("SetBackground", setBackgroundColor);
		luaComponentMetatable.rawSet("SetForeground", setForegroundColor);
		luaComponentMetatable.rawSet("GetForegroundColor", getForegroundColor);
		luaComponentMetatable.rawSet("GetBackgroundColor", getBackgroundColor);
		luaComponentMetatable.rawSet("SetVisible", setVisibilityFunction);
		luaComponentMetatable.rawSet("GetVisible", getVisibilityFunction);
		luaComponentMetatable.rawSet("SetPreferredSize", setPreferredSizeFunction);
		luaComponentMetatable.rawSet("GetPreferredSize", getPreferredSizeFunction);
		luaComponentMetatable.rawSet("SetBorder", setBorderFunction);
	}

}
