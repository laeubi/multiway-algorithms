package nz.ac.waikato.cms.adams.multiway.algorithm.regression;

import nz.ac.waikato.cms.adams.multiway.algorithm.MultiLinearPLS;
import nz.ac.waikato.cms.adams.multiway.algorithm.SONPLS;
import nz.ac.waikato.cms.adams.multiway.data.DataReader;
import nz.ac.waikato.cms.adams.multiway.data.tensor.Tensor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SONPLSRegressionTestManager extends MultiBlockSupervisedRegressionTestManager<SONPLS, Map<String, Tensor>> {

  @Override
  public boolean resultEqualsReference() throws IOException {
    Map<String, Tensor> result = algorithm.getLoadingMatrices();
    Tensor[] data = getRegressionTestData();

    result.put("Yhat", algorithm.predict(new Tensor[]{data[0], data[1]}));

    Map<String, Tensor> reference = loadReference();
    if (!result.keySet().equals(reference.keySet())){
      return false;
    }

    for (String key : result.keySet()){
      if (!result.get(key).equalsWithEps(reference.get(key), 10e-7)) {
	return false;
      }
    }
    return true;
  }

  @Override
  public void saveNewReference() throws IOException {
    Map<String, Tensor> ref = algorithm.getLoadingMatrices();
    for (String s : ref.keySet()){
      DataReader.writeMatrixCsv(ref.get(s).toArray2d(), getReferenceFilePath(s), ",");
    }

    Tensor[] data = getRegressionTestData();
    Tensor Yhat = algorithm.predict(new Tensor[]{data[0], data[1]});

    DataReader.writeMatrixCsv(Yhat.toArray2d(), getReferenceFilePath("Yhat"), ",");
  }

  @Override
  public String getRegressionReferenceDirectory() {
    return super.getRegressionReferenceDirectory() + "/sonpls/" + options;
  }
  @Override
  public Map<String, Tensor> loadReference() throws IOException {
    Map<String, Tensor> ref = new HashMap<>();
    for (String s : algorithm.getLoadingMatrices().keySet()){
      ref.put(s, Tensor.create(DataReader.readMatrixCsv(getReferenceFilePath(s),",")));
    }

    ref.put("Yhat", Tensor.create(DataReader.readMatrixCsv(getReferenceFilePath("Yhat"), ",")));
    return ref;
  }
}