package de.briemla.fxmltemplateloader;

abstract class Import {

	private final ClassLoader classLoader;

	public Import(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
	}

	abstract boolean matches(String className);

	protected Class<?> load(String className) throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classLoader == null) ? 0 : classLoader.hashCode());
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
		Import other = (Import) obj;
		if (classLoader == null) {
			if (other.classLoader != null)
				return false;
		} else if (!classLoader.equals(other.classLoader))
			return false;
		return true;
	}

}
