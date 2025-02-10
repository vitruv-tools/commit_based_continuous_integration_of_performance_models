package org.splevo.tests.statements;

/**
 * Class with methods containing different throw variations.
 */
public class ThrowStatements {

    public static void similarThrow(){
        throw new IllegalArgumentException();
    }
    
    public static void similarThrowWithParameter(){
        throw new IllegalArgumentException("Error");
    }
    
    public static void changedThrow(){
        throw new IllegalArgumentException();
    }
    
    public static void changedThrowWithParameter(){
        throw new IllegalArgumentException("Error");
    }
    
    public static void throwWithChangedParameter(){
        throw new IllegalArgumentException("Error");
    }
    
}
