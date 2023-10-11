package fr.atlasworld.network.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.atlasworld.network.api.AtlasNetwork;
import fr.atlasworld.network.api.event.components.CancellableEvent;
import fr.atlasworld.network.api.event.components.Event;
import fr.atlasworld.network.api.event.components.EventListener;
import fr.atlasworld.network.api.event.components.Listener;
import fr.atlasworld.network.api.exception.module.ModuleException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidClassException;
import fr.atlasworld.network.api.exception.module.ModuleInvalidException;
import fr.atlasworld.network.api.logging.LogUtils;
import fr.atlasworld.network.api.module.Module;
import fr.atlasworld.network.api.module.ModuleManager;
import fr.atlasworld.network.api.module.NetworkModule;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ModuleHandler implements ModuleManager {
    private final List<ModuleInfo> loadedModules;
    private final Map<Class<? extends Event>, List<Map.Entry<Map.Entry<EventListener, Method>, Listener>>> registeredListeners;


    public ModuleHandler(List<ModuleInfo> loadedModules, Map<Class<? extends Event>, List<Map.Entry<Map.Entry<EventListener, Method>, Listener>>> registeredListeners) {
        this.loadedModules = loadedModules;
        this.registeredListeners = registeredListeners;
    }

    public ModuleHandler() {
        this(new ArrayList<>(), new HashMap<>());
    }

    @Override
    public Collection<Module> getLoadedModules() {
        return Collections.unmodifiableCollection(this.loadedModules);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerListener(EventListener listener, Module module) {
        Class<? extends EventListener> listenerClass = listener.getClass();

        for (Method method : listenerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Listener.class)) {
                Listener annotation = method.getAnnotation(Listener.class);

                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 1) {
                    throw new IllegalArgumentException("Method '" + method.getName() + "' marked with @Listener from '" + listenerClass.getName() + "' may only take one parameter!");
                }

                if (!Event.class.isAssignableFrom(parameters[0])) {
                    throw new IllegalArgumentException("Method '" + method.getName() + "' marked with @Listener from '" + listenerClass.getName() + "' may only take events as parameter!");
                }

                Class<? extends Event> eventParam = (Class<? extends Event>) parameters[0];

                if (!this.registeredListeners.containsKey(eventParam)) {
                    this.registeredListeners.put(eventParam, new ArrayList<>());
                }

                this.registeredListeners.get(eventParam).add(Map.entry(Map.entry(listener, method), annotation));
            }
        }
    }

    @Override
    public void callEvent(Event event) {
        Class<? extends Event> eventType = event.getClass();
        if (this.registeredListeners.containsKey(eventType)) {
            List<Map.Entry<Map.Entry<EventListener, Method>, Listener>> sortedListeners = this.registeredListeners
                    .get(eventType)
                    .stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().priority()))
                    .toList();

            for (Map.Entry<Map.Entry<EventListener, Method>, Listener> entry : sortedListeners) {
                EventListener listener = entry.getKey().getKey();
                Method method = entry.getKey().getValue();
                Listener annotation = entry.getValue();

                if (event instanceof CancellableEvent cancellableEvent) {
                    if (annotation.ignoreIfCancelled() && cancellableEvent.cancelled()) {
                        continue;
                    }
                }

                try {
                    method.setAccessible(true);
                    method.invoke(listener, event);
                } catch (Exception e) {
                    LogUtils.getServerLogger().error("Could not pass event '{}' to '{}'", eventType.getSimpleName(),
                            method.getDeclaringClass().getSimpleName(), e);
                }
            }
        }
    }

    @Override
    public CompletableFuture<Event> callEventAsync(Event event) {
        return CompletableFuture.supplyAsync(() -> {
            this.callEvent(event);
            return event;
        });
    }


    /**
     * Load a module from a file
     * @param moduleFile module file
     * @return the loaded module
     * @throws ModuleException if the module could not be loaded, usually caused by the jar not being valid
     */
    @ApiStatus.Internal
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

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public URLClassLoader loadModulesToClasspath(File... moduleFiles) throws ModuleException {
        URL[] urls = new URL[moduleFiles.length];
        for (int i = 0; i < moduleFiles.length; i++) {
            try {
                urls[i] = moduleFiles[i].toURI().toURL();
            } catch (MalformedURLException e) {
                throw new ModuleException("Could not load '" + moduleFiles[i].getName() + "': ", e);
            }
        }

        URLClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

        try {
            Class<?> classLoaderClass = ClassLoader.class;
            Field classesField = classLoaderClass.getDeclaredField("classes");
            classesField.setAccessible(true);

            Vector<Class<?>> classesVector = (Vector<Class<?>>) classesField.get(loader);
            for (Class<?> clazz : classesVector) {
                loader.loadClass(clazz.getName());
            }
        }  catch (Exception e) {
            throw new ModuleException("Unable to load classes", e);
        }

        Thread.currentThread().setContextClassLoader(loader);
        return loader;
    }

    @ApiStatus.Internal
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
        } catch (InstantiationException e) {
            throw new ModuleInvalidClassException("Main class of '" + info.getName() + "' must not be an interface or abstract class!");
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ModuleInvalidClassException("Main class of '" + info.getName() + "' must have a public constructor and must not take any parameters!");
        } catch (IllegalArgumentException e) {
            throw new ModuleInvalidClassException("Main class of '" + info.getName() + "' must not take any constructor parameters!");
        } catch (Exception e) {
            throw new ModuleException("Failed to initialize '" + info.getName() + "': ", e);
        }
    }
}
