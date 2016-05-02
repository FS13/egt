package org.eclipsescout.egt.client.matlab;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import org.eclipse.scout.commons.TypeCastUtility;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipsescout.egt.shared.configuration.MatlabFunctionsLocationParameter;

import Jama.Matrix;

public class MatlabControl {

  //Proxy to communicate with MATLAB
  private final AtomicReference<MatlabProxy> _proxyHolder = new AtomicReference<MatlabProxy>();

//Factory to create proxy
  private final MatlabProxyFactory _factory;

  public MatlabControl() {
    MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
        .setHidden(true)
        .setMatlabLocation("C:\\Program Files\\MATLAB\\R2015b\\bin\\matlab.exe")
    .build();
    _factory = new MatlabProxyFactory(options);

  }

  public void requestProxy() throws ProcessingException {
    MatlabFunctionsLocationParameter mflp = new MatlabFunctionsLocationParameter();
    final String mfl = mflp.getValue();

    //Request a proxy
    try {
      _factory.requestProxy(new MatlabProxyFactory.RequestCallback()
      {
        @Override
        public void proxyCreated(final MatlabProxy proxy)
        {
          _proxyHolder.set(proxy);
          try {
            _proxyHolder.get().eval("cd('" + mfl + "');");
            _proxyHolder.get().eval("clc;");
          }
          catch (MatlabInvocationException e) {
            e.printStackTrace();
          }

          proxy.addDisconnectionListener(new MatlabProxy.DisconnectionListener()
          {
            @Override
            public void proxyDisconnected(MatlabProxy proxy)
            {
              _proxyHolder.set(null);
            }
          });

        }
      });
    }
    catch (MatlabConnectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void disconnectProxy() {
    try {
      _proxyHolder.get().exit();
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void resetSession() {
    try {
      _proxyHolder.get().eval("clear;");
      _proxyHolder.get().eval("clc;");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void createXCorMatrix(String xCorMatrix, int numberOfVertices, int numberOfSpecies) {
    try {
      _proxyHolder.get().eval("" + xCorMatrix + " = createXCorMatrix(" + numberOfVertices + ", " + numberOfSpecies + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void createFitnessMatrix(String fitnessMatrix, String xCorMatrix, List<Double> fitnessList) {
    String fitnessVector = "[";
    for (Double f : fitnessList) {
      fitnessVector += (f + ";");
    }
    fitnessVector += "]";
    try {
      _proxyHolder.get().eval("" + fitnessMatrix + " = createFitnessMatrix(" + xCorMatrix + ", " + fitnessVector + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public int computeNumberOfStates(String xCorMatrix) {
    Object[] solution = null;
    try {
      solution = _proxyHolder.get().returningEval("computeNumberOfStates( " + xCorMatrix + ");", 1);
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return TypeCastUtility.castValue(solution[0], int[].class)[0];
  }

  public void createSquareMatrix(String matrix, int size) {
    createMatrix(matrix, size, size);
  }

  public void createMatrix(String matrix, int rows, int columns) {
    try {
      _proxyHolder.get().eval("" + matrix + " = createMatrix(" + rows + ", " + columns + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void computeEdge(String matrix, int i, int j, double weight, int numberOfSpecies, String xCorMatrix, String fitnessMatrix) {
    try {
      _proxyHolder.get().eval("" + matrix + " = computeEdge(" + i + ", " + j + ", " + weight + ", " + numberOfSpecies + ", " + xCorMatrix + ", " + fitnessMatrix + ", " + matrix + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void createUseVector(String useVector, String xCorMatrix) {
    try {
      _proxyHolder.get().eval("" + useVector + " = createUseVector(" + xCorMatrix + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void createSubMatrixAndVector(String subMatrix, String subVector, String matrix, String useVector) {
    createSubMatrix(subMatrix, matrix, useVector);
    createSubVector(subVector, matrix, useVector);
  }

  public void createSubMatrix(String subMatrix, String matrix, String useVector) {
    try {
      _proxyHolder.get().eval("" + subMatrix + " = createSubMatrix(" + matrix + ", " + useVector + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void createSubVector(String subVector, String matrix, String useVector) {
    try {
      _proxyHolder.get().eval("" + subVector + " = createSubVector(" + matrix + ", " + useVector + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void solveForSub(String subSolVector, String subMatrix, String subVector) {
    try {
      _proxyHolder.get().eval("" + subSolVector + " = solveForSub(" + subMatrix + ", " + subVector + ");");
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public Matrix buildSolutionVector(String subSolVector, String useVector, int length) {
    Object[] solution = null;
    try {
      solution = _proxyHolder.get().returningEval("buildSolutionVector(" + subSolVector + ", " + useVector + ");", 1);
    }
    catch (MatlabInvocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new Matrix(TypeCastUtility.castValue(solution[0], double[].class), length);
  }

}
