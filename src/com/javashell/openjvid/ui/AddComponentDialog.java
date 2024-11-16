package com.javashell.openjvid.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.configuration.jVidNodeComponentDescriptor;
import com.javashell.openjvid.jnodecomponents.EmulatedComponent;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.DigestNodeFactory;
import com.javashell.openjvid.jnodecomponents.processors.EgressNodeFactory;
import com.javashell.openjvid.jnodecomponents.processors.IngestNodeFactory;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.openjvid.peripheral.PeripheralDescriptor;
import com.javashell.openjvid.ui.components.DimensionInputComponent;
import com.javashell.openjvid.ui.components.FileInputComponent;
import com.javashell.openjvid.ui.components.FloatInputComponent;
import com.javashell.openjvid.ui.components.IntegerInputComponent;
import com.javashell.openjvid.ui.components.JackInputComponent;
import com.javashell.openjvid.ui.components.PeripheralInputComponent;
import com.javashell.openjvid.ui.components.StringInputComponent;
import com.javashell.openjvid.ui.components.URLInputComponent;
import com.javashell.video.VideoProcessor;

public class AddComponentDialog extends JDialog {
	private static final long serialVersionUID = -5715707375013666122L;

	public final static String[] componentTypes, shownComponentTypes;

	public final static HashMap<String, Method> callBackMethods;

	static {
		callBackMethods = new HashMap<String, Method>();
		System.out.println("Checking for callback methods");

		Method[] ingestMethods = IngestNodeFactory.class.getMethods();
		Method[] digestMethods = DigestNodeFactory.class.getMethods();
		Method[] egressMethods = EgressNodeFactory.class.getMethods();

		String[] _componentTypes = new String[ingestMethods.length + digestMethods.length + egressMethods.length];
		String[] _shownComponentTypes = new String[_componentTypes.length];

		int ctIndex = 0, sctIndex = 0;

		for (Method m : ingestMethods) {
			if (m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) {
				TypeName type = m.getAnnotation(TypeNameAnnotation.TypeName.class);
				callBackMethods.put(type.typeName(), m);
				_componentTypes[ctIndex] = type.typeName();
				ctIndex++;
				if (type.isShown()) {
					_shownComponentTypes[sctIndex] = type.typeName();
					sctIndex++;
				}
				System.out.println("Found method " + type.typeName());
			}
		}

		for (Method m : digestMethods) {
			if (m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) {
				TypeName type = m.getAnnotation(TypeNameAnnotation.TypeName.class);
				callBackMethods.put(type.typeName(), m);
				_componentTypes[ctIndex] = type.typeName();
				ctIndex++;
				if (type.isShown()) {
					_shownComponentTypes[sctIndex] = type.typeName();
					sctIndex++;
				}
				System.out.println("Found method " + type.typeName());
			}
		}

		for (Method m : egressMethods) {
			if (m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) {
				TypeName type = m.getAnnotation(TypeNameAnnotation.TypeName.class);
				_componentTypes[ctIndex] = type.typeName();
				ctIndex++;
				if (type.isShown()) {
					_shownComponentTypes[sctIndex] = type.typeName();
					sctIndex++;
				}
				callBackMethods.put(type.typeName(), m);
			}
		}

		componentTypes = Arrays.copyOfRange(_componentTypes, 0, ctIndex);
		shownComponentTypes = Arrays.copyOfRange(_shownComponentTypes, 0, sctIndex);
	}

	private static JPanel currentInputPanel;
	private final JNodeFlowPane pane;
	private final boolean isEmulated;

