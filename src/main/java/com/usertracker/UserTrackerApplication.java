package com.usertracker;

import com.usertracker.domain.UserManager;
import com.usertracker.domain.UserRepository;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import com.usertracker.ports.UserRepositoryImpl;
import com.usertracker.ports.UserTrackerResource;


public class UserTrackerApplication extends Application<UserTrackerConfiguration> {

    private UserRepository repository = new UserRepositoryImpl();
    private UserManager manager = new UserManager(repository);

    public static void main(String[] args) throws Exception {
        new UserTrackerApplication().run(args);
    }

    @Override
    public void run(UserTrackerConfiguration configuration, Environment environment) throws Exception {
        final UserTrackerResource resource = new UserTrackerResource(manager);

        environment.jersey().register(resource);
    }
}
