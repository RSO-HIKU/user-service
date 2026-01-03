package com.hiku.userService;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

import com.hiku.userService.controller.Hello;
import com.hiku.userService.controller.UserController;

import java.util.HashSet;
import java.util.Set;

/**
 * Explicit JAX-RS application class that registers resources and providers.
 * Some JAX-RS runtimes require explicit registration instead of classpath scanning.
 */
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"user", "admin"})
@ApplicationPath("/api/user")
public class UserService extends Application {


}
