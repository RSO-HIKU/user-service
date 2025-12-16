package com.hiku.userService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.hiku.userService.controller.Hello;
import com.hiku.userService.controller.UserController;

import java.util.HashSet;
import java.util.Set;

/**
 * Explicit JAX-RS application class that registers resources and providers.
 * Some JAX-RS runtimes require explicit registration instead of classpath scanning.
 */
@ApplicationPath("/")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Register resource(s)
        classes.add(Hello.class);
        classes.add(UserController.class);
        // Register provider(s)
        classes.add(CORSFilter.class);
        return classes;
    }
}
