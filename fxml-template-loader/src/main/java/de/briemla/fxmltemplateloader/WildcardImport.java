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
	protected Class<?> load(String className) throws ClassNotFoundException {
		int indexBeforeWildcard = importQualifier.length() - 1;
		String removedWildcard = importQualifier.substring(0, indexBeforeWildcard);
		String fullQualifiedImport = removedWildcard + className;
		return super.load(fullQualifiedImport);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((importQualifier == null) ? 0 : importQualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WildcardImport other = (WildcardImport) obj;
		if (importQualifier == null) {
			if (other.importQualifier != null)
				return false;
		} else if (!importQualifier.equals(other.importQualifier))
			return false;
		return true;
	}

}
