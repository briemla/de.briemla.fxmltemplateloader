package de.briemla.fxmltemplateloader;

public class WildcardImport extends Import {

	private final String importQualifier;

	public WildcardImport(String importQualifier) {
		super();
		this.importQualifier = importQualifier;
	}

	@Override
	boolean matches(String className) {
		try {
			return load(className) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	protected boolean isWildcard() {
		return true;
	}

	@Override
	protected Class<?> load(String className) throws ClassNotFoundException {
		int indexBeforeWildcard = importQualifier.length() - 1;
		String removedWildcard = importQualifier.substring(0, indexBeforeWildcard);
		String fullQualifiedImport = removedWildcard + className;
		return super.load(fullQualifiedImport);
	}

}
