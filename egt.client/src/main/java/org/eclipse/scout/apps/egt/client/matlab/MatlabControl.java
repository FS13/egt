package org.eclipse.scout.apps.egt.client.matlab;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.scout.apps.egt.shared.matlab.EgtMatlabProperties.EgtMatlabFilesLocationProperty;
import org.eclipse.scout.apps.egt.shared.matlab.EgtMatlabProperties.EgtMatlabInstallationLocationProperty;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;

import Jama.Matrix;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

public final class MatlabControl {

	// Proxy to communicate with MATLAB
	private final static AtomicReference<MatlabProxy> m_proxyHolder = new AtomicReference<MatlabProxy>();

	public MatlabControl() {

	}

	public static void requestProxy() throws ProcessingException {
		final String mfl = CONFIG.getPropertyValue(EgtMatlabFilesLocationProperty.class);

		final String mil = CONFIG.getPropertyValue(EgtMatlabInstallationLocationProperty.class);
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
				.setHidden(true)
				.setMatlabLocation(mil)
				.build();
		MatlabProxyFactory factory = new MatlabProxyFactory(options);

		// Request a proxy
		try {
			factory.requestProxy(new MatlabProxyFactory.RequestCallback() {
				@Override
				public void proxyCreated(final MatlabProxy proxy) {
					m_proxyHolder.set(proxy);
					try {
						m_proxyHolder.get().eval("cd('" + mfl + "');");
						m_proxyHolder.get().eval("clc;");
					} catch (MatlabInvocationException e) {
						e.printStackTrace();
					}

					proxy.addDisconnectionListener(new MatlabProxy.DisconnectionListener() {
						@Override
						public void proxyDisconnected(MatlabProxy proxy) {
							m_proxyHolder.set(null);
						}
					});

				}
			});
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void disconnectProxy() {
		try {
			m_proxyHolder.get().exit();
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void resetSession() {
		try {
			m_proxyHolder.get().eval("clear;");
			m_proxyHolder.get().eval("clc;");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createXCorMatrix(String xCorMatrix, int numberOfVertices, int numberOfSpecies) {
		try {
			m_proxyHolder.get().eval("" + xCorMatrix + " = createXCorMatrix(" + numberOfVertices + ", " + numberOfSpecies + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createFitnessMatrix(String fitnessMatrix, String xCorMatrix, List<Double> fitnessList) {
		String fitnessVector = "[";
		for (Double f : fitnessList) {
			fitnessVector += (f + ";");
		}
		fitnessVector += "]";
		try {
			m_proxyHolder.get().eval("" + fitnessMatrix + " = createFitnessMatrix(" + xCorMatrix + ", " + fitnessVector + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int computeNumberOfStates(String xCorMatrix) {
		Object[] solution = null;
		try {
			solution = m_proxyHolder.get().returningEval("computeNumberOfStates( " + xCorMatrix + ");", 1);
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TypeCastUtility.castValue(solution[0], int[].class)[0];
	}

	public static void createSquareMatrix(String matrix, int size) {
		createMatrix(matrix, size, size);
	}

	public static void createMatrix(String matrix, int rows, int columns) {
		try {
			m_proxyHolder.get().eval("" + matrix + " = createMatrix(" + rows + ", " + columns + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void computeEdge(String matrix, int i, int j, double weight, int numberOfSpecies, String xCorMatrix, String fitnessMatrix) {
		try {
			m_proxyHolder.get().eval("" + matrix + " = computeEdge(" + i + ", " + j + ", " + weight + ", " + numberOfSpecies + ", " + xCorMatrix + ", " + fitnessMatrix + ", " + matrix + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createUseVector(String useVector, String xCorMatrix) {
		try {
			m_proxyHolder.get().eval("" + useVector + " = createUseVector(" + xCorMatrix + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createSubMatrixAndVector(String subMatrix, String subVector, String matrix, String useVector) {
		createSubMatrix(subMatrix, matrix, useVector);
		createSubVector(subVector, matrix, useVector);
	}

	public static void createSubMatrix(String subMatrix, String matrix, String useVector) {
		try {
			m_proxyHolder.get().eval("" + subMatrix + " = createSubMatrix(" + matrix + ", " + useVector + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createSubVector(String subVector, String matrix, String useVector) {
		try {
			m_proxyHolder.get().eval("" + subVector + " = createSubVector(" + matrix + ", " + useVector + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void solveForSub(String subSolVector, String subMatrix, String subVector) {
		try {
			m_proxyHolder.get().eval("" + subSolVector + " = solveForSub(" + subMatrix + ", " + subVector + ");");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Matrix buildSolutionVector(String subSolVector, String useVector, int length) {
		Object[] solution = null;
		try {
			solution = m_proxyHolder.get().returningEval("buildSolutionVector(" + subSolVector + ", " + useVector + ");", 1);
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Matrix(TypeCastUtility.castValue(solution[0], double[].class), length);
	}

}
