package de.briemla.fxmltemplateloader.template;

interface ExistingRoot {

    void setRoot(Object fxRoot);

    /**
     * Returns the controller for this element. Returns <code>null</code> if no controller is
     * specified.
     *
     * @return controller instance or <code>null</code> if there is not controller
     */
    <T> T getController();

}