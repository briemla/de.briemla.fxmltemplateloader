package de.briemla.fxmltemplateloader.parser;

class WildcardImport extends Import {

    private final String importQualifier;

    WildcardImport(String importQualifier, ClassLoader classLoader) {
        super(classLoader);
        this.importQualifier = importQualifier;
    }

    @Override
    boolean matches(String className) {
        if (className == null) {
            return false;
        }
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
        int result = super.hashCode();
        result = prime * result + ((importQualifier == null) ? 0 : importQualifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WildcardImport other = (WildcardImport) obj;
        if (importQualifier == null) {
            if (other.importQualifier != null) {
                return false;
            }
        } else if (!importQualifier.equals(other.importQualifier)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WildcardImport [importQualifier=" + importQualifier + "]";
    }

}
