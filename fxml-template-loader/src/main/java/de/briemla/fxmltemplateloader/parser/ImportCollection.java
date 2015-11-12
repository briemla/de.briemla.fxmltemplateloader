package de.briemla.fxmltemplateloader.parser;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.events.ProcessingInstruction;

import javafx.fxml.LoadException;

public class ImportCollection {

    private static final String IMPORT = "import";

    private final ArrayList<Import> imports;
    private final ImportFactory factory;

    /**
     * Collection to manage {@link Import}s of FXML files.
     *
     * @param factory
     *            to create {@link Import}s from {@link String}s
     */
    public ImportCollection(ImportFactory factory) {
        super();
        this.factory = factory;
        imports = new ArrayList<>();
    }

    /**
     * Return class for given class name if it exists in the {@link Collection}.
     *
     * @param className
     *            to search the {@link Class} for
     * @return {@link Class} for given className
     * @throws LoadException
     *             if no matching {@link Class} can be loaded
     */
    public Class<?> findClass(String className) throws LoadException {
        try {
            for (Import importQualifier : imports) {
                if (importQualifier.matches(className)) {
                    return importQualifier.load(className);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new LoadException(e);
        }
        throw new LoadException("No matching import available for class with name: " + className);
    }

    /**
     * Add {@link ProcessingInstruction} from XML if the {@link ProcessingInstruction} is a FXML
     * import.
     *
     * @param instruction
     *            possible import to add
     */
    public void add(ProcessingInstruction instruction) {
        if (!IMPORT.equals(instruction.getTarget())) {
            return;
        }
        String importClassifier = instruction.getData();
        imports.add(factory.create(importClassifier));
    }

    public void clear() {
        imports.clear();
    }
}
