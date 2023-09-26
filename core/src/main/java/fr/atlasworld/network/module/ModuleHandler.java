package fr.atlasworld.network.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.atlasworld.network.api.AtlasNetwork;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidClassException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidException;
import fr.atlasworld.network.api.module.Module;
import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.module.NetworkModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleHandler implements ModuleManager {
    private final List<ModuleInfo> loadedModules;

    public ModuleHandler(List<ModuleInfo> loadedModules) {
        this.loadedModules = loadedModules;
    }

    public ModuleHandler() {
        this(new ArrayList<>());
    }

    @Override
    public Collection<Module> getLoadedModules() {
        return Collections.unmodifiableCollection(this.loadedModules);
    }

    /**
     * Load a module from a file
     * @param moduleFile module file
     * @return the loaded module
     * @throws ModuleException if the module could not be loaded, usually caused by the jar not being valid
     */
    public ModuleInfo loadModule(File moduleFile) throws ModuleException {
        try (JarFile jarFile = new JarFile(moduleFile)) {
            JarEntry jsonEntry = jarFile.getJarEntry("module.json");
            if (jsonEntry == null) {
                throw new ModuleInvalidException(moduleFile.getName() + " is not a module! Missing 'module.json' file!");
            }

            try (InputStream inputStream = jarFile.getInputStream(jsonEntry)) {
                JsonElement json = JsonParser.parseReader(new InputStreamReader(inputStream));
                ModuleInfo moduleInfo = ModuleInfo.createFromJson(json.getAsJsonObject(), moduleFile);

                this.loadedModules.add(moduleInfo);
                return moduleInfo;
            } catch (IllegalArgumentException e) {
                throw new ModuleInvalidException(moduleFile.getName() + ": " + e.getMessage());
            } catch (IllegalStateException e) {
                throw new ModuleInvalidException("module.json has an invalid format!");
            }
        } catch (IOException e) {
            throw new ModuleException("Unable to load " + moduleFile.getName(), e);
        }
    }

    public URLClassLoader loadModulesToClasspath(File... moduleFiles) throws ModuleException {
        URL[] urls = new URL[moduleFiles.length];
        for (int i = 0; i < moduleFiles.length; i++) {
            try {
                urls[i] = moduleFiles[i].toURI().toURL();
            } catch (MalformedURLException e) {
                throw new ModuleException("Could not load '" + moduleFiles[i].getName() + "': ", e);
            }
        }

        return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
    }

    public void enableModule(ModuleInfo info, ClassLoader loader) throws ModuleException {
        try {
            Class<?> moduleMainClass = Class.forName(info.getMainClass(), true, loader);

            if (!NetworkModule.class.isAssignableFrom(moduleMainClass)) {
                throw new ModuleInvalidClassException("Main class '" + moduleMainClass.getName() + "' of '" + info.getName() + "' does not extend NetworkModule!");
            }

            NetworkModule module = (NetworkModule) moduleMainClass.getDeclaredConstructor().newInstance();
            module.startModule(AtlasNetwork.getServer(), info);
        } catch (ClassNotFoundException e) {
            throw new ModuleInvalidClassException("Could not find '" + info.getName() + "' main class: " + info.getMainClass());
        } catch (InvocationTargetException e) {
            throw new ModuleException("Failed to initialize '" + info.getName() + "': ", e);
        } catch (InstantiationException e) {
            throw new ModuleInvalidClassException("Main class of '" + info.getName() + "' must not be an interface or abstract class!");
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ModuleInvalidClassException("Main class of '" + info.getName() + "' must have a public constructor and must not take any parameters!");
        } catch (IllegalArgumentException e) {
            throw new ModuleInvalidClassException("Main class of '" + info.getName() + "' must not take any constructor parameters!");
        }
    }
}