	public AddComponentDialog(Frame owner, JNodeFlowPane pane, boolean isEmulated) {
		super(owner, "Add Component", true);
		this.isEmulated = isEmulated;
		this.pane = pane;

		JComboBox<String> componentSelection = new JComboBox<String>(shownComponentTypes);
		// Component selection will drive jpanel generation via generateInputPanel based
		// on the selected component type
		currentInputPanel = generateInputPanel(callBackMethods.get(shownComponentTypes[0]));
		componentSelection.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				System.out.println("Selection changed");
				remove(currentInputPanel);
				currentInputPanel = generateInputPanel(
						callBackMethods.get(componentSelection.getSelectedItem().toString()));
				add(currentInputPanel, BorderLayout.CENTER);
				System.out.println("LOADED: " + componentSelection.getSelectedItem().toString());
				revalidate();
				pack();
			}
		});

		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BorderLayout());

		JLabel selectionLabel = new JLabel("Select a type:");
		selectionPanel.add(selectionLabel, BorderLayout.WEST);
		selectionPanel.add(componentSelection, BorderLayout.EAST);

		setLayout(new BorderLayout());

		add(selectionPanel, BorderLayout.NORTH);
		setSize(275, 500);
		setVisible(true);

		add(currentInputPanel, BorderLayout.CENTER);
		revalidate();
		pack();
	}

	private JPanel generateInputPanel(Method m) {
		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS);
		inputPanel.setLayout(layout);

		Class<?>[] paramTypes = m.getParameterTypes();
		final Parameter[] params = m.getParameters();
		final Action[] paramActions = new Action[params.length];

		final Hashtable<Parameter, Object> parameterValues = new Hashtable<Parameter, Object>();

		int index = 0;

		for (Class<?> param : paramTypes) {
			Parameter p = params[index];
			// Identify type name, or specified label
			ParameterLabelAnnotation.Label labelAnnotation = p.getAnnotation(ParameterLabelAnnotation.Label.class);
			String label = p.getName();
			if (labelAnnotation != null) {
				label = labelAnnotation.label();
			}

			// Parse parameter type and generate proper input panel

			if (param.isInstance(new String())) {
				StringInputComponent inputComponent = new StringInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {

					private static final long serialVersionUID = 523150373601699354L;

					@Override
					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getString());
					}

				};
			}

			try {
				if (param.isInstance(new URL("https://google.com"))) {
					URLInputComponent inputComponent = new URLInputComponent(label);
					inputPanel.add(inputComponent);
					paramActions[index] = new AbstractAction() {

						private static final long serialVersionUID = 460808087052007952L;

						public void actionPerformed(ActionEvent e) {
							try {
								parameterValues.put(p, inputComponent.getURL());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					};
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (param.isAssignableFrom(int.class)) {
				IntegerInputComponent inputComponent = new IntegerInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {

					private static final long serialVersionUID = 8781780707545614243L;

					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getInt());
					}
				};
			}

			if (param.isInstance(new Dimension())) {
				DimensionInputComponent inputComponent = new DimensionInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {

					private static final long serialVersionUID = -4221662606504890055L;

					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getDimension());
					}
				};
			}

			if (param.isInstance(new File(""))) {
				FileInputComponent inputComponent = new FileInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {
					private static final long serialVersionUID = -2839715408638790225L;

					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getFile());
					}
				};
			}

			if (param.isInstance(new PeripheralDescriptor())) {
				PeripheralInputComponent inputComponent = new PeripheralInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {
					private static final long serialVersionUID = -2839715408638790225L;

					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getPeripheralDescriptor());
					}
				};
			}

			if (param.isAssignableFrom(float.class)) {
				FloatInputComponent inputComponent = new FloatInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {
					private static final long serialVersionUID = -2839715408638790225L;

					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getFloat());
					}
				};
			}

			if (param.isInstance(new JackInputComponent.JackInputClient(""))) {
				JackInputComponent inputComponent = new JackInputComponent(label);
				inputPanel.add(inputComponent);
				paramActions[index] = new AbstractAction() {
					private static final long serialVersionUID = -2839715408638790225L;

					public void actionPerformed(ActionEvent e) {
						parameterValues.put(p, inputComponent.getString());
					}
				};
			}
			index++;
		}

		JButton completeForm = new JButton("Complete");
		completeForm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Action a : paramActions) {
					if (a == null)
						continue;
					a.actionPerformed(e);
				}

				int paramsSize = (isEmulated ? params.length - 1 : params.length);
				Object[] paramValues = new Object[paramsSize];
				for (int i = 0; i < paramsSize; i++) {
					paramValues[i] = parameterValues.get(params[i]);
				}

				try {
					if (isEmulated) {
						for (int i = 0; i < paramValues.length; i++)
							System.out.println(i + " " + paramValues[i]);
						jVidNodeComponent<VideoProcessor> emuComp = new EmulatedComponent(pane);
						String compKey = null;
						for (String key : callBackMethods.keySet()) {
							if (m == callBackMethods.get(key)) {
								compKey = key;
								break;
							}
						}

						jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
								compKey, paramValues);

						if (m.isAnnotationPresent(TypeNameAnnotation.TypeName.class)) {
							TypeName type = m.getAnnotation(TypeNameAnnotation.TypeName.class);
							emuComp.setNodeType(type.nodeType());
						}
						emuComp.setNodeComponentDescriptor(desc);
						pane.add(emuComp);
					} else {
						paramValues[params.length - 1] = pane;

						for (int i = 0; i < paramValues.length; i++)
							System.out.println(i + " " + paramValues[i]);
						Component vidComp = (Component) m.invoke(null, paramValues);
						pane.add(vidComp);
					}
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
				setVisible(false);
				dispose();
			}
		});

		inputPanel.add(completeForm);
		return inputPanel;
	}

}
