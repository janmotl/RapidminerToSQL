// Converts the GLM weights from RapidMiner into SQL.
// Only regression and binary classification are supported.
// Presence of the intercept is presumed.
// The code should be put into "Execute Script" operator.
            
import com.rapidminer.h2o.model.GeneralizedLinearModel;
import com.rapidminer.operator.SimpleResultObject;
import java.util.stream.IntStream;

         // Setting
        String separator = " = "; // Separator for nominal features
        String quoteEntity = "\"";
        String quoteString = "'";
        String indentationWeighs = "\t\t\t\t";
        String indentationFormula = "\t\t\t";

        // Data
        GeneralizedLinearModel model = input[0];
        String[] names_raw = model.getCoefficientNames();
        double[] coefficients_raw = model.getCoefficients();

        // Sort by name, filter away zero weights, replace dot separator with equality sign, cast the weights to string
        TreeMap<String, String> map = new TreeMap<>();
        for (int i = 0; i < names_raw.length; i++) {
            if (coefficients_raw[i] != 0) {
                map.put(names_raw[i].replace(".", " = "), Double.toString(coefficients_raw[i]));
            }
        }
        String[] names = map.keySet().toArray(new String[0]);
        String[] coefficients = map.values().toArray(new String[0]);

        // Loop
        String linearFormula = "";
        String subselect = "";
        for (int row = 0; row < names.length; row++) {
            String featureName = names[row];
            String featureWeight = coefficients[row];
            if ("Intercept".equals(featureName)) {
                linearFormula = featureWeight + linearFormula;
            } else {
                if (featureName.contains(separator)) {
                    String[] split = featureName.split(separator, 2);
                    String attributeName = split[0];
                    String valueName = split[1];
                    subselect = subselect + indentationWeighs + ", case when " + quoteEntity + attributeName + quoteEntity + " = " + quoteString + valueName + quoteString + " then " + featureWeight + " else 0 end as " + quoteEntity + featureName + quoteEntity + "\n";
                } else {
                    subselect = subselect + indentationWeighs + ", " + featureWeight + " * " + quoteEntity + featureName + quoteEntity + " as " + quoteEntity + featureName + quoteEntity + "\n";
                }
                linearFormula = linearFormula + " + " + quoteEntity + featureName + quoteEntity;
            }
        }

        // Output
        String logisticFormula = indentationFormula + "1/(1 + exp(-(" + linearFormula + ")))";
        linearFormula = indentationFormula + linearFormula;
        SimpleResultObject result;
        if ("binomial".equals(model.glmScore._m._parms._family)) {
            result = new SimpleResultObject("SQL", logisticFormula + "\n\n" + subselect);
        } else {
            result = new SimpleResultObject("SQL", linearFormula + "\n\n" + subselect);
        }
        
        return result;

