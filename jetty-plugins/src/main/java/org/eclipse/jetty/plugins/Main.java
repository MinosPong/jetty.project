//
//  ========================================================================
//  Copyright (c) 1995-2012 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.plugins;

import java.util.Map;
import java.util.Set;

public class Main
{
    private static final String JETTY_HOME = "JETTY_HOME";

    private MavenService _mavenService = new HttpMavenService();
    private PluginManager _pluginManager;
    private String _jettyHome;
    private String _installPlugin;
    private boolean _listPlugins;
    private String _repositoryUrl;
    private String _groupId;
    private String _version;
    private boolean _searchRemoteRepository = true;
    private boolean _searchLocalRepository = false;

    public static void main(String[] args)
    {
        Main main = new Main();
        main.execute(args);
    }

    private void execute(String[] args)
    {
        parseEnvironmentVariables();
        parseCommandline(args);
        configureMavenService();

        _pluginManager = new DefaultPluginManager(_mavenService, _jettyHome);

        if (_listPlugins)
            listPlugins();
        else if (_installPlugin != null)
            installPlugin();
    }

    private void configureMavenService()
    {
        if (_repositoryUrl != null)
            _mavenService.setRepositoryUrl(_repositoryUrl);
        if (_groupId != null)
            _mavenService.setGroupId(_groupId);
        if (_version != null)
            _mavenService.setVersion(_version);

        _mavenService.setSearchLocalRepository(_searchLocalRepository);
        _mavenService.setSearchRemoteRepository(_searchRemoteRepository);
    }

    private void listPlugins()
    {
        Set<String> availablePlugins = _pluginManager.listAvailablePlugins();
        for (String pluginName : availablePlugins)
            System.out.println(pluginName);
    }

    private void installPlugin()
    {
        _pluginManager.installPlugin(_installPlugin);
        System.out.println("Successfully installed plugin: " + _installPlugin
                + " to " + _jettyHome);
    }

    private void parseEnvironmentVariables()
    {
        Map<String, String> env = System.getenv();
        if (env.containsKey(JETTY_HOME))
            _jettyHome = env.get(JETTY_HOME);
    }

    private void parseCommandline(String[] args)
    {
        int i = 0;
        for (String arg : args)
        {
            i++;

            if (arg.startsWith("--jettyHome="))
                _jettyHome = arg.substring(12);
            if (arg.startsWith("--repositoryUrl="))
                _repositoryUrl = arg.substring(16);
            if (arg.startsWith("--groupId="))
                _groupId = arg.substring(10);
            if (arg.startsWith("--version="))
                _version = arg.substring(10);
            if (arg.startsWith("--useLocalRepo="))
                _searchLocalRepository = Boolean.valueOf(arg.substring(15));
            if (arg.startsWith("--useRemoteRepo="))
                _searchRemoteRepository = Boolean.valueOf(arg.substring(15));
            if (arg.startsWith("install"))
                _installPlugin = args[i];
            if ("list".equals(arg))
                _listPlugins = true;
        }

        if (_jettyHome == null && _installPlugin != null)
            printUsage("No --jettyHome commandline option specified and no \"JETTY_HOME\" environment variable found!");
        if (_installPlugin == null && !_listPlugins)
            printUsage("Neither install <pluginname> nor list commandline option specified. Nothing to do for me!");
        if (_installPlugin != null && _listPlugins)
            printUsage("Please specify either install <pluginname> or list commandline options, " +
                    "but not both at the same time!");
    }

    private void printUsage(String errorMessage)
    {
        System.out.println(errorMessage);
        System.exit(1);
        //TODO: print usage
    }
}
