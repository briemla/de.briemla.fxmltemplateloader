package de.briemla.fxmltemplateloader;

public final class InitializableControllerClass {

	private boolean initiliazed;
	
	public InitializableControllerClass() {
		super();
		initiliazed = false;
	}

	public void initialize() {
		initiliazed = true;
	}

	public boolean isInitialized() {
		return initiliazed;
	}
	
}
