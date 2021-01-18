package com.chemaxon.calculations.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Help debugging lambda deployments.
 * 
 * @author Gabor Imre
 */
public class Diag implements RequestHandler<String, Map<String, String>> {

    @Override
    public Map<String, String> handleRequest(String input, Context context) {
        
        try {
            return ImmutableMap.of(
                "input", input,
                "env", System.getenv().toString(),
                "props", System.getProperties().toString()
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        
    }
    
    
    
}
