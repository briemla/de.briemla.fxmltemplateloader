package de.briemla.fxmltemplateloader.parser;

class FullQualifiedImport extends Import {

    private final String importQualifier;

    FullQualifiedImport(String importQualifier, ClassLoader classLoader) {
        super(classLoader);
        this.importQualifier = importQualifier;
    }

    @Override
    boolean matches(String className) {
        if (className == null) {
            return false;
        }
        return importQualifier.endsWith(className);
    }

    @Override
    protected Class<?> load(String className) throws ClassNotFoundException {
        return super.load(importQualifier);
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
        FullQualifiedImport other = (FullQualifiedImport) obj;
        if (importQualifier == null) {
            if (other.importQualifier != null) {
                return false;
            }
        } else if (!importQualifier.equals(other.importQualifier)) {
            return false;
        }
        return true;
    }

}
