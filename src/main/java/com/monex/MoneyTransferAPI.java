package com.monex;

import com.google.gson.Gson;
import com.monex.controller.AccountController;
import com.monex.controller.UserController;
import com.monex.exception.*;
import com.monex.response.StandardResponse;
import com.monex.response.StatusResponse;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.before;
import static spark.Spark.exception;

public class MoneyTransferAPI {
    public static void main(String[] args) {
        startSparkApplication();
    }

    private static void startSparkApplication() {
        setExceptionHandlers();
        setResponseType();
        UserController userController = new UserController();
        userController.registerUserApiRoutes();
        AccountController accountController = new AccountController();
        accountController.registerAccountApiRoutes();
    }

    private static void setResponseType() {
        before((req, res) -> res.type("application/json"));
    }

    private static void setExceptionHandlers() {
        exception(NotFoundException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, e.getMessage())));
        });
        exception(AlreadyExistException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, e.getMessage())));
        });
        exception(NotSufficientBalanceException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, e.getMessage())));
        });
        exception(SameAccountException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, e.getMessage())));
        });
        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, "Illegal arguments provided in request")));
        });
        exception(NullPointerException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, "Insufficient arguments provided")));
        });
        exception(InvalidAmountException.class, (e, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, e.getMessage())));
        });
        exception(Exception.class, (e, req, res) -> {
            res.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            res.body(new Gson().toJson(
                    new StandardResponse(StatusResponse.ERROR, "The system did not respond properly")));
        });
    }
}
