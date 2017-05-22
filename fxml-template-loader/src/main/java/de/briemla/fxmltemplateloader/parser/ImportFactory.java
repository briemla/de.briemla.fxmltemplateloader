package de.briemla.fxmltemplateloader.parser;

public class ImportFactory {

    private static final String WILDCARD_MATCH = ".*";
    private ClassLoader classLoader;

    public ImportFactory(ClassLoader classLoader) {
        super();
        this.classLoader = classLoader;
    }

    Import create(String importClassifier) {
        String trimmedClassifier = importClassifier.trim();
        if (trimmedClassifier.endsWith(WILDCARD_MATCH)) {
            return new WildcardImport(trimmedClassifier, classLoader);
        }
        return new FullQualifiedImport(trimmedClassifier, classLoader);
    }

    public DeclaredClass createClass(String className) {
    	return new DeclaredClass(className, classLoader);
    }
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean hasClassLoader() {
        return classLoader != null;
    }

	public boolean canLoad(String fullQualifiedClass) {
		if (hasClassLoader()) {
			return classLoaderKnows(fullQualifiedClass);
		}
		return false;
	}

	private boolean classLoaderKnows(String fullQualifiedClass) {
		try {
			return null != classLoader.loadClass(fullQualifiedClass);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
