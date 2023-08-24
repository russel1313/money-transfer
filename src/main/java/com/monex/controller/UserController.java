package com.monex.controller;

import com.google.gson.Gson;
import com.monex.dependencyinjection.daggercomponents.DaggerUserServiceComponent;
import com.monex.dependencyinjection.daggercomponents.UserServiceComponent;
import com.monex.model.User;
import com.monex.response.StandardResponse;
import com.monex.response.StatusResponse;
import com.monex.service.UserService;

import java.util.Objects;

import static spark.Spark.*;

public class UserController {
    UserServiceComponent userServiceComponent = DaggerUserServiceComponent.create();
    UserService userService = userServiceComponent.buildUserService();

    public void registerUserApiRoutes() {
        //get list of all users
       get("/user/all", (req, res) -> {
           return new Gson().toJson(
                   new StandardResponse(StatusResponse.SUCCESS,new Gson()
                           .toJsonTree(userService.getAllUsers())));
       });

       //get user with given id
       get("/user/:id", (req, res) -> {
           String id = Objects.requireNonNull(req.params(":id"));
           return new Gson().toJson(
                   new StandardResponse(StatusResponse.SUCCESS,new Gson()
                           .toJsonTree(userService.getUser(id))));
       });

        //create a new user
        post("/user/:id", (req, res) -> {
            String userId = Objects.requireNonNull(req.params(":id"));
            String firstName = Objects.requireNonNull(req.queryParams("firstName"));
            String lastName = Objects.requireNonNull(req.queryParams("lastName"));
            String email = Objects.requireNonNull(req.queryParams("email"));
            User user = User.builder().withId(userId).withFirstName(firstName).withLastName(lastName)
                            .withEmail(email).build();
            userService.createUser(user);
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,"New user has been created"));
        });

        //edit a particular user
        put("/user/:id", (req, res) -> {
            String userId = Objects.requireNonNull(req.params(":id"));
            String firstName = Objects.requireNonNull(req.queryParams("firstName"));
            String lastName = Objects.requireNonNull(req.queryParams("lastName"));
            String email = Objects.requireNonNull(req.queryParams("email"));
            User user = User.builder().withId(userId).withFirstName(firstName).withLastName(lastName)
                    .withEmail(email).build();
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,new Gson()
                            .toJsonTree(userService.updateUser(user))));
        });

        //check whether a user exists with given id
       options("/user/:id", (req, res) -> {
           String userId = Objects.requireNonNull(req.params(":id"));
           return new Gson().toJson(
                   new StandardResponse(StatusResponse.SUCCESS,new Gson()
                           .toJsonTree(userService.userExist(userId))));
       });

       //delete a particular user
       delete("/user/:id", (req, res) -> {
           String userId = Objects.requireNonNull(req.params(":id"));
           userService.deleteUser(userId);
           return new Gson().toJson(
                   new StandardResponse(StatusResponse.SUCCESS,"User with %s id has been deleted", userId));
       });

    }
}
