package com.javashell.openjvid.configuration;

import java.awt.Color;
import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.JNodeFlowPane.Linkage;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.jnodecomponents.jVidMatrixNodeComponent;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.ui.components.AddComponentDialog;
import com.javashell.openjvid.ui.components.ThrobberPopup;

public class jVidConfigurationParser {

	public static void loadConfiguration(JNodeFlowPane flowPane, File configuration) {

		ThrobberPopup throb = new ThrobberPopup(200, 200, 8, Color.BLACK, 1.0f / 15.0f);

		Thread loadThread = new Thread(new Runnable() {
			public void run() {
				try (FileInputStream fin = new FileInputStream(configuration)) {
					final HashMap<UUID, jVidNodeComponent<?>> compsByUUID = new HashMap<UUID, jVidNodeComponent<?>>();
					final HashMap<UUID, HashSet<UUID>> linkagesByUUID = new HashMap<UUID, HashSet<UUID>>();

					final HashMap<UUID, MatrixDescriptor> unrealizedMatrices = new HashMap<UUID, MatrixDescriptor>();
					final Hashtable<UUID, Hashtable<UUID, HashSet<UUID>>> matrixCrosspoints = new Hashtable<UUID, Hashtable<UUID, HashSet<UUID>>>();

					// Load a node graph from a given file
					Scanner sc = new Scanner(fin);
					sc.useDelimiter("\\n\\t:\\r");
					while (sc.hasNext()) {
						final String descriptor = sc.next();
						final String[] values = descriptor.split("\n\t:");

						// Check if this line is a LINKAGE description
						if (values[0].equals("LINKAGE")) {
							UUID originLink = UUID.fromString(values[1]);
							HashSet<UUID> children = new HashSet<UUID>();
							for (int i = 2; i < values.length; i++) {
								children.add(UUID.fromString(values[i]));
							}
							linkagesByUUID.put(originLink, children);
							continue;
						}
						// Check if this line is a MATRIX description
						if (values[0].equals("MATRIX")) {
							UUID matrixID = UUID.fromString(values[1]);
							Hashtable<UUID, HashSet<UUID>> crosspoints = new Hashtable<UUID, HashSet<UUID>>();
							for (int i = 2; i < values.length; i++) {
								System.out.println(values[i]);
								var sourceID = UUID.fromString(values[i]);
								var sinks = values[i + 1].split(",");
								HashSet<UUID> sinkIDs = new HashSet<UUID>();
								for (var sink : sinks)
									sinkIDs.add(UUID.fromString(sink));

								crosspoints.put(sourceID, sinkIDs);
								i++;
							}
							matrixCrosspoints.put(matrixID, crosspoints);
							continue;
						}
						// If this line is neither a LINKAGE or MATRIX, it must just be an object
						// definition
						final UUID nodeID = UUID.fromString(values[0]);
						final String contentClass = values[1];

						final int x = Integer.parseInt(values[2]);
						final int y = Integer.parseInt(values[3]);

						final Object[] parameters = new Object[values.length - 3];

						// Decode all the parameter objects
						for (int i = 4; i < values.length; i++) {
							final byte[] decoded64 = Base64.getDecoder().decode(values[i]);
							final ByteArrayInputStream bin = new ByteArrayInputStream(decoded64);
							final ObjectInputStream oin = new ObjectInputStream(bin);
							parameters[i - 4] = oin.readObject();
						}
						// If this object is a type of Matrix, load appropriately
						if (contentClass.startsWith("Matrix")) {
							final MatrixDescriptor md = new MatrixDescriptor();
							md.parameters = parameters;
							md.x = x;
							md.y = y;
							md.contentClass = contentClass;
							unrealizedMatrices.put(nodeID, md);
							continue;
						}
						// Insert the flowPane parameter as the last index
						parameters[parameters.length - 1] = flowPane;
						// Load the associated callback method for creation
						Method creationMethod = AddComponentDialog.callBackMethods.get(contentClass);
						// Create the jVidNodeComponent
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								try {
									jVidNodeComponent<?> vidComp = (jVidNodeComponent<?>) creationMethod.invoke(null,
											parameters);
									vidComp.setLocation(x, y);
									compsByUUID.put(nodeID, vidComp);
									flowPane.add(vidComp);
									System.out.println(vidComp.getNodeName());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});

					}
					sc.close();

					for (UUID id : matrixCrosspoints.keySet()) {
						final HashMap<jVidNodeComponent<?>, HashSet<jVidNodeComponent<?>>> crosspoints = new HashMap<jVidNodeComponent<?>, HashSet<jVidNodeComponent<?>>>();
						for (var sourceID : matrixCrosspoints.get(id).keySet()) {
							jVidNodeComponent<?> source = compsByUUID.get(sourceID);
							final HashSet<jVidNodeComponent<?>> sinks = new HashSet<jVidNodeComponent<?>>();
							for (var sinkID : matrixCrosspoints.get(id).get(sourceID)) {
								sinks.add(compsByUUID.get(sinkID));
							}
							crosspoints.put(source, sinks);
						}
						MatrixDescriptor desc = unrealizedMatrices.get(id);
						var parameters = Arrays.copyOf(desc.parameters, desc.parameters.length + 1);
						parameters[parameters.length - 2] = crosspoints;
						parameters[parameters.length - 1] = flowPane;
						Method creationMethod = AddComponentDialog.callBackMethods.get(desc.contentClass);
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								try {
									jVidNodeComponent<?> vidComp = (jVidNodeComponent<?>) creationMethod.invoke(null,
											parameters);
									vidComp.setLocation(desc.x, desc.y);
									compsByUUID.put(id, vidComp);
									flowPane.add(vidComp);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});

					}

					for (UUID id : linkagesByUUID.keySet()) {
						jVidNodeComponent<?> origin = compsByUUID.get(id);
						HashSet<UUID> children = linkagesByUUID.get(id);
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								for (UUID child : children) {
									jVidNodeComponent<?> childNode = compsByUUID.get(child);
									try {
										flowPane.createLinkage(origin, childNode);
									} catch (IncorrectLinkageException e) {
										e.printStackTrace();
									}
								}
							}
						});

					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				throb.dispose();
			}

		});

		loadThread.start();

	}

	public static String dumpConfiguration(JNodeFlowPane flowPane) {
		final Component[] components = flowPane.getComponents();
		final Hashtable<JNodeComponent, HashSet<Linkage>> linkages = flowPane.getLinkages();
		final Hashtable<UUID, Hashtable<UUID, HashSet<UUID>>> matrices = new Hashtable<UUID, Hashtable<UUID, HashSet<UUID>>>();

		StringBuilder exportString = new StringBuilder();

		// Filter out non-jvid related swing components
		for (Component c : components) {
			if (c instanceof jVidNodeComponent) {
				var comp = (jVidNodeComponent) c;
				comp.preSave();
				var desc = comp.getNodeComponentDescriptor();

				exportString.append(comp.getUUID().toString() + "\n\t:" + desc.getContentClass() + "\n\t:" + comp.getX()
						+ "\n\t:" + comp.getY() + "\n\t:");

				Object[] initArgs = desc.getInitArgs();

				if (comp instanceof jVidMatrixNodeComponent) {
					matrices.put(comp.getUUID(), (Hashtable<UUID, HashSet<UUID>>) initArgs[initArgs.length - 1]);
					initArgs = Arrays.copyOf(initArgs, initArgs.length - 1);
				}

				for (Object arg : initArgs) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					try (ObjectOutputStream out = new ObjectOutputStream(bout)) {
						out.writeObject(arg);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					exportString.append(Base64.getEncoder().encodeToString(bout.toByteArray()) + "\n\t:");
				}
			}
			exportString.append("\r");
		}

		for (JNodeComponent node : linkages.keySet()) {
			final UUID origin = node.getUUID();

			exportString.append("LINKAGE\n\t:");
			exportString.append(origin.toString() + "\n\t:");

			final HashSet<Linkage> children = linkages.get(node);
			for (Linkage l : children) {
				exportString.append(l.getNode().getUUID().toString() + "\n\t:");
			}
			exportString.append("\r");
		}

		for (UUID key : matrices.keySet()) {
			exportString.append("MATRIX\n\t:");
			exportString.append(key.toString() + "\n\t:");

			var idMatrix = matrices.get(key);
			for (var source : idMatrix.keySet()) {
				exportString.append(source.toString() + "\n\t:");
				for (var sink : idMatrix.get(source))
					exportString.append(sink.toString() + ",");
				exportString.deleteCharAt(exportString.length() - 1);
				exportString.append("\n\t:");
			}
		}
		return exportString.toString();
	}

	public static void saveConfiguration(JNodeFlowPane flowPane, File configuration) {
		final String exportString = dumpConfiguration(flowPane);
		try (FileOutputStream fout = new FileOutputStream(configuration)) {
			fout.write(exportString.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class MatrixDescriptor {
		private Object[] parameters;
		private int x, y;
		private String contentClass;
	}

}
