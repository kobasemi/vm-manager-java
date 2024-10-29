package net.asaken1021.vmmanager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flak.App;
import flak.Flak;
import flak.annotations.Route;
import flak.jackson.JSON;

import net.asaken1021.vmmanager.util.ConnectException;
import net.asaken1021.vmmanager.util.DomainLookupException;
import net.asaken1021.vmmanager.util.VMManager;

public class WebApiApp {
    private VMManager vmm;
    private String uri;

    private App webApp;

    public WebApiApp(String uri) {
        this.uri = uri;
        try {
            this.vmm = new VMManager(this.uri);
        } catch (ConnectException e) {
            printError(e.getLocalizedMessage());
        }
    }

    public void run() {
        try {
            this.webApp = Flak.createHttpApp(8080);
            webApp.scan(new WebApiApp(this.uri));
            webApp.start();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | IOException e) {
            printError(e.getLocalizedMessage());
        }
    }

    private void printError(String message) {
        System.err.println("エラー: " + message);
    }
    
    @Route("/")
    public String index() {
        return "index";
    }

    @Route("/vms")
    @JSON
    public List<String> getVms() {
        List<String> names = new ArrayList<String>();
        try {
            for (String name : this.vmm.getVmNames()) {
                names.add(name);
            }
            return names;
        } catch (DomainLookupException e) {
            names.add(e.getLocalizedMessage());
            return names;
        }
    }
}
