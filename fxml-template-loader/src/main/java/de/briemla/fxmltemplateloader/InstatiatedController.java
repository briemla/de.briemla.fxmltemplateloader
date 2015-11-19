package de.briemla.fxmltemplateloader;

import de.briemla.fxmltemplateloader.template.Controller;

public class InstatiatedController implements Controller {

    private Object controller;

    public InstatiatedController(Object controller) {
        this.controller = controller;
    }

    @Override
    public Object instance() {
        return controller;
    }

}
