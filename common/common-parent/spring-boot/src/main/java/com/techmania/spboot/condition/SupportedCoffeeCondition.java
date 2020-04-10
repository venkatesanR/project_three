package com.techmania.spboot.condition;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Override condition on load and intimate app
 * Consumer with nice error message :)
 */
class SupportedCoffeeCondition extends SpringBootCondition {
    private static final String COFFEE_NAME = "coffee.name";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        ConditionMessage.Builder validCoffee = ConditionMessage.forCondition("validCoffee");
        Environment environment = context.getEnvironment();
        String coffee = environment.getProperty(COFFEE_NAME);

        if (validCoffee(coffee)) {
            return ConditionOutcome.match(validCoffee
                    .available(String.format("Found matching coffee configuration: %s", coffee)));
        }

        return ConditionOutcome.noMatch(validCoffee.because(String.format("Invalid cofee [%s] from config please fix " +
                "it", coffee)));
    }

    private static boolean validCoffee(String coffee) {
        return coffee != null && !coffee.equalsIgnoreCase("Hella");
    }

}