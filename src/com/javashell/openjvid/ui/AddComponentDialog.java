package com.javashell.openjvid.ui;

import java.awt.Frame;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.jnodecomponents.processors.DigestNodeFactory;
import com.javashell.openjvid.jnodecomponents.processors.IngestNodeFactory;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;

public class AddComponentDialog extends JDialog {
	private static final long serialVersionUID = -5715707375013666122L;

	public final static String[] componentTypes = { "NDI Ingest", "QOYV Ingest", "FFmpeg Ingest", "Amcrest Ingest",
			"AutoFraming Digest", "FacePaint Digest", "Matrix Digest", "Multiview Digest", "NDI Egress", "QOYV Egress",
			"FFmpeg Egress" };

	public final static HashMap<String, Method> callBackMethods;

	static {
		callBackMethods = new HashMap<String, Method>();
		System.out.println("Checking for callback methods");

		Method[] ingestMethods = IngestNodeFactory.class.getMethods();
		Method[] digestMethods = DigestNodeFactory.class.getMethods();
		// Method[] egressMethods = EgressNodeFactory.class.getMethods();

		for (Method m : ingestMethods) {
			if (m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) {
				TypeName type = m.getAnnotation(TypeNameAnnotation.TypeName.class);
				callBackMethods.put(type.typeName(), m);
				System.out.println("Found method " + type.typeName());
			}
		}

		for (Method m : digestMethods) {
			if (m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) {
				TypeName type = m.getAnnotation(TypeNameAnnotation.TypeName.class);
				callBackMethods.put(type.typeName(), m);
				System.out.println("Found method " + type.typeName());
			}
		}

		/*
		 * for(Method m : egressMethods) {
		 * if(m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) { TypeName type
		 * = m.getAnnotation(TypeNameAnnotation.TypeName.class);
		 * callBackMethods.put(type.typeName(), m); } }
		 */
	}

	public AddComponentDialog(Frame owner, JNodeFlowPane pane) {
		super(owner, "Add Component", true);
		JComboBox<String> componentSelection = new JComboBox<String>(componentTypes);

		JPanel selectionPanel = new JPanel();
		selectionPanel.add(componentSelection);

	}

	private JPanel generateInputPanel(Method m) {
		JPanel inputPanel = new JPanel();
		Class<?>[] paramTypes = m.getParameterTypes();

		for (Class<?> param : paramTypes) {
			if (param.isInstance(new String())) {
			}

			if (param.isInstance(Integer.valueOf(1))) {
			}
		}

		return inputPanel;
	}

}
