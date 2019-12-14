// The implementation was tested only for regression and binary classification.
// For polynomial classification, we would have to use getMultinominalCoefficients()...
// The code should be pasted into "Execute Script" operator.

import com.rapidminer.tools.Ontology;
import com.rapidminer.h2o.model.GeneralizedLinearModel;

        // Read the model
        GeneralizedLinearModel model = input[0];

        // Validation
        if (model.isMultinomialModel()) operator.getLogger().error("Multinomial models are not supported");

        // Construct attribute set
        Attribute[] attributes = new Attribute[3];
        attributes[0] = AttributeFactory.createAttribute("FEATURE", Ontology.STRING);
        attributes[1] = AttributeFactory.createAttribute("COEFFICIENT", Ontology.REAL);
        attributes[2] = AttributeFactory.createAttribute("STANDARDIZED_COEFFICIENT", Ontology.REAL);
        
        MemoryExampleTable table = new MemoryExampleTable(attributes);
        DataRowFactory ROW_FACTORY = new DataRowFactory(0);

        String[] strings = new String[3];
        String[] names = model.getCoefficientNames();
        double[] coefficients = model.getCoefficients();
        double[] stdCoefficients = model.getStdCoefficients();

        for (int i = 0; i < names.length; i++) {
        	if (coefficients[i] != 0.0) {
            strings[0] = names[i].replace(".", " = ");
            strings[1] = Double.toString(coefficients[i]);
            strings[2] = Double.toString(stdCoefficients[i]);

            // make and add row
            DataRow row = ROW_FACTORY.create(strings, attributes);
            table.addDataRow(row);
        	}
        }
        
        ExampleSet exampleSet = table.createExampleSet();
        return exampleSet;
